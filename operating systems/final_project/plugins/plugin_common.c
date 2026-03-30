#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>

#include "plugin_common.h"
#include "sync/consumer_producer.h"

// One context per .so 
static plugin_context_t g_ctx;

static void* consumer_thread_main(void* arg) {
    plugin_context_t* ctx = (plugin_context_t*)arg;

    for (;;) {
        char* item = consumer_producer_get(&ctx->queue);  // blocking pop
        if (item == NULL) {
            // Queue finished & empty, exit 
            break;
        }

        if (strcmp(item, SENTINEL_STR) == 0) {
            // Forward <END> downstream (if any), then finish 
            if (ctx->next_place_work) {
                (void)ctx->next_place_work(item); // next stage copies 
            }
            free(item);   // we owned the dequeued string
            ctx->finished = 1;
            consumer_producer_signal_finished(&ctx->queue);    // wake any waiters on this stage
            break;
        }

               // transform, plugin returns a heap-allocated string 
        const char* res = ctx->process_function ? ctx->process_function(item) : strdup(item);
        free(item);

        if (!res) {
            log_error(ctx, "process_function returned NULL");
            continue;
        }
        char* out = (char*)res;  // cast away const because the plugin allocated it 

        // pass to next stage or just drop after side-effects if last 
        if (ctx->next_place_work) {
            const char* err = ctx->next_place_work(out);
            if (err != NULL) {
                log_error(ctx, "next_place_work returned error");
            }
            free(out); // we free our heap copy
        } else {
            free(out); //last stage 
        }
    }

    return NULL;
}

void log_error(plugin_context_t* context, const char* message) {
    fprintf(stderr, "[ERROR][%s] %s\n", context && context->name ? context->name : "plugin", message);
}

void log_info(plugin_context_t* context, const char* message) {
    (void)context; 
    (void)message;
}

// common init
const char* common_plugin_init(const char* (*process_function)(const char*), const char* name, int queue_size) {
    memset(&g_ctx, 0, sizeof(g_ctx));    // reset per instance context
    g_ctx.name = name;
    g_ctx.process_function = process_function;   // plugin’s transform function
    g_ctx.next_place_work = NULL;
    g_ctx.finished = 0;

    const char* qerr = consumer_producer_init(&g_ctx.queue, queue_size);
    if (qerr != NULL) return qerr;  // queue init error

    if (pthread_create(&g_ctx.consumer_thread, NULL, consumer_thread_main, &g_ctx) != 0) {
        consumer_producer_destroy(&g_ctx.queue);
        return "pthread_create failed";
    }

    g_ctx.initialized = 1;
    return NULL;
}

__attribute__((weak, visibility("default")))
const char* plugin_get_name(void) {
    // weak so the plugin’s strong definition wins, prevents duplicate symbols 
    return g_ctx.name ? g_ctx.name : "plugin";
}

// Each plugin provides a strong plugin_init(int) and calls common_plugin_init(...).
__attribute__((weak, visibility("default")))
const char* plugin_init(int queue_size) {
    (void)queue_size;
    return "plugin_init() must be implemented by each plugin and call common_plugin_init(...)";
}

__attribute__((visibility("default")))
const char* plugin_place_work(const char* str) {
    if (!g_ctx.initialized) return "plugin not initialized";
    return consumer_producer_put(&g_ctx.queue, str); // queue takes its own copy
}

__attribute__((visibility("default")))
void plugin_attach(const char* (*next_place_work)(const char*)) {
    g_ctx.next_place_work = next_place_work;   // set downstream stage
}

__attribute__((visibility("default")))
const char* plugin_wait_finished(void) {
    if (!g_ctx.initialized) return "plugin not initialized";
    (void)pthread_join(g_ctx.consumer_thread, NULL); // block until worker exits
    g_ctx.finished = 1; // mark so fini won’t double-join 
    return NULL;
}

__attribute__((visibility("default")))
const char* plugin_fini(void) {
    if (g_ctx.initialized) {
        if (!g_ctx.finished) { (void)pthread_join(g_ctx.consumer_thread, NULL); }
        consumer_producer_destroy(&g_ctx.queue);  // destroy queue + monitors
        g_ctx.initialized = 0;
    }
    return NULL;
}
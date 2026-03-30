\
#ifndef PLUGIN_COMMON_H
#define PLUGIN_COMMON_H

#include <pthread.h>
#include "plugin_sdk.h"
#include "sync/consumer_producer.h"

#ifdef __cplusplus
extern "C" {
#endif

#define SENTINEL_STR "<END>"

// Context shared by all plugins
typedef struct {
    const char* name;
    consumer_producer_t queue;
    pthread_t consumer_thread;
    const char* (*next_place_work)(const char*);
    const char* (*process_function)(const char*); // returns newly allocated string
    int initialized;
    int is_last;
    int finished;
} plugin_context_t;

// Common exported functions (visibility default so dlsym can find them)
__attribute__((visibility("default"))) const char* plugin_get_name(void);
__attribute__((visibility("default"))) const char* plugin_init(int queue_size);
__attribute__((visibility("default"))) const char* plugin_fini(void);
__attribute__((visibility("default"))) const char* plugin_place_work(const char* str);
__attribute__((visibility("default"))) void plugin_attach(const char* (*next_place_work)(const char*));
__attribute__((visibility("default"))) const char* plugin_wait_finished(void);

// Implemented by plugin_common.c — called by each plugin's plugin_init()
const char* common_plugin_init(const char* (*process_function)(const char*), const char* name, int queue_size);

// Logging helpers (to stderr). Keep stdout clean from non-pipeline logs.
void log_error(plugin_context_t* context, const char* message);
void log_info(plugin_context_t* context, const char* message);

// Expose access to the singleton context inside each .so
plugin_context_t* plugin_get_context(void);

#ifdef __cplusplus
}
#endif

#endif /* PLUGIN_COMMON_H */

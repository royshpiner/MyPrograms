
/*
 * main.c - Modular Pipeline System main application
 */
#define _GNU_SOURCE
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <dlfcn.h>
#include <errno.h>
#include <unistd.h>
#include <libgen.h>
#include <limits.h>
#include "plugins/plugin_sdk.h"


#define MAX_LINE 1024
#define SENTINEL "<END>"

static void print_usage(void) {
    printf("Usage: ./analyzer <queue_size> <plugin1> <plugin2> ... <pluginN>\n\n");
    printf("Arguments:\n");
    printf("  queue_size    Maximum number of items in each plugin's queue\n");
    printf("  plugin1..N    Names of plugins to load (without .so extension)\n\n");
    printf("Available plugins:\n");
    printf("  logger        - Logs all strings that pass through\n");
    printf("  typewriter    - Simulates typewriter effect with delays\n");
    printf("  uppercaser    - Converts strings to uppercase\n");
    printf("  rotator       - Move every character to the right.  Last character moves to the beginning.\n");
    printf("  flipper       - Reverses the order of characters\n");
    printf("  expander      - Expands each character with spaces\n\n");
    printf("Example:\n");
    printf("  ./analyzer 20 uppercaser rotator logger\n");
    printf("  echo 'hello' | ./analyzer 20 uppercaser rotator logger\n");
    printf("  echo '<END>' | ./analyzer 20 uppercaser rotator logger\n");
}

typedef struct {
    plugin_init_func_t init;
    plugin_fini_func_t fini;
    plugin_place_work_func_t place_work;
    plugin_attach_func_t attach;
    plugin_wait_finished_func_t wait_finished;
    char* name;
    void* handle;
} plugin_handle_t;

static int get_executable_dir(char* out_dir, size_t out_size) { // get the directory of the running binary using /proc/self/exe
    char path[PATH_MAX];
    ssize_t n = readlink("/proc/self/exe", path, sizeof(path)-1);
    if (n <= 0) return -1;
    path[n] = '\0';
    // dirname may modify its argument
    char* d = dirname(path);
    if (!d) return -1;
    snprintf(out_dir, out_size, "%s", d);
    return 0;
}
static int copy_file(const char* src, const char* dst) { // copy src -> dst (used so duplicate plugin names get separate static state)
    FILE* in = fopen(src, "rb");
    if (!in) return -1;
    FILE* out = fopen(dst, "wb");
    if (!out) { fclose(in); return -1; }
    char buf[1 << 16];
    size_t n;
    while ((n = fread(buf, 1, sizeof(buf), in)) > 0) {
        if (fwrite(buf, 1, n, out) != n) { fclose(in); fclose(out); errno = EIO; return -1; }
    }
    if (ferror(in)) { fclose(in); fclose(out); errno = EIO; return -1; }
    fclose(in);
    if (fclose(out) != 0) return -1;
    return 0;
}

int main(int argc, char** argv) {
    //step 1 check arguments
    if (argc < 3) { //check the number of arguments
        fprintf(stderr, "Error: missing arguments.\n");
        print_usage();
        return 1;
    }
    // Parse queue size
    char* endptr = NULL;
    long firstArgumentNum = strtol(argv[1], &endptr, 10);  //if the whole stirng,converted to long, (queue size) is a valid number then endtpr will point to '\0' otherwise it will point to the first invalid character
    if (*argv[1] == '\0' || *endptr != '\0' || firstArgumentNum <= 0 || firstArgumentNum > INT_MAX) {
        fprintf(stderr, "Error: queue_size must be a positive integer.\n");
        print_usage();
        return 1;
    }
    int queue_size = (int)firstArgumentNum;
    int plugin_count = argc - 2;   //the first argument is the running function and the second is the queue size, all others are plugins

    //step 2 preperation
    plugin_handle_t* plugins = calloc((size_t)plugin_count, sizeof(*plugins));
    if (!plugins) { perror("calloc"); return 1; }
    char exe_dir[PATH_MAX] = {0};  // find the directory of the analyzer
    if (get_executable_dir(exe_dir, sizeof(exe_dir)) != 0) { 
        // Fallback to current directory
        if (!getcwd(exe_dir, sizeof(exe_dir))) strcpy(exe_dir, ".");
    }
    // load each plugin (duplicate names get per-instance copied .so)
    for (int i = 0; i < plugin_count; ++i) {
        const char* pname = argv[2 + i];   //first plugin at location 2
        char base_so[PATH_MAX];
        char inst_so[PATH_MAX];
        // load from same directory as analyzer (output/)


        snprintf(base_so, sizeof(base_so), "%s/%s.so", exe_dir, pname);
        snprintf(inst_so, sizeof(inst_so), "%s/%s%d.so", exe_dir, pname, i); // "./<exe_dir>/<pname><i>.so" for duplicate plugins
        void* handle = NULL;
        
        if (copy_file(base_so, inst_so) != 0) {
            // if the base .so doesn't exist, dlopen it directly to get the loader's error text
            handle = dlopen(base_so, RTLD_NOW | RTLD_LOCAL);
            if (!handle) {
                fprintf(stderr, "Error: failed to load plugin '%s' from '%s': %s\n",pname, base_so, dlerror()); // includes "cannot open shared object file"
                print_usage();
                free(plugins);
                return 1;
            }
        }
        if (!handle) { // on success copying: load the per-instance file
            handle = dlopen(inst_so, RTLD_NOW | RTLD_LOCAL);
            if (!handle) {
                fprintf(stderr, "Error: dlopen failed for '%s': %s\n", inst_so, dlerror());
                print_usage();
                unlink(inst_so);
                free(plugins);
                return 1;
            }
            unlink(inst_so);
        }
        // resolve required SDK symbols (hard requirements)
        plugins[i].handle = handle;
        plugins[i].init = (plugin_init_func_t)dlsym(handle, "plugin_init");
        plugins[i].fini = (plugin_fini_func_t)dlsym(handle, "plugin_fini");
        plugins[i].place_work = (plugin_place_work_func_t)dlsym(handle, "plugin_place_work");
        plugins[i].attach = (plugin_attach_func_t)dlsym(handle, "plugin_attach");
        plugins[i].wait_finished = (plugin_wait_finished_func_t)dlsym(handle, "plugin_wait_finished");

        plugin_get_name_func_t get_name = (plugin_get_name_func_t)dlsym(handle, "plugin_get_name");
        
        if (!plugins[i].init || !plugins[i].fini || !plugins[i].place_work || !plugins[i].attach || !plugins[i].wait_finished || !get_name) {
            fprintf(stderr, "Error: plugin '%s' missing required symbols: %s\n", pname, dlerror());
            print_usage();
            // cleanup loaded so far
            for (int j = 0; j <= i; ++j) if (plugins[j].handle) dlclose(plugins[j].handle); // unload already-opened .so
            free(plugins);
            return 1;
        }

        const char* gname = get_name();
        plugins[i].name = gname ? strdup(gname) : strdup(pname);
        if (!plugins[i].name) { perror("strdup"); return 1; }
    }

    // step 3 initialize plugins
    for (int i = 0; i < plugin_count; ++i) {
        const char* err = plugins[i].init(queue_size); // plugin may return error
        if (err != NULL) {
            fprintf(stderr, "Error: init failed for plugin '%s': %s\n", plugins[i].name, err);
            // cleanup previously inited plugins
            for (int j = 0; j < i; ++j) (void)plugins[j].fini(); // best-effort finalize those already inited
            for (int j = 0; j < plugin_count; ++j) {
                if (plugins[j].handle) dlclose(plugins[j].handle);
                free(plugins[j].name);
            }
            free(plugins);
            return 2;
        }
    }
    // step 4 attach plugins - each plugin forwards to the next
    for (int i = 0; i < plugin_count - 1; ++i) {
        plugins[i].attach(plugins[i+1].place_work); // attach next stage’s place_work
    }

    // step 5 Read from input and feed the first plugin, stop after sending "<END>"
    char buf[MAX_LINE + 2];
    while (fgets(buf, sizeof(buf), stdin) != NULL) {
        size_t len = strlen(buf);
        if (len > 0 && buf[len-1] == '\n') buf[len-1] = '\0';  // remove newline if exists
        const char* input = buf;
        const char* err = plugins[0].place_work(input); //send to first plugin
        if (err != NULL) {
            fprintf(stderr, "Error: place_work failed in plugin '%s': %s\n", plugins[0].name, err);
            break;
        }
        if (strcmp(input, SENTINEL) == 0) {  // after sending END, stop reading further
            break;
        }
    }

    // step 6 Wait for plugins to finish in order
    for (int i = 0; i < plugin_count; ++i) {
        const char* err = plugins[i].wait_finished(); // blocking wait, no busy-wait
        if (err != NULL) {
            fprintf(stderr, "Error: wait_finished failed for plugin '%s': %s\n", plugins[i].name, err);
        }
    }

    // step 7 Cleanup
    for (int i = 0; i < plugin_count; ++i) {
        (void)plugins[i].fini();  //shutdown
    }
    for (int i = 0; i < plugin_count; ++i) {
        if (plugins[i].handle) dlclose(plugins[i].handle); // unload .so
        free(plugins[i].name);     // free strdup name
    }
    free(plugins);   // free plugin array

    // step 8 Finalize
    printf("Pipeline shutdown complete\n");
    return 0;
}

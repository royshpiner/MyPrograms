#ifndef PLUGIN_SDK_H
#define PLUGIN_SDK_H

#ifdef __cplusplus
extern "C" {
#endif

/* ========= Required plugin interface (per assignment) ========= */

/**
 * Get the plugin's name
 * @return The plugin's name (should not be modified or freed)
 */
const char* plugin_get_name(void);

/**
 * Initialize the plugin with the specified queue size
 * @param queue_size Maximum number of items that can be queued
 * @return NULL on success, error message on failure
 */
const char* plugin_init(int queue_size);

/**
 * Finalize the plugin - terminate thread gracefully
 * @return NULL on success, error message on failure
 */
const char* plugin_fini(void);

/**
 * Place work (a string) into the plugin's queue
 * @param str The string to process
 * @return NULL on success, error message on failure
 */
const char* plugin_place_work(const char* str);

/**
 * Attach this plugin to the next plugin in the chain
 * @param next_place_work Function pointer to the next plugin's place_work
 */
void plugin_attach(const char* (*next_place_work)(const char*));

/**
 * Wait until the plugin has finished processing all work and is ready to shutdown
 * @return NULL on success, error message on failure
 */
const char* plugin_wait_finished(void);


/* ========= Convenience typedefs for the loader (main.c) ========= */

typedef const char* (*plugin_get_name_func_t)(void);
typedef const char* (*plugin_init_func_t)(int);
typedef const char* (*plugin_fini_func_t)(void);
typedef const char* (*plugin_place_work_func_t)(const char*);
typedef void (*plugin_attach_func_t)(const char* (*next_place_work)(const char*));
typedef const char* (*plugin_wait_finished_func_t)(void);

#ifdef __cplusplus
}
#endif

#endif /* PLUGIN_SDK_H */
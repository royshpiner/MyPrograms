\
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "plugin_common.h"

__attribute__((visibility("default")))
const char* plugin_transform(const char* input) {
    // print then forward unchanged
    printf("[logger] %s\n", input);
    fflush(stdout); 
    const char* out = strdup(input);  // return a heap copy so the common layer can forward and free it safely
    return out;
}

__attribute__((visibility("default")))
const char* plugin_get_name(void) { return "logger"; }

__attribute__((visibility("default")))
const char* plugin_init(int queue_size) {
    return common_plugin_init(plugin_transform, "logger", queue_size);
}

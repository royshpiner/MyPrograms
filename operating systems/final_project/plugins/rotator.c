#include <stdlib.h>
#include <string.h>
#include "plugin_common.h"

__attribute__((visibility("default")))
const char* plugin_transform(const char* input) {
    size_t n = strlen(input);
    char* out = (char*)malloc(n + 1);
    if (!out) return NULL;
    if (n == 0) { out[0] = '\0'; return out; }  // empty input stays empty
    // Move last char to front, shift the rest right 
    out[0] = input[n - 1];
    memcpy(out + 1, input, n - 1);
    out[n] = '\0';
    return out;
}

__attribute__((visibility("default")))
const char* plugin_get_name(void) { return "rotator"; }

__attribute__((visibility("default")))
const char* plugin_init(int queue_size) {
    return common_plugin_init(plugin_transform, "rotator", queue_size);
}
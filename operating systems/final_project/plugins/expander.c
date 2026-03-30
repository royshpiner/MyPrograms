#include <stdlib.h>
#include <string.h>
#include "plugin_common.h"

__attribute__((visibility("default")))
const char* plugin_transform(const char* input) {
    size_t n = strlen(input);
    if (n == 0) {
        char* out = (char*)malloc(1);
        if (out) out[0] = '\0';
        return out;
    }
    size_t m = 2 * n - 1; // spaces between each char 
    char* out = (char*)malloc(m + 1);  // +1 for '\0'
    if (!out) return NULL;
    for (size_t i = 0; i < n; ++i) {
        out[2 * i] = input[i];  // copy char
        if (i + 1 < n) out[2 * i + 1] = ' ';  // add space between, not after last
    }
    out[m] = '\0';
    return out;
}

__attribute__((visibility("default")))
const char* plugin_get_name(void) { return "expander"; }

__attribute__((visibility("default")))
const char* plugin_init(int queue_size) {
    return common_plugin_init(plugin_transform, "expander", queue_size);
}
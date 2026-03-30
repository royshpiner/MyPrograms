#include <ctype.h>
#include <stdlib.h>
#include <string.h>
#include "plugin_common.h"


__attribute__((visibility("default")))
const char* plugin_transform(const char* input) {
    size_t n = strlen(input);
    char* out = (char*)malloc(n + 1);
    if (!out) return NULL;
    for (size_t i = 0; i < n; ++i) out[i] = (char)toupper((unsigned char)input[i]); // uppercase safely (cast to unsigned char)
    out[n] = '\0';
    return out;
}

__attribute__((visibility("default")))
const char* plugin_get_name(void) { return "uppercaser"; }

__attribute__((visibility("default")))
const char* plugin_init(int queue_size) {
    return common_plugin_init(plugin_transform, "uppercaser", queue_size);
}
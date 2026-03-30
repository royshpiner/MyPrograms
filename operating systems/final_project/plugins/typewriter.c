\
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include "plugin_common.h"

__attribute__((visibility("default")))
const char* plugin_transform(const char* input) {
    // print slowly with 100ms delay per character
    fputs("[typewriter] ", stdout);
    for (const char* p = input; *p; ++p) {
        fputc(*p, stdout);
        fflush(stdout);
        usleep(100000); // 100ms
    }
    fputc('\n', stdout);
    fflush(stdout);
    return strdup(input);
}

__attribute__((visibility("default")))
const char* plugin_get_name(void) { return "typewriter"; }

__attribute__((visibility("default")))
const char* plugin_init(int queue_size) {
    return common_plugin_init(plugin_transform, "typewriter", queue_size);
}

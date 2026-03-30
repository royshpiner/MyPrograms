#!/usr/bin/env bash
set -euo pipefail

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

print_status(){ echo -e "${GREEN}[BUILD]${NC} $1"; }
print_warning(){ echo -e "${YELLOW}[WARNING]${NC} $1"; }
print_error(){ echo -e "${RED}[ERROR]${NC} $1"; }

mkdir -p output

CFLAGS="-Wall -Wextra -std=c11 -O2"
LDFLAGS_MAIN="-ldl -lpthread"
LDFLAGS_PLUGINS="-ldl -lpthread"

print_status "Compiling main application"
gcc $CFLAGS -o output/analyzer main.c $LDFLAGS_MAIN

# Build plugins list
plugins=(logger typewriter uppercaser rotator flipper expander)

for plugin_name in "${plugins[@]}"; do
  print_status "Building plugin: $plugin_name"
  gcc -fPIC -shared -o "output/${plugin_name}.so"     "plugins/${plugin_name}.c"     "plugins/plugin_common.c"     "plugins/sync/monitor.c"     "plugins/sync/consumer_producer.c"     $LDFLAGS_PLUGINS
done



print_status "Build complete"

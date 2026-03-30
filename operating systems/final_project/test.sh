#!/usr/bin/env bash
# Minimal, robust test runner for the Modular Pipeline System
# - Builds project (main + plugins) via build.sh
# - Runs positive & negative tests automatically
# - Prints clear PASS/FAIL lines 
# - Exits non-zero if any test fails

set -u  # no unbound vars

# --- colors ---
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'  # reset

# --- config ---
OUT_DIR="output"
ANALYZER="${OUT_DIR}/analyzer"
PLUGINS=(logger typewriter uppercaser rotator flipper expander)

# --- counters ---
PASS=0
FAIL=0
TOTAL=0

# --- helpers (colored output) ---
pass_line() { echo -e "  [${GREEN}PASS${NC}] $*"; ((PASS++)); }
fail_line() { echo -e "  [${RED}FAIL${NC}] $*"; ((FAIL++)); }
next()      { ((TOTAL++)); echo; echo "Test ${TOTAL}: $*"; }

# Run a command with timeout and capture output+exit
run() {
  # usage: run <timeout_sec> <cmd...>
  local to="$1"; shift
  timeout --preserve-status "${to}" "$@" 2>&1
  return $?
}

# Expect command to exit with one of the given codes
expect_exit_in() {
  local expect_list="$1"; shift
  local out rc
  out="$(run 10 "$@")"; rc=$?
  if [[ " ${expect_list} " =~ " ${rc} " ]]; then
    pass_line "exit code in {${expect_list}}"
    printf "%s\n" "$out" | sed 's/^/        | /'
    return 0
  else
    fail_line "exit code ${rc}, expected one of {${expect_list}}"
    printf "%s\n" "$out" | sed 's/^/        | /'
    return 1
  fi
}

# Expect output to contain a substring (case-sensitive)
expect_output_has() {
  local needle="$1"; shift
  local out rc
  out="$(run 10 "$@")"; rc=$?
  if grep -Fq -- "$needle" <<<"$out"; then
    pass_line "output contains: $needle"
    printf "%s\n" "$out" | sed 's/^/        | /'
    return 0
  else
    fail_line "missing substring: $needle"
    printf "%s\n" "$out" | sed 's/^/        | /'
    return 1
  fi
}

# Expect exact line from pipeline logger
expect_logger_is() {
  local input="$1" expected="$2"; shift 2
  local out rc line
  out="$(echo -e "${input}" | run 10 "$@")"; rc=$?
  line="$(grep '^\[logger\] ' <<<"$out" || true)"
  if [[ "$line" == "$expected" ]]; then
    pass_line "logger line equals: ${expected}"
  else
    fail_line "logger mismatch. expected: ${expected} ; got: ${line}"
    printf "%s\n" "$out" | sed 's/^/        | /'
    return 1
  fi
}

# Expect N logger lines
expect_logger_count() {
  local input="$1" count_expected="$2"; shift 2
  local out rc c
  out="$(echo -e "${input}" | run 15 "$@")"; rc=$?
  c="$(grep -c '^\[logger\] ' <<<"$out" || true)"
  if [[ "$c" -eq "$count_expected" ]]; then
    pass_line "logger count == ${count_expected}"
  else
    fail_line "logger count mismatch: expected ${count_expected}, got ${c}"
    printf "%s\n" "$out" | sed 's/^/        | /'
    return 1
  fi
}

# --- 1) build ---
next "Build via build.sh"
if run 60 ./build.sh >/dev/null; then
  pass_line "build.sh completed"
else
  fail_line "build.sh failed"
fi

next "Analyzer exists"
if [[ -x "${ANALYZER}" ]]; then
  pass_line "found ${ANALYZER}"
else
  fail_line "missing ${ANALYZER}"
fi

next "All plugin .so exist"
missing=0
for p in "${PLUGINS[@]}"; do
  if [[ ! -f "${OUT_DIR}/${p}.so" ]]; then
    fail_line "missing plugin: ${p}.so"
    missing=1
  fi
done
[[ $missing -eq 0 ]] && pass_line "all plugins present"

# --- 2) command-line validation (negative) ---
# accept either 1 or 2 as usage error exit code
USAGE_CODES="1 2"

next "No arguments -> usage error"
expect_exit_in "${USAGE_CODES}" "${ANALYZER}"

next "Only queue size -> usage error"
expect_exit_in "${USAGE_CODES}" "${ANALYZER}" 10

next "Invalid queue size (0)"
out="$(run 10 "${ANALYZER}" 0 logger)"; rc=$?
if [[ $rc -ne 0 && "$out" == *"Error: queue_size must be a positive integer."* ]]; then
  pass_line "rejects zero with correct error"
else
  fail_line "did not reject zero properly"
  printf "%s\n" "$out" | sed 's/^/        | /'
fi

next "Invalid queue size (negative)"
out="$(run 10 "${ANALYZER}" -5 logger)"; rc=$?
if [[ $rc -ne 0 && "$out" == *"Error: queue_size must be a positive integer."* ]]; then
  pass_line "rejects negative with correct error"
else
  fail_line "did not reject negative properly"
  printf "%s\n" "$out" | sed 's/^/        | /'
fi

next "Invalid queue size (non-numeric)"
out="$(run 10 "${ANALYZER}" abc logger)"; rc=$?
if [[ $rc -ne 0 && "$out" == *"Error: queue_size must be a positive integer."* ]]; then
  pass_line "rejects non-numeric with correct error"
else
  fail_line "did not reject non-numeric properly"
  printf "%s\n" "$out" | sed 's/^/        | /'
fi

next "Missing plugin -> loader error contains canonical text"
out="$(run 10 "${ANALYZER}" 10 nonexistent_plugin)"; rc=$?
if [[ $rc -ne 0 && "$out" == *"failed to load plugin"* && "$out" == *"cannot open shared object file"* ]]; then
  pass_line "missing plugin handled with correct loader message"
else
  fail_line "missing plugin message unexpected"
  printf "%s\n" "$out" | sed 's/^/        | /'
fi

# --- 3) individual plugins (positive) ---
next "logger echoes input"
expect_logger_is "hello\n<END>" "[logger] hello" "${ANALYZER}" 10 logger

next "uppercaser -> logger"
expect_logger_is "hello\n<END>" "[logger] HELLO" "${ANALYZER}" 10 uppercaser logger

next "rotator -> logger"
expect_logger_is "abcdef\n<END>" "[logger] fabcde" "${ANALYZER}" 10 rotator logger

next "flipper -> logger"
expect_logger_is "hello\n<END>" "[logger] olleh" "${ANALYZER}" 10 flipper logger

next "expander -> logger"
expect_logger_is "abc\n<END>" "[logger] a b c" "${ANALYZER}" 10 expander logger

next "typewriter prints expected line"
out="$(echo -e "hi\n<END>" | run 10 "${ANALYZER}" 10 typewriter)"
if grep -Fxq "[typewriter] hi" <<<"$out"; then
  pass_line "typewriter printed expected line"
else
  fail_line "typewriter output mismatch"
  printf "%s\n" "$out" | sed 's/^/        | /'
fi

# --- 4) pipeline chains ---
next "two-stage: uppercaser -> logger"
expect_logger_is "hello\n<END>" "[logger] HELLO" "${ANALYZER}" 10 uppercaser logger

next "three-stage: uppercaser -> rotator -> logger"
expect_logger_is "abcd\n<END>" "[logger] DABC" "${ANALYZER}" 10 uppercaser rotator logger

next "five-stage complex"
expect_logger_is "hello\n<END>" "[logger] L L E H O" "${ANALYZER}" 15 uppercaser rotator flipper expander logger

next "multiple input lines"
out="$(echo -e "line1\nline2\nline3\n<END>" | run 10 "${ANALYZER}" 10 uppercaser logger)"
if grep -Fxq "[logger] LINE1" <<<"$out" &&
   grep -Fxq "[logger] LINE2" <<<"$out" &&
   grep -Fxq "[logger] LINE3" <<<"$out"; then
  pass_line "processed three lines"
else
  fail_line "multi-line processing mismatch"
  printf "%s\n" "$out" | sed 's/^/        | /'
fi

# --- 5) edge cases & shutdown ---
next "empty string through logger"
expect_logger_is "\n<END>" "[logger] " "${ANALYZER}" 10 logger

next "single char uppercaser"
expect_logger_is "a\n<END>" "[logger] A" "${ANALYZER}" 10 uppercaser logger

next "immediate <END> triggers graceful shutdown"
out="$(echo -e "<END>" | run 10 "${ANALYZER}" 10 logger)"
if [[ "$out" == *"Pipeline shutdown complete"* ]]; then
  pass_line "shutdown message present"
else
  fail_line "missing shutdown message"
  printf "%s\n" "$out" | sed 's/^/        | /'
fi

# --- 6) repeated plugin usage ---
next "same plugin multiple times (3 loggers)"
out="$(echo -e "test\n<END>" | run 10 "${ANALYZER}" 10 logger logger logger)"
count="$(grep -c '^\[logger\] ' <<<"$out" || true)"
if [[ "$count" -eq 3 ]]; then
  pass_line "three logger instances produced three lines"
else
  fail_line "expected 3 logger lines, got ${count}"
  printf "%s\n" "$out" | sed 's/^/        | /'
fi

next "four rotations restore original"
expect_logger_is "abcd\n<END>" "[logger] abcd" "${ANALYZER}" 10 rotator rotator rotator rotator logger

next "double flip restores original"
expect_logger_is "hello\n<END>" "[logger] hello" "${ANALYZER}" 10 flipper flipper logger

# --- 7) queue capacity ---
next "queue size = 1 still processes"
expect_logger_is "test\n<END>" "[logger] TEST" "${ANALYZER}" 1 uppercaser logger

next "small queue handles 5 items"
out="$(echo -e "i1\ni2\ni3\ni4\ni5\n<END>" | run 15 "${ANALYZER}" 2 logger)"
count="$(grep -c '^\[logger\] ' <<<"$out" || true)"
if [[ "$count" -eq 5 ]]; then
  pass_line "processed 5 items with small queue"
else
  fail_line "expected 5 logger lines, got ${count}"
  printf "%s\n" "$out" | sed 's/^/        | /'
fi

# --- 8) long strings ---
next "1000-char line passes unchanged through logger"
long=$(printf 'A%.0s' {1..1000})
out="$(echo -e "${long}\n<END>" | run 15 "${ANALYZER}" 10 logger)"
got="$(grep '^\[logger\] ' <<<"$out" | sed 's/^\[logger\] //')"
if [[ "$got" == "$long" ]]; then
  pass_line "long line (1000 chars) ok"
else
  fail_line "long line mismatch (length got ${#got}, expected ${#long})"
fi

# --- summary ---
echo
echo "======================================================="
echo -e "RESULTS: ${GREEN}${PASS}${NC} passed, ${RED}${FAIL}${NC} failed, ${TOTAL} total"
echo "======================================================="

exit $(( FAIL == 0 ? 0 : 1 ))
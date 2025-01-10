#!/bin/bash

set -eu
set -o pipefail

# This is not persisted into the JAR,
# i.e. if you don't also set this at runtime, Timbre will see `:compile-time-config {:min-level nil}` at runtime:
export TAOENSSO_TIMBRE_MIN_LEVEL_EDN='[[#{"timbre-slf4j-otel-agent-mwe.*"} :debug][#{"*"} :info]]'

exec clj -T:build uber

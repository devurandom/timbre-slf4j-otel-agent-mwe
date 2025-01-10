#!/bin/bash

set -eu
set -o pipefail

# https://opentelemetry.io/docs/zero-code/java/agent/configuration/#java-agent-logging-output
export OTEL_JAVAAGENT_LOGGING=application
# https://github.com/taoensso/timbre/wiki/1-Getting-started#compile-time-elision
# The environment variable does not use the syntax described in the docstring of `taoensso.timbre/*config*`:
#export TAOENSSO_TIMBRE_MIN_LEVEL_EDN='[[#{"timbre-slf4j-otel-agent-mwe.*"} :debug][#{"*"} :info]]'
export TAOENSSO_TIMBRE_MIN_LEVEL_EDN=':info'

jar="$(find target -name '*-standalone.jar' | head -n1)"

# slf4j.provider: https://www.slf4j.org/faq.html#explicitProvider
# slf4j.internal.verbosity: https://www.slf4j.org/faq.html#internalMessages
exec java \
  -Dslf4j.provider=com.taoensso.timbre.slf4j.TimbreServiceProvider \
  -Dslf4j.internal.verbosity=DEBUG \
  -javaagent:opentelemetry-javaagent-2.10.0.jar \
  -jar "${jar}"

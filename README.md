This reproduces a problem with SLF4J, Timbre and the OpenTelemetry Java agent,
where the Java agent's log messages are not filtered by log level until
`taoensso.timbre/merge-config!` was called, i.e. ignoring
`TAOENSSO_TIMBRE_MIN_LEVEL_EDN`.

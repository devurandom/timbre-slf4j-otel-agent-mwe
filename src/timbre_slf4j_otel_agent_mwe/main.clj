(ns timbre-slf4j-otel-agent-mwe.main
  (:gen-class)
  (:require
    [taoensso.timbre :as log])
  (:import
    (org.slf4j LoggerFactory)))

(defn -main
  []
  ;; Throw like recent versions of Timbre would,
  ;; cf. https://github.com/taoensso/timbre/commit/a393582b5d90ef019f9e47e1f3fcb16c2c23ff3a:
  (when-let [compile-time-min-level (get-in log/*config* [:_init-config :compile-time-config :min-level])]
    (when (and (sequential? compile-time-min-level)
               (= :timbre/invalid-min-level (first compile-time-min-level)))
      (throw (ex-info "invalid Timbre compile-time min-level" (:_init-config log/*config*)))))
  (println ">>> COMPILE-TIME MIN-LEVEL" (:min-level log/*config*))
  (println ">>> COMPILE-TIME CONFIG" log/*config*)
  (log/info "STARTING")
  ;; Without the following line, logs from the OpenTelemetry Java agent will not be passed through Timbre at all:
  (LoggerFactory/getLogger (str *ns*))
  ;; Move the call to `org.slf4j.LoggerFactory#getLogger(java.lang.String)` after `taoensso.timbre/merge-config!`
  ;; to show that Timbre would process the agent's log lines if it was configured early enough.
  ;; Sadly, this does not work for real applications,
  ;; where we are not in control over when the first call to SLF4J happens,
  ;; since libraries we use might call it before us.
  (log/merge-config! {:min-level [[#{"timbre-slf4j-otel-agent-mwe.*"} :debug]
                                  [#{"*"} :info]]
                      :appenders {:println {:min-level [[#{"timbre-slf4j-otel-agent-mwe.*"} :debug]
                                                        [#{"*"} :info]]}}
                      :middleware [(fn [data] (println ">>> TIMBRE DEBUG" data) data)]})
  (println ">>> RUNTIME CONFIG" log/*config*)
  (log/info "LOGGER INITIALIZED")
  (Thread/sleep 10000)
  (log/info "EXITING"))

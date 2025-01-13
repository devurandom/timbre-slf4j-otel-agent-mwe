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
  ;; With the following line before the call of `org.slf4j.LoggerFactory#getLogger(java.lang.String)`,
  ;; we see that Timbre processes the log messages from the OpenTelemetry Java agent:
  (log/merge-config! {:middleware [(fn [data] (println ">>> HELLO FROM TIMBRE MIDDLEWARE" data) data)]})
  (println ">>> RUNTIME CONFIG WITH MIDDLEWARE" log/*config*)
  ;; Without the following line, logs from the OpenTelemetry Java agent will not be passed through Timbre at all:
  (LoggerFactory/getLogger (str *ns*))
  ;; Move the following line call to before `org.slf4j.LoggerFactory#getLogger(java.lang.String)`
  ;; to show that Timbre would filter the agent's log lines by level if it was configured early enough.
  ;; Sadly, this does not work for real applications,
  ;; where we are not in control over when the first call to SLF4J happens,
  ;; since libraries we use might call it before us.
  (log/merge-config! {:min-level [[#{"timbre-slf4j-otel-agent-mwe.*"} :debug]
                                  [#{"*"} :info]]
                      :appenders {:println {:min-level [[#{"timbre-slf4j-otel-agent-mwe.*"} :debug]
                                                        [#{"*"} :info]]}}})
  (println ">>> RUNTIME CONFIG WITH MIN-LEVEL" log/*config*)
  (log/info "STARTED")
  (Thread/sleep 10000)
  (log/info "EXITING"))

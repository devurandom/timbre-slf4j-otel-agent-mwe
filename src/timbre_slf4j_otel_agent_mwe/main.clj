(ns timbre-slf4j-otel-agent-mwe.main
  (:gen-class)
  (:require
    [taoensso.timbre :as log])
  (:import
    (org.slf4j LoggerFactory)))

(defn -main
  []
  ;; Without the following line, logs from the OpenTelemetry Java agent will not be passed through Timbre:
  (LoggerFactory/getLogger (str *ns*))
  ;; Move the call to `org.slf4j.LoggerFactory#getLogger(java.lang.String)` after `taoensso.timbre/merge-config!`
  ;; to show that Timbre would process the agent's log lines if it was configured early enough.
  ;; Sadly, this does not work for real applications,
  ;; where we are not in control over when the first call to SLF4J happens,
  ;; since libraries we use might call it before us.
  (println ">>> STARTUP LOG CONFIG" log/*config*)
  (log/info "STARTING")
  (log/merge-config! {:min-level [[#{"timbre-slf4j-otel-agent-mwe.*"} :debug]
                                  [#{"*"} :info]]
                      :appenders {:println {:min-level [[#{"timbre-slf4j-otel-agent-mwe.*"} :debug]
                                                        [#{"*"} :info]]}}
                      :middleware [(fn [data] (println ">>> TIMBRE DEBUG" data) data)]})
  (println ">>> INITIALIZED LOG CONFIG" log/*config*)
  (log/info "LOGGER INITIALIZED")
  (Thread/sleep 10000)
  (log/info "EXITING"))

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

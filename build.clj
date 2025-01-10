(ns build
  (:require
    [clojure.tools.build.api :as b]))

(def lib 'timbre-slf4j-otel-agent-mwe)
(def version (b/git-process {:git-args "rev-parse HEAD"}))
(def class-dir "target/classes")
(def uber-file (format "target/%s-%s-standalone.jar" (name lib) version))

(def basis (delay (b/create-basis {:project "deps.edn"})))

(defn clean [_]
  (b/delete {:path "target"}))

(defn uber [_]
  (clean nil)
  (b/copy-dir {:src-dirs   ["src" "resources"]
               :target-dir class-dir})
  (b/compile-clj {:basis      @basis
                  :ns-compile '[timbre-slf4j-otel-agent-mwe.main]
                  :class-dir  class-dir})
  (b/uber {:class-dir class-dir
           :uber-file uber-file
           :basis     @basis
           :main      'timbre-slf4j-otel-agent-mwe.main
           :manifest  {"Implementation-Title"   lib
                       "Implementation-Version" version}}))

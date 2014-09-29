(ns ring.server.leiningen
  "Functions to start a server from a Leiningen plugin."
  (:require [ring.server.standalone :as standalone]))

(defn- load-var [sym]
  (when sym
    (require (-> sym namespace symbol))
    (find-var sym)))

(defn serve
  "Start a server from a Leiningen project map."
  [project]
  (standalone/serve
   (load-var (-> project :ring :handler))
   (merge
    {:join? true}
    (:ring project)
    (-> project :ring :adapter)
    {:init    (load-var (-> project :ring :init))
     :destroy (load-var (-> project :ring :destroy))
     :stacktrace-middleware (load-var (-> project :ring :stacktrace-middleware))})))

(ns ring.server.leiningen
  "Functions to start a server from a Leiningen plugin."
  (:require [ring.server.standalone :as standalone]))

(defn- load-var [sym]
  (require (-> sym namespace symbol))
  (find-var sym))

(defn serve
  "Start a server from a Leiningen project map."
  [project]
  (standalone/serve
   (load-var (-> project :ring :handler))
   {:join? false, :open-browser? false}))

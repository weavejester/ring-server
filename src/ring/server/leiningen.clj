(ns ring.server.leiningen
  "Functions to start a server from a Leiningen plugin."
  (:require [ring.server.standalone :as standalone]))

(defn- load-var [sym]
  (when sym
    (require (-> sym namespace symbol))
    (find-var sym)))

(defn get-handler [project]
  (let [handler-sym (-> project :ring :handler)]
    (if (-> project :ring :re-resolve)
      (fn [r] ((load-var handler-sym) r))
      (load-var handler-sym))))

(defn serve
  "Start a server from a Leiningen project map."
  [project]
  (standalone/serve
   (get-handler project)
   (merge
    {:join? true}
    (:ring project)
    (merge
     (-> project :ring :adapter)
     {:configurator (load-var (-> project :ring :adapter :configurator))})
    {:init    (load-var (-> project :ring :init))
     :destroy (load-var (-> project :ring :destroy))
     :stacktrace-middleware (load-var (-> project :ring :stacktrace-middleware))})))

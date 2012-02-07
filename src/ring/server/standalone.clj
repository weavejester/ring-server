(ns ring.server.standalone
  (:use [ring.adapter.jetty :only (run-jetty)]))

(defn serve
  "Start a web server to run a handler."
  [handler & [{:as options}]]
  (run-jetty
    handler
    (merge {:port 5000} options)))

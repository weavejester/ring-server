(ns ring.server.standalone
  "Functions to start a standalone Ring server."
  (:use ring.adapter.jetty
        ring.util.environment))

(defn serve
  "Start a web server to run a handler."
  [handler & [{:as options}]]
  (let [port (Integer. (*env* "PORT" "5000"))]
    (run-jetty
      handler
      (merge {:port port} options))))

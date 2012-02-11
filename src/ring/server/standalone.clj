(ns ring.server.standalone
  "Functions to start a standalone Ring server."
  (:use ring.adapter.jetty
        ring.server.options))

(defn- try-port [port run-server]
  (if-not (sequential? port)
    (run-server port)
    (try (run-server (first port))
         (catch Exception ex
           (if-let [port (next port)]
             (try-port port run-server)
             (throw ex))))))

(defn serve
  "Start a web server to run a handler."
  [handler & [{:as options}]]
  (try-port (port options)
    (fn [port]
      (run-jetty
        handler
        (merge {:port port} options)))))

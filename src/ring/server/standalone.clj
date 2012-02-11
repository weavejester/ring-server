(ns ring.server.standalone
  "Functions to start a standalone Ring server."
  (:use ring.adapter.jetty
        ring.server.options))

(defn- try-port
  "Try running a server under one port or a list of ports. If a list of ports
  is supplied, try each port until it succeeds or runs out of ports."
  [port run-server]
  (if-not (sequential? port)
    (run-server port)
    (try (run-server (first port))
         (catch Exception ex
           (if-let [port (next port)]
             (try-port port run-server)
             (throw ex))))))

(defmacro ^:private in-thread
  "Execute the body in a new thread and return the Thread object."
  [& body]
  `(doto (Thread. (fn [] ~@body))
     (.start)))

(defn- add-destroy-hook [server destroy]
  "Add a destroy hook to be executed when the server ends."
  (in-thread
   (try (.join server)
        (finally (if destroy (destroy))))))

(defn serve
  "Start a web server to run a handler."
  [handler & [{:keys [init destroy join?] :as options}]]
  (let [options (assoc options :join? false)
        destroy (if destroy (memoize destroy))]
    (if init (init))
    (if destroy
      (. (Runtime/getRuntime)
         (addShutdownHook (Thread. destroy))))
    (try-port (port options)
      (fn [port]
        (let [options (merge {:port port} options) 
              server  (run-jetty handler options)
              thread  (add-destroy-hook server destroy)]
          (if join? (.join thread))
          server)))))

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

(defmacro ^:private in-thread [& body]
  `(doto (Thread. (fn [] ~@body))
     (.start)))

(defn- run-server [server destroy]
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
              thread  (run-server server destroy)]
          (if join? (.join thread))
          server)))))

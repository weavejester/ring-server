(ns ring.server.standalone
  "Functions to start a standalone Ring server."
  (:use ring.adapter.jetty
        ring.server.options
        ring.middleware.stacktrace
        ring.middleware.reload
        ring.middleware.refresh
        [clojure.java.browse :only (browse-url)]))

(defn- try-port
  "Try running a server under one port or a list of ports. If a list of ports
  is supplied, try each port until it succeeds or runs out of ports."
  [port run-server]
  (if-not (sequential? port)
    (run-server port)
    (try (run-server (first port))
         (catch java.net.BindException ex
           (if-let [port (next port)]
             (try-port port run-server)
             (throw ex))))))

(defn server-port
  "Get the port the server is listening on."
  [server]
  (-> (.getConnectors server)
      (first)
      (.getPort)))

(defn server-host
  "Get the host the server is bound to."
  [server]
  (-> (.getConnectors server)
      (first)
      (.getHost)
      (or "localhost")))

(defn- open-browser-to [server options]
  (future
    (browse-url
     (str "http://" (server-host server) ":" (server-port server) (browser-uri options)))))

(defmacro ^{:private true} in-thread
  "Execute the body in a new thread and return the Thread object."
  [& body]
  `(doto (Thread. (fn [] ~@body))
     (.start)))

(defn- add-destroy-hook [server destroy]
  "Add a destroy hook to be executed when the server ends."
  (in-thread
   (try (.join server)
        (finally (if destroy (destroy))))))

(defn- add-stacktraces [handler options]
  (if (stacktraces? options)
    ((or (:stacktrace-middleware options)
         wrap-stacktrace) handler)
    handler))

(defn- add-auto-reload [handler options]
  (if (auto-reload? options)
    (wrap-reload handler {:dirs (reload-paths options)})
    handler))

(defn- add-auto-refresh [handler options]
  (if (:auto-refresh? options)
    (wrap-refresh handler)
    handler))

(defn- add-middleware [handler options]
  (-> handler
      (add-auto-refresh options)
      (add-auto-reload options)
      (add-stacktraces options)))

(defn serve
  "Start a web server to run a handler. Takes the following options:
    :port                  - the port to run the server on
    :join?                 - if true, wait for the server to stop
    :init                  - a function to run before the server starts
    :destroy               - a function to run after the server stops
    :open-browser?         - if true, open a web browser after the server starts
    :browser-uri           - the path to browse to when opening a browser
    :stacktraces?          - if true, display stacktraces when an exception is thrown
    :stacktrace-middleware - a middleware that handles stacktraces
    :auto-reload?          - if true, automatically reload source files
    :reload-paths          - seq of src-paths to reload on change - defaults to [\"src\"]
    :auto-refresh?         - if true, automatically refresh browser when source changes

  If join? is false, a Server object is returned."
  {:arglists '([handler] [handler options])}
  [handler & [{:keys [init destroy join?] :as options}]]
  (let [options (assoc options :join? false)
        destroy (if destroy (memoize destroy))
        handler (add-middleware handler options)]
    (if init (init))
    (if destroy
      (. (Runtime/getRuntime)
         (addShutdownHook (Thread. destroy))))
    (try-port (port options)
      (fn [port]
        (let [options (merge {:port port} options)
              server  (run-jetty handler options)
              thread  (add-destroy-hook server destroy)]
          (println "Started server on port" (server-port server))
          (if (open-browser? options)
            (open-browser-to server options))
          (if join?
            (.join thread))
          server)))))

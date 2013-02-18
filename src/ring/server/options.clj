(ns ring.server.options
  "Functions to retrieve options and settings with sensible defaults"
  (:use ring.util.environment
        [clojure.core.incubator :only (-?>)])
  (:require [clojure.string :as str]))

(def dev-env?
  (not (*env* "LEIN_NO_DEV")))

(defn port
  "Find the port or list of ports specified in the options or environment.
  Defaults to a range of ports from 3000 to 3010."
  [options]
  (or (:port options)
      (-?> (*env* "PORT") Integer.)
      (range 3000 3010)))

(defn open-browser?
  "True if a browser should be opened to view the web server. By default
  a browser is opened unless the LEIN_NO_DEV environment variable is set."
  [options]
  (:open-browser? options dev-env?))

(defn browser-uri
  "The path to browse to when opening a browser"
  [options]
  (-> (str "/" (:browser-uri options))
      (str/replace #"^/+" "/")))

(defn auto-reload?
  "True if the source files should be automatically reloaded."
  [options]
  (:auto-reload? options dev-env?))

(defn stacktraces?
  "True if stacktraces should be shown for exceptions raised by the handler."
  [options]
  (:stacktraces? options dev-env?))

(defn reload-paths 
  [options]
  (:reload-paths options ["src"]))

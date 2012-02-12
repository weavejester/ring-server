(ns ring.server.options
  "Functions to retrieve options and settings with sensible defaults"
  (:use ring.util.environment
        ring.middleware.stacktrace
        ring.middleware.reload
        [clojure.core.incubator :only (-?>)]))

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

(defn middleware
  [options]
  (or (:middleware options)
      (if dev-env?
        [wrap-stacktrace wrap-reload])))

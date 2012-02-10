(ns ring.server.options
  "Functions to retrieve options and settings with sensible defaults"
  (:use [ring.util.environment :only (*env*)]
        [clojure.core.incubator :only (-?>)]))

(defn port
  "Find the port or list of ports specified in the options or environment.
  Defaults to a range of ports from 3000 to 3010."
  [options]
  (or (:port options)
      (-?> (*env* "PORT") Integer.)
      (range 3000 3010)))

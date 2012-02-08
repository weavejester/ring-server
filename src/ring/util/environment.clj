(ns ring.util.environment
  "A namespace managing and reading environment variables.")

(def ^{:dynamic true, :doc "A map of environment variables."}
  *env* (into {} (System/getenv)))

(defmacro with-env
  "Merges the supplied map of environment variable into *env*."
  [env-map & body]
  `(binding [*env* (merge *env* ~env-map)]
     ~@body))

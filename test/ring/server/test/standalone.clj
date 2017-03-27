(ns ring.server.test.standalone
  (:use clojure.test
        ring.server.standalone
        ring.server.test.utils
        ring.util.environment
        ring.util.response))

(defn exception-handler [req]
  (throw (Exception. "testing")))

(defn test-server [& [{:as options}]]
  (let [handler (:handler options default-handler)]
    (serve handler (merge {:join? false, :open-browser? false} options))))

(deftest serve-test
  (testing "default port"
    (with-server (test-server)
      (is-server-running-on-port 3000)))

  (testing "fallback default ports"
    (with-server (test-server)
      (with-server (test-server)
        (is-server-running-on-port 3000)
        (is-server-running-on-port 3001))))

  (testing "PORT environment variable"
    (with-env {"PORT" "4563"}
      (with-server (test-server)
        (is-server-running-on-port 4563))))

  (testing ":port option"
    (with-server (test-server {:port 5463})
      (is-server-running-on-port 5463)))

  (testing ":port options"
    (with-server (test-server {:port [3564 3565]})
      (is-server-running-on-port 3564)
      (is-server-not-running-on-port 3565)))

  (testing "fallback :port options"
    (let [opts {:port (range 4000 4010)}]
      (with-server (test-server opts)
        (with-server (test-server opts)
          (is-server-running-on-port 4000)
          (is-server-running-on-port 4001)))))

  (testing ":init option"
    (let [ran-init? (atom false)]
      (with-server (test-server {:init #(reset! ran-init? true)})
        (is @ran-init?))))

  (testing ":destroy option"
    (let [ran-destroy? (atom false)]
      (with-server (test-server {:destroy #(reset! ran-destroy? true)})
        (is (not @ran-destroy?)))
      (Thread/sleep 100)
      (is @ran-destroy?)))

  (testing "default middleware"
    (with-server (test-server {:handler exception-handler})
      (let [body (:body (http-get 3000 ""))]
        (is (re-find #"java\.lang\.Exception" body))
        (is (re-find #"testing" body)))))

  (testing "custom stacktrace middleware"
    (let [middleware (fn [handler]
                       (fn [req]
                         (try (handler req)
                              (catch Exception e
                                  {:body "Hello"}))))]
      (with-server (test-server {:handler exception-handler
                                 :stacktrace-middleware middleware})
        (is (= (:body (http-get 3000 ""))
               "Hello"))))))

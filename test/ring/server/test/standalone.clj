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
        (is (re-find #"java\.lang\.Exception: testing" body))))))

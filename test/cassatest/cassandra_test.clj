(ns cassatest.cassandra-test
  (:require [clojure.test :refer :all]
            [cassatest.cassandra :as cassandra]
            [cassatest.cassatest :as cassatest]))


(defn mock-cassandra [counter]
  (reify cassandra/Cassandra
    (-query [_ query params] (swap! counter inc))
    (-close! [_])))

(deftest test-run-queries []
                          ;hosts threads thread-rate-limit query params iterations
                          (let [counter (atom 0)
                                threads 10
                                thread-rate-limit 1000
                                iterations 10]
                            (let [latch (:latch (cassandra/run-queries!
                                                  {:hosts ["localhost"]
                                                   :threads threads
                                                   :thread-rate-limit thread-rate-limit
                                                   :query "select * from test"
                                                   :params {}
                                                   :iterations iterations
                                                   ;;add type :mock for testing
                                                   :type :mock
                                                   :mock (mock-cassandra counter)}))]
                              @latch
                              (is (= @counter (* threads iterations))))))


(deftest test-run-queries-from-main
                         []
                          ;hosts threads thread-rate-limit query params iterations
                          (let [counter (atom 0)
                                threads 10
                                thread-rate-limit 1000
                                iterations 10]
                            (let [latch (:latch (cassatest/run-queries!
                                                  {:hosts ["localhost"]
                                                   :threads threads
                                                   :thread-rate-limit thread-rate-limit
                                                   :query "select * from test"
                                                   :params {}
                                                   :iterations iterations
                                                   ;;add type :mock for testing
                                                   :type :mock
                                                   :mock (mock-cassandra counter)}))]

                              (is (= @counter (* threads iterations))))))

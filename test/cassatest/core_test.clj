(ns cassatest.core-test
  (:require [clojure.test :refer :all]
            [cassatest.exec :as api]
            [cassatest.cassandra :as cassandra]))


;;[state thread-count thread-rate-limit f]

(deftest test-threading []
                        (let [counter (atom 0)
                              test-f (fn [state] (fn [] (dotimes [i 10] (swap! counter inc))))

                              latch (api/via-threads! {} 2 test-f)]

                          @latch
                          (is (= @counter 20))))


(deftest test-rate-limited []
                           (let [counter (atom 0)
                                 test-f (fn [state] (fn [] (swap! counter inc)))
                                 rate-f (api/rate-limited-iterations 100 100 test-f)]

                             ((rate-f {}))
                             (is (= @counter 100))))



(deftest test-rate-limited []
                           (let [counter (atom 0)
                                 test-f (fn [state] (fn [] (swap! counter inc)))
                                 start (System/currentTimeMillis)
                                 rate-f (api/rate-limited-duration 100 5 test-f)]

                             ((rate-f {}))
                             (is (pos? @counter))
                             (is (>= (- (System/currentTimeMillis) start) 5000))))

(deftest test-rate-threading []
                             (let [counter (atom 0)
                                   test-f (fn [state] (fn [] (swap! counter inc)))
                                   rate-f (api/rate-limited-iterations 100 100 test-f)
                                   latch (api/via-threads! {} 2 rate-f)]

                               @latch
                               (is (= @counter 200))))


(deftest test-apply-params []
                           (is (= (cassandra/apply-params "select {a} from {b}" {:a (fn [] 1) :b (fn [] 2)})
                                  "select 1 from 2")))
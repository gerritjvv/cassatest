(ns cassatest.metrics-test
  (:require [clojure.test :refer :all]
            [cassatest.metrics :as metrics]))

(deftest test-metrics []
                      (let [ctx (metrics/start {})]
                        (metrics/mark-query! ctx)

                        (is (= (metrics/queries-counter ctx) 1))))

(ns cassatest.generators-test
  (:require [clojure.test :refer :all]
            [cassatest.generators :as gens])
  (:import (java.util UUID)))


(deftest test-int-range []
                        (let [f (gens/parse-generator {:type :int-range :from 2 :to 5})]
                          (dotimes [i 100]
                            (is (< 1 (f) 5)))))

(deftest test-constant []
                        (let [f (gens/parse-generator {:type :constant :v 10})]
                          (dotimes [i 100]
                            (is (= (f) 10)))))



(deftest test-uuid []
                       (let [f (gens/parse-generator {:type :uuid})]
                         (dotimes [i 100]
                           (is (instance? UUID (f))))))


(deftest test-rand-chars []
                       (let [f (gens/parse-generator {:type :rand-chars :length 4})]
                         (dotimes [i 100]
                           (is (string? (f)))
                           (is (= (count (f)) 4)))))


(deftest test-rand-data []
                         (let [f (gens/parse-generator {:type :rand-data :file "test-resources/times.txt"})]
                           (is (string? (f))))

                         (let [f (gens/parse-generator {:type :rand-data :file "test-resources/times.txt" :last true})]
                           (is (string? (f)))
                           (is (= (f) "5")))

                         (try
                           (do (gens/parse-generator {:type :rand-data :file "test-resources/notexist"})
                               (is false))
                           (catch RuntimeException rte (is true))))
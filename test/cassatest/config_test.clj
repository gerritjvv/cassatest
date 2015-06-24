(ns cassatest.config-test
  (:require [clojure.test :refer :all]
            [cassatest.config :as config]))


(deftest test-parse-params-nil []
                           (is (associative? (config/parse-parmams-arg nil))))

(deftest test-parse-params-edn []
                               (let [{:keys [a b]} (config/parse-parmams-arg "{:a {:type :constant :v 1} :b {:type :constant :v 2}}")]
                                 (is (and (fn? a) (fn? b)))
                                 (is (= (a) 1))
                                 (is (= (b) 2))))

(deftest test-parse-hosts-nil []
                              (is (= (config/parse-hosts-arg nil) config/DEFAULT-HOSTS)))

(deftest test-parse-hosts []
                          (let [[a b c] (config/parse-hosts-arg "a,b,c")]
                            (is (and
                                  (= a "a")
                                  (= b "b")
                                  (= c "c")))))
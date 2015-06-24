(ns cassatest.generators
  (:require
    [clojure.edn :as edn]))
;;
;; usage (parse-generator {:type :int-range :form 1 :to 10})
;;
(set! *unchecked-math* true)

(defmulti gen-create :type)

(defmethod gen-create :int-range [{:keys [^long from ^long to]}]
  (let [diff (- (Math/max from to) (Math/min from to))
        n (Math/min from to)]
    (fn []
      (+ (rand-int diff) n))))

(defmethod gen-create :constant [{:keys [v]}]
  (fn []
    v))

(defn parse-generator
  "Takes a associative argument with format
  {:type :range-int :from number :to number}
  {:type :constant :v value}
  And returns a function that when called will return the value"
  [{:keys [type from to v] :as m} ]
  (cond
    (= type :int-range) (if-not (and (number? from) (number? to))
                          (throw (RuntimeException. (str "Bad definition " m " expecting {:type :int-range :from number :to number}"))))
    (= type :constant) (if-not v
                         (throw (RuntimeException. (str "Bad definition " m " expecting {:type :constant :v value}"))))
    :default
    (throw (RuntimeException. (str "Bad definition type " type " in " m " is not supported please use :int-range or :constant"))))

  (gen-create m))
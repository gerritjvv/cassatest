(ns
  ^{:doc "Automatically generate query values from integer ranges, constants, uuids to random text
         usage (parse-generator {:type :int-range :form 1 :to 10})
         gen-create is a factory pattern that looks for the :type keyword
         each new generator should defmethod on gen-create"}
  cassatest.generators
  (:require
    [clojure.edn :as edn])
  (:import (java.util UUID)))

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

(defmethod gen-create :uuid [_]
  (fn []
    (UUID/randomUUID)))

(defmethod gen-create :rand-chars [{:keys [length] :or {length 4}}]
  (let [seed (vec (map char (range 97 123)))]
    (fn []
      (apply str (take length (shuffle seed))))))


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
    (= type :uuid) nil
    (= type :rand-chars) nil
    :default
    (throw (RuntimeException. (str "Bad definition type " type " in " m " is not supported please use :int-range, :uuid, :rand-chars :constant"))))

  (gen-create m))
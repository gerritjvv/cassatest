(ns cassatest.config
  (:require [clojure.edn :as edn]
            [cassatest.generators :as gen]
            [clojure.string :as string]))


(defonce CONSISTENCY-LEVELS #{:any :one :two :three :quorum :all :serial :local-quorum :each-quorum})

(defonce DEFAULT-CONSISTENCY :one)
(defonce DEFAULT-PARAMS {})
(defonce DEFAULT-HOSTS ["localhost"])
(defonce DEFAULT-THREADS 1)
(defonce DEFAULT-RATE Integer/MAX_VALUE)
(defonce DEFAULT-ITERATIONS 1000)

(defn- edn->gen
  "m must be {:var-name <gen-def> :var-name <gen-def>}
   Returns the same map but with <gen-def> transformed to a generator function"
  [m]
  (reduce-kv (fn [state k v] (assoc state k (gen/parse-generator v))) {} m))

(defn parse-parmams-arg
  "Params should be an EDN string with {:var-name {:type :constant :v value} :var-name2 {:type :int-range :from 0 :to 10}} ...
   Returns this map parsed"
  [s]
  (edn->gen (if (empty? s)
              DEFAULT-PARAMS
              (if-let [m (edn/read-string s)]
                (if-not (associative? m)
                  (throw (RuntimeException. (str "Params must be a map e.g {:var-name {:type :constant :v value} :var-name2 {:type :int-range :from 0 :to 10}}")))
                  m)
                DEFAULT-PARAMS))))

(defn parse-hosts-arg
  "s is a comma, semi comma or space separated string with host names"
  [s]
  (if s
    (string/split s #"[,; ]")
    DEFAULT-HOSTS))

(defn parse-consistency-arg
  [s]
  (let [consistency-level (keyword s)]
    (if (CONSISTENCY-LEVELS consistency-level)
      consistency-level
      (throw (RuntimeException. (str "Consistency Level " consistency-level " is not supported, please use " CONSISTENCY-LEVELS))))))
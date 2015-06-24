(ns cassatest.cassandra
  (:require [cassatest.exec :as exec]
            [clojurewerkz.cassaforte.client :as cc]
            [cassatest.metrics :as metrics]))
;;
;; Usage (def c (connect {:type :cassaforte :hosts [host1 host2]}))
;; (query c sql params)
;; (close! c)
(declare apply-params)


(defprotocol Cassandra
  (-query [conn sql params] "Runs a sql query sql is the sql query and params the parameters to be passed to the query")
  (-close! [conn]))

(defmulti connect :type)

(defmethod connect :cassaforte [{:keys [hosts consistency retry]}]
  (let [session (cc/connect hosts {:consistency-level consistency :retry-policy retry})]
    (reify Cassandra
      (-query [_ sql params]
        (cc/execute session (apply-params sql params)))
      (-close! [_]
        (cc/disconnect session)))))

(defmethod connect :mock [{:keys [mock]}]
  mock)



(defn replace-templates [text m]
  (clojure.string/replace text
                          #"\{\w+\}"
                          (fn [groups]
                            ((->> groups
                                  reverse
                                  (drop 1)
                                  reverse
                                  (drop 1)
                                  (apply str)
                                  keyword) m))))


(defn apply-params
  "sql with {name} where the params should be used
   params a map of {:name gen-f} note that the keys must be keywords"
  [sql params]
  {:pre [(string? sql) (associative? params)]}
  (replace-templates
    sql
    (reduce-kv (fn [state k v] (assoc state k (str (v)))) {} params)))



(defn run-queries!
  "Run queries in multiple threads and rate limited
   Returns a delay that should be used for waiting on completion"
  [{:keys [hosts threads thread-rate-limit query params iterations duration] :as state}]
  {:pre [(coll? hosts) (number? threads) (number? thread-rate-limit) (string? query) (associative? params) (number? iterations)]}
  (let [f (metrics/metrics-f (metrics/start {})
                             (fn [state]
                               (let [state2 (if (:type state) state (assoc state :type :cassaforte))
                                     c (connect state2)]
                                 (fn []
                                   (-query c query params)))))]

    (exec/via-threads! state threads
                       (if duration
                         (exec/rate-limited-duration thread-rate-limit duration f)
                         (exec/rate-limited-iterations thread-rate-limit iterations f)))))
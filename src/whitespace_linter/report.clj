(ns whitespace-linter.report
  (:require [clojure.string :as str]))

(defn- humanize [k]
  (-> (name k)
      (str/replace "-" " ")
      str/capitalize))

(defn pluralize [n {:keys [singular plural]}]
  {:pre [singular plural]}
  (if (= n 1) singular plural))

(defn- header [file warnings]
  (let [n (count warnings)
        problem (pluralize n {:singular "problem" :plural "problems"})]
    (str "=> " n " " problem " in " file)))

(defmulti report-warning
  (fn [_ _ _ warning-location] (type warning-location)))

(defmethod report-warning
  clojure.lang.PersistentHashSet
  [_ step warning-name line-numbers]
  (let [n (count line-numbers)]
    (str/join "\n" (cons
                    (str "   " step ". " warning-name ":")
                    (for [line-number (sort line-numbers)]
                      (str "    - Line " line-number))))))

(defmethod report-warning
  java.lang.Boolean
  [file step warning-name _]
  (str "   " step ". " warning-name))

(def ^:private map-indexed-from-1
  (partial map-indexed (fn [i x] [(inc i) x])))

(defn- describe-error [[file warnings]]
  (str/join "\n\n"
            (cons (header file warnings)
                  (for [[step [warning-keyword warning-location]]
                        (map-indexed-from-1 warnings)]
                    (report-warning file
                                    step
                                    (humanize warning-keyword)
                                    warning-location)))))

(defn print-report [errors]
  (if (seq errors)
    (println (->> errors
                  (map describe-error)
                  (str/join "\n\n")))))

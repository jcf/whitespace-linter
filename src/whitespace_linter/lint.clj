(ns whitespace-linter.lint
  (:require [clojure.string :as str]
            [whitespace-linter.check :as check]))

(defn- new-file [file contents]
  {:path file :content contents})

(defn- check-file [file file-contents]
  (reduce-kv
   (fn [agg check-name checker]
     (if (checker (new-file file file-contents))
       (assoc agg check-name true)
       agg))
   {}
   check/file-checks))

(defn- new-line [file line-number content]
  {:path file :line line-number :content content})

(defn- numbered-lines [file file-contents]
  (map-indexed
   (fn [i content] (new-line file (inc i) content))
   (str/split-lines file-contents)))

(defn- check-lines [file file-contents]
  (let [lines (numbered-lines file file-contents)]
    (reduce-kv
     (fn [agg check-name checker]
       (let [errors (checker lines)]
         (if (seq errors)
           (assoc agg check-name (set (map :line errors)))
           agg)))
     {}
     check/line-checks)))

;; {java.io.File {:long-lines #{2 4 32}
;;                :trailing-whitespace #{3 5}}}
(defn- validate-file [agg file read-contents]
  (let [file-contents (read-contents)
        file-errors (check-file file file-contents)
        line-errors (check-lines file file-contents)
        errors (merge file-errors line-errors)]
    (if (seq errors)
      (assoc agg file errors)
      agg)))

(defn validate-files [files]
  (reduce-kv validate-file {} files))

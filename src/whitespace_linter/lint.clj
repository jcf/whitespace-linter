(ns whitespace-linter.lint
  "The guts of whitespace-linter. This namespace takes a hash-map of
  files and their contents, and applies each file checker and line
  checker in turn.

  Each file checker is given a hash-map of properties about the file
  to be checked, and each line checker is given a hash-map of
  properties about the line to be checked.

  The results of each checker are aggregated into a hash-map. An
  example of which might look like:

      {#<File example/file.txt> {:long-lines #{1 44 86}
                                 :missing-final-newline true}}

  For more information on adding a line or file-based checker see
  `whitespace-linter.check`."
  (:require [clojure.string :as str]
            [whitespace-linter.check :as check]))

(defn- new-file [file contents]
  {:path file :content contents})

(defn- check-file [args file file-contents]
  (reduce-kv
   (fn [agg check-name checker]
     (let [{errors :errors
            warnings :warnings} (checker args (new-file file file-contents))]
       (cond-> agg
         errors (assoc-in  [:errors check-name] true)
         warnings (assoc-in [:warnings check-name] true))))
   {}
   check/file-checks))

(defn- new-line [file line-number content]
  {:path file :line line-number :content content})

(defn- numbered-lines [file file-contents]
  (map-indexed
   (fn [i content] (new-line file (inc i) content))
   (str/split-lines file-contents)))

(defn- check-lines [args file file-contents]
  (let [lines (numbered-lines file file-contents)]
    (reduce-kv
     (fn [agg check-name checker]
       (let [{:keys [errors warnings]} (checker args lines)]
         (cond-> agg
           (seq errors)
           (assoc-in [:errors check-name] (set (map :line errors)))
           (seq warnings)
           (assoc-in [:warnings check-name] (set (map :line warnings))))))
     {}
     check/line-checks)))

(defn- validate-file [args agg file read-contents]
  (let [file-contents (read-contents)
        {file-errors :errors
         file-warnings :warnings} (check-file args file file-contents)
        {line-errors :errors
         line-warnings :warnings} (check-lines args file file-contents)
        errors (merge file-errors line-errors)
        warnings (merge file-warnings line-warnings)]
    (cond-> agg
      (seq errors) (assoc-in [:errors file] errors)
      (seq warnings) (assoc-in [:warnings file] warnings))))

(defn validate-files [args files]
  (reduce-kv (partial validate-file args) {} files))

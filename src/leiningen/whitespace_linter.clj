(ns leiningen.whitespace-linter
  (:require [whitespace-linter.file :as file]
            [whitespace-linter.lint :as lint]
            [whitespace-linter.report :as report]))

;; - Find every file
;; - Filter down the list
;; - Apply desired checks
;; - Return result

(defn- project->files
  "Returns a list of all files in :source-paths and :test-paths."
  [project]
  (concat
   (mapcat file/find-files (:source-paths project))
   (mapcat file/find-files (:test-paths project))))

(defn- args->files [args]
  (some-> (seq args)
          file/select-files))

(defn- header [files elapsed]
  (let [n (count files)
        word (report/pluralize n {:singular "file" :plural "files"})]
    (format "=> Found %d %s in %.3f ms." n word elapsed)))

(defn- done [_ elapsed]
  (format "=> Linting took %.3f ms." elapsed))

(defmacro ^:private profile [& body]
  `(let [t0#  (System/nanoTime)
         result# ~@body
         t1#  (System/nanoTime)]
     [result# (/ (- t1# t0#) 1e6)]))

(defn read-files [args project]
  (file/read-files (or (args->files args)
                       (project->files project))))

(defn whitespace-linter
  "I don't do a lot."
  [project & args]
  (let [[files t1] (profile (read-files args project))
        _ (println (header files t1))

        [errors t2] (profile (lint/validate-files files))
        _ (println (done files t2))]

    (if (seq errors)
      (do (report/print-report errors)
          (System/exit 1)))))

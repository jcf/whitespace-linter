(ns leiningen.whitespace-linter
  (:require [whitespace-linter.file :as file]
            [whitespace-linter.lint :as lint]
            [whitespace-linter.report :as report]))

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

(def numeric-options
  #{:warn-line-width
    :error-line-width})

(defn- parse-arg [[k v]]
  (let [kw (keyword (subs k 1))]
    (if (numeric-options kw)
      [kw (Integer/parseInt v)]
      [kw v])))

(defn- parse-args [args]
  (let [arg-count (count args)]
    (if (even? arg-count)
      (into {} (map parse-arg (apply hash-map args)))
      (throw
       (ex-info (str "Even number of forms required! got: " arg-count)
                {:args args
                 :causes #{:invalid-args}})))))

(defn whitespace-linter
  "Checks files for whitespace-related issues.

  Reads all files in :source-paths and :test-paths and lints them
  using a series of whitespace-related checks.

  An optional list of files to lint can be provided. This optional
  list will be used in place of :source-paths and :test-paths when
  provided.

  A glob can be used to lint only the files you care about like so:

      $ lein whitespace-linter :path app/{scripts,styles}/**/*

  Additionally, you may pass the :warn-line-width option to throw a warning,
  but not throw an error exit code on a `soft limit` on line length as well as
  override the default line length limit of 80 with the :error-line-width
  option"
  [project & args]
  (let [arg-map (parse-args args)
        _ (println "Options: " arg-map)
        path (:path arg-map)
        [files t1] (profile (read-files path project))
        _ (println (header files t1))

        [{errors :errors
          warnings :warnings} t2] (profile (lint/validate-files arg-map files))
        _ (println (done files t2))]

    (when warnings
      (do (println "Warnings:\n")
          (report/print-report warnings)))

    (if (seq errors)
      (do (println "Errors:\n")
          (report/print-report errors)
          (System/exit 1)))))

(ns whitespace-linter.file
  (:require [clojure.java.io :as io]))

(defn- read-file [path]
  [path #(slurp path)])

(defn find-files
  "Recursively finds files in `directory`"
  [directory]
  (->> (io/file directory)
       file-seq
       (filter #(.isFile %))))

(defn select-files
  "Filters the given collection, returning only the entries that are
  files."
  [coll]
  (->> (map io/file coll)
       (filter #(.isFile %))))

(defn read-files
  "Returns a hash-map of java.io.File instances mapped to a function
  that when called will read the contents into memory."
  [files]
  (into {} (map read-file files)))

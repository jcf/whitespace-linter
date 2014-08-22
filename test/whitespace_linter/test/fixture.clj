(ns whitespace-linter.test.fixture
  (:require [clojure.java.io :as io]
            [whitespace-linter.file :as file]))

(def fixture-folder
  "fixtures")

(defn fixture-file [filename]
  (io/file fixture-folder filename))

(defn load-fixtures []
  (-> fixture-folder
      file/find-files
      file/read-files))

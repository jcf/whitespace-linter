(ns whitespace-linter.leiningen-test
  (:require [clojure.test :refer :all]
            [leiningen.whitespace-linter :refer :all]
            [whitespace-linter.test.fixture :as fixture]))

(defn- files->names [files]
  (->> files keys sort))

(deftest test-read-files
  (let [project {:source-paths [fixture/fixture-folder]}]
    (testing "with an explicit sequence of files"
      (let [messy-fixture (fixture/fixture-file "messy.txt")
            files (read-files [messy-fixture] project)
            filenames (files->names files)]
        (is (= filenames [messy-fixture]))))

    (testing "with no sequence of files"
      (let [files (read-files [] project)
            filenames (files->names files)
            all-fixtures (map fixture/fixture-file
                              ["messy.txt" "report.txt" "tidy.txt"])]
        (is (= filenames all-fixtures))))))

(ns leiningen.whitespace-linter-test
  (:require [clojure.test :refer :all]
            [leiningen.whitespace-linter :refer :all]))

(defn- files->names [files]
  (->> files keys (map str) sort))

(deftest test-read-files
  (let [project {:source-paths ["test/fixtures"]}]
    (testing "with an explicit sequence of files"
      (let [files (read-files ["test/fixtures/messy.txt"] project)
            filenames (files->names files)]
        (is (= filenames ["test/fixtures/messy.txt"]))))

    (testing "with no sequence of files"
      (let [files (read-files [] project)
            filenames (files->names files)]
        (is (= filenames ["test/fixtures/messy.txt"
                          "test/fixtures/report.txt"
                          "test/fixtures/tidy.txt"]))))))

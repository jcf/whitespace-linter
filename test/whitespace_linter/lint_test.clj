(ns whitespace-linter.lint-test
  (:require [clojure.test :refer :all]
            [whitespace-linter.file :as file]
            [whitespace-linter.lint :refer :all]))

(defn load-fixture [path]
  (-> path
      file/find-files
      file/read-files))

(deftest test-validate-files
  (let [results (validate-files (load-fixture "test/fixtures"))
        result (first results)
        file (first result)
        errors (last result)]

    (is (= (count results) 1))
    (is (= (str file) "test/fixtures/messy.txt"))

    (are [k line-numbers] (= (k errors) line-numbers)
         :hard-tabs #{3 4 5}
         :trailing-whitespace #{3}
         :long-lines #{1})

    (is (:missing-final-newline errors))))

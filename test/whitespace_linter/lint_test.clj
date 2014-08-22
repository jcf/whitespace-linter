(ns whitespace-linter.lint-test
  (:require [clojure.test :refer :all]
            [whitespace-linter.lint :refer :all]
            [whitespace-linter.test.fixture :as fixture]))

(deftest test-validate-files
  (let [results (validate-files (fixture/load-fixtures))
        result (first results)
        file (first result)
        errors (last result)]

    (is (= (count results) 1))
    (is (= file (fixture/fixture-file "messy.txt")))

    (are [k line-numbers] (= (k errors) line-numbers)
         :hard-tabs #{3 4 5}
         :trailing-whitespace #{3}
         :long-lines #{1})

    (is (:missing-final-newline errors))))

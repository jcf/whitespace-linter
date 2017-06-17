(ns whitespace-linter.lint-test
  (:require [clojure.test :refer :all]
            [whitespace-linter.lint :refer :all]
            [whitespace-linter.test.fixture :as fixture]))

(deftest test-validate-files
  (let [{results :errors} (validate-files {} (fixture/load-fixtures))
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


(deftest test-validate-files-with-warnings
  (let [{err-results :errors
         warn-results :warnings} (validate-files {:warn-line-width 68}
                                                 (fixture/load-fixtures))
        result (first err-results)
        file (first result)
        errors (last result)
        warning (first warn-results)
        warn-file (first warning)
        warnings (last warning)]

    (is (= (count err-results) 1))
    (is (= file (fixture/fixture-file "messy.txt")))

    (are [k line-numbers] (= (k errors) line-numbers)
      :hard-tabs #{3 4 5}
      :trailing-whitespace #{3}
      :long-lines #{1})

    (is (:missing-final-newline errors))

    (is (= (count warn-results) 1))
    (is (= warn-file (fixture/fixture-file "tidy.txt")))
    (is (= (:long-lines warnings) #{2}))))

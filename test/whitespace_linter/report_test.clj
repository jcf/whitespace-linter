(ns whitespace-linter.report-test
  (:require [clojure.string :as str]
            [clojure.test :refer :all]
            [whitespace-linter.report :refer :all]
            [whitespace-linter.test.fixture :as fixture]))

(def ^:private errors
  {"/path/to/file.clj" {:line-error #{1 2 3}
                        :file-error true}})

(deftest test-print-report
  (let [report (with-out-str (print-report errors))]
    (is (= report (slurp (fixture/fixture-file "report.txt"))))))

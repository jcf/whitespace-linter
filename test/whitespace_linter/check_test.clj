(ns whitespace-linter.check-test
  (:require [clojure.test :refer :all]
            [whitespace-linter.check :refer :all]))

(defn- letters [n]
  (apply str (repeat n "a")))

(defn- lines [failing-content]
  [{:line 1 :content failing-content}
   {:line 2 :content "Hello"}])

(deftest test-hard-tabs
  (testing "with leading hard tabs"
    (let [result (:errors (hard-tabs {} (lines "\tHello")))]
      (is (= (count result) 1))
      (is (= (-> result first :line) 1))))

  (testing "with trailing hard tabs"
    (let [result (:errors (hard-tabs {} (lines "Hello\t")))]
      (is (= (count result) 1))
      (is (= (-> result first :line) 1)))))

(deftest test-trailing-whitespace
  (testing "with trailing spaces"
    (let [result (:errors (trailing-whitespace {} (lines "Hello ")))]
      (is (= (count result) 1))
      (is (= (-> result first :line) 1))))

  (testing "with trailing hard tabs"
    (let [result (:errors (trailing-whitespace {} (lines "Hello\t")))]
      (is (= (count result) 1))
      (is (= (-> result first :line) 1)))))

(deftest test-long-lines
  (testing "with exactly 80 letters"
    (let [result (:errors (long-lines {} (lines (letters 80))))]
      (is (= (count result) 0))))

  (testing "newline not counted as char in max"
    (let [result (:errors (long-lines {} (lines (str (letters 80) "\n"))))]
      (is (= (count result) 0))))

  (testing "warnings on line-width options"
    (let [{warnings :warnings
           errors :errors} (long-lines {:warn-line-width 68}
                                       (lines (letters 69)))]
      (is (= (count warnings) 1))
      (is (= (count errors) 0))))

  (testing "with more than 80 letters"
    (let [result (:errors (long-lines {} (lines (letters 81))))]
      (is (= (count result) 1))
      (is (= (-> result first :line) 1)))))

(deftest test-missing-final-newline?
  (testing "with a newline at the end of the file"
    (let [result (missing-final-newline? {} {:path "foo.txt"
                                             :content "Hello\n"})]
      (is (false? (:errors result)))))

  (testing "with no new line at the end of the file"
    (let [result (missing-final-newline? {} {:path "foo.txt" :content "Hello"})]
      (is (true? (:errors result))))))

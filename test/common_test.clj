(ns common-test
  (require [clojure.test :refer :all]
           [common :refer :all])
  (import [java.io StringWriter]))

(deftest test-eprintln
  (testing "Test that eprintln is really sending to stderr and not stdout"
    (let [local-out (StringWriter.)
          local-err (StringWriter.)]
      (binding [*out* local-out
                *err* local-err]
        (eprintln "Foo"))
      (is (= "Foo\n" (str local-err)))
      (is (empty? (str local-out))))))

(deftest test-concat-unique
  (testing "Test that concat-unique operates as concat, without duplicates,
            and duplicates to be moved back in the list"
    (is (= [] (concat-unique [] [])))
    (is (= [1 2 3] (concat-unique [] [1 2 3])))
    (is (= [1 2 3] (concat-unique [1 2 3] [])))
    (is (= [1 2 3 4 5 6] (concat-unique [1 2 3] [4 5 6])))
    (is (= [1 2 3] (concat-unique [1 2 3] [1 2 3])))
    (is (= [1 2 3 4 5] (concat-unique [1 2 3] [3 4 5])))
    (is (= [1 3 2 4 5] (concat-unique [1 2 3] [2 4 5])))))

(deftest test-fixpoint
  (testing "Test that fixpoint creates a function that settles on a final value.
            We can't do failure testing, as this would create an infinite loop."
    (letfn [(inc-to-10 [x] (if (< x 10) (inc x) x))]
      (is (= 1 (inc-to-10 0)))
      (is (= 10 (inc-to-10 9)))
      (is (= 10 (inc-to-10 10)))
      (let [fp-to-10 (partial fixpoint inc-to-10)]
        (is (= 10 (fp-to-10 0)))
        (is (= 10 (fp-to-10 9)))
        (is (= 10 (fp-to-10 10)))))))


(ns detective-test
  (:require [clojure.test :refer :all]
            [detective :refer :all]))

(deftest test-longest-common-sequence
  (testing "Test the longest-common-sequence function, looking for common sequences."
    (is (= [[]] (longest-common-sequence [] [])))
    (is (= [[]] (longest-common-sequence [1 2 3] [])))
    (is (= [[1]] (longest-common-sequence [1 2 3] [1])))
    (is (= [[2 3 4]] (longest-common-sequence [1 2 3 4] [2 3 4 5 6 7])))
    (is (= [[2 5]] (longest-common-sequence [1 2 3 5] [2 4 5 6 7])))
    (is (= [[3 6 7]] (longest-common-sequence [1 3 4 6 7 8] [2 3 5 6 7 9])))))

(deftest test-next-variant
  (testing "Test the next-variant reduction function. Does it slice out the expected elements?
            The guarantees are
            - the second parameter will always be common in both the :a and :b sequences
            - the second parameter will always be the NEXT common value in both the :a and :b sequences"
    ;; find 2, and append it
    (is (= {:results [[2]] :a [] :b []} (next-variant {:results [[]] :a [2] :b [2]} 2)))
    ;; find 2, and append what led to it in b
    (is (= {:results [[1 2]] :a [] :b []} (next-variant {:results [[]] :a [2] :b [1 2]} 2)))
    (is (= {:results [[0 1 2]] :a [] :b []} (next-variant {:results [[0]] :a [2] :b [1 2]} 2)))
    ;; find 4, and append the 2 different paths that get to it
    (is (= {:results [[0 3 4] [0 2 4]] :a [] :b []} (next-variant {:results [[0]] :a [3 4] :b [2 4]} 4)))
    (is (= {:results [[0 2 4] [0 1 3 4]] :a [] :b []} (next-variant {:results [[0]] :a [2 4] :b [1 3 4]} 4)))
    ;; find 4, and append the 2 different paths that get to it. Data will be left behind in :a or :b
    (is (= {:results [[2 4] [1 3 4]] :a [5] :b []} (next-variant {:results [[]] :a [2 4 5] :b [1 3 4]} 4)))
    (is (= {:results [[2 4] [1 3 4]] :a [] :b [6]} (next-variant {:results [[]] :a [2 4] :b [1 3 4 6]} 4)))
    (is (= {:results [[2 4] [1 3 4]] :a [5] :b [6]} (next-variant {:results [[]] :a [2 4 5] :b [1 3 4 6]} 4)))
    ;; find 4, and perform a cross product of the multiple paths onto the multiple existing paths
    (is (= {:results [[0 3 4] [0 2 4] [1 3 4] [1 2 4]] :a [] :b []} (next-variant {:results [[0] [1]] :a [3 4] :b [2 4]} 4)))
    ;; find 4, perform a cross product of the multiple paths onto the multiple existing paths, and leave data in :a and :b
    (is (= {:results [[0 3 4] [0 2 4] [1 3 4] [1 2 4]] :a [] :b [5 7]} (next-variant {:results [[0] [1]] :a [3 4] :b [2 4 5 7]} 4)))
    (is (= {:results [[0 3 4] [0 2 4] [1 3 4] [1 2 4]] :a [6] :b []} (next-variant {:results [[0] [1]] :a [3 4 6] :b [2 4]} 4)))
    (is (= {:results [[0 3 4] [0 2 4] [1 3 4] [1 2 4]] :a [6] :b [5 7]} (next-variant {:results [[0] [1]] :a [3 4 6] :b [2 4 5 7]} 4)))))


;; The following are automated tests from the provided example files

(deftest simple-pair-test
  (testing "Test a single pair, with nothing trailing"
    (let [ex1 [["fight", "gunshot", "fleeing"]
               ["gunshot", "falling", "fleeing"]]]
      (is (= [["fight","gunshot","falling","fleeing"]]
             (final-merge-timelines ex1))))))

(deftest partial-merge-test
  (testing "Test a single pair, with more than 1 possibility for merging"
    (let [ex2 [["shadowy figure", "demands", "scream", "siren"],
               ["shadowy figure", "pointed gun", "scream"]]]
      (is (= [["shadowy figure","demands","scream","siren"]
              ["shadowy figure","pointed gun","scream","siren"]]
             (final-merge-timelines ex2))))))

(deftest unmerge-test
  (testing "Test 3 sequences that have no merge"
    (let [ex3 [["argument", "stuff", "pointing"],
               ["press brief", "scandal", "pointing"],
               ["bribe", "coverup"]]]
      (is (= [["argument","stuff","pointing"],["press brief","scandal","pointing"]
              ["bribe","coverup"]]
             (final-merge-timelines ex3))))))

(deftest multi-pass-merge
  (testing "Test a series of sequences that merge in more than one step"
    (let [ex4 [["0"]
               ["1"]
               ["2"]
               ["3"]
               ["0", "1"]
               ["0", "2"]
               ["0", "3"]
               ["1", "2"]
               ["1", "3"]
               ["2", "3"]
               ["0", "1", "2"]
               ["0", "1", "3"]
               ["0", "2", "3"]
               ["1", "2", "3"]]]
      (is (= [["0" "2" "3"] ["1" "2" "3"] ["0" "1" "2" "3"]]
             (merge-timelines ex4)))
      (is (= [["0", "1", "2", "3"]]
             (final-merge-timelines ex4))))))


(ns detective
  "Contains the entry point, and principal functions for evaluating merges between multiple timelines"
  (:require [common :refer [concat-unique fixpoint eprintln]]
            [clojure.java.io :as io]
            [clojure.data.json :as json])
  (:import [clojure.lang ExceptionInfo]
           [java.io IOException])
  (:gen-class))


(defn longest-common-sequence
  "Find the Longest Common Sequence between two sequences.
   Uses dynamic programming to recursively search through each sequence for matching elements."
  [[f1 & r1 :as data1] [f2 & r2 :as data2]]
  (if (or (empty? data1) (empty? data2))
    [[]]                ;; a single empty sequence to build on
    (if (= f1 f2)
      (map #(cons f1 %) (longest-common-sequence r1 r2))
      (let [[af :as a] (longest-common-sequence r1 data2)
            [bf :as b] (longest-common-sequence data1 r2)]
        (cond
          (> (count af) (count bf)) a
          (< (count af) (count bf)) b
          :default (concat-unique a b))))))


(defn next-variant
  "A reducing function to accumulate results in the first param as a structure.
   The c parameter is the next common element (found in the longest common sequence) to be processed.
   Slice off the beginning of each timeline up to this common element.
   - If only one timeline contains this leading component, then this is the only valid timeline. Append
     this leading component to each of the accumulated variations.
   - If more that one timeline contains a leading component, then this forms a new variation, leading to
     a cross product. This is built by mapping the accumulated components so far across all the leading
     components."
  [{:keys [results a b]} c]
  ;; get the leading elements up to the first common element
  (let [not-cw (complement #{c})
        leaders (or (seq (keep #(seq (take-while not-cw %)) [a b]))
                    [[]])]  ;; single empty sequence if no leading elements
    ;; add the leaders to the result, drop the scanned elements
    {:results (mapcat (fn [e] (map #(concat e % [c]) leaders)) results)
     :a (rest (drop-while not-cw a))
     :b (rest (drop-while not-cw b))}))


(defn merge-pair
  "Identifies a merge between a pair of timelines. This finds the longest common subsequence between them
   then works through each timeline to the next common element using next-variant. This function step through
   each timeline to the next common element, accumulating those slices into the final result. Once the common
   elements have been traversed, anything trailing is also appended, possibly creating a cross product in the
   results when both timelines have non-mergable trailing sequences."
  [a b]
  (let [commons (longest-common-sequence a b)
        nr-commons (count commons)]

    ;; multiple maximum subsequences appear to be beyond the scope of the requirements.
    (when (> nr-commons 1)
      (throw (ex-info "More than one set of common sequences" {:seqs commons})))
    
    (if (zero? nr-commons)

      ;; nothing in common just returns the inputs
      [a b]

      (let [common (first commons)  ;; there is only 1 common longest-common-sequence, this extracts that single value

            ;; step through the common results, adding non-common data in between, as appropriate
            ;; start with a single empty sequence
            {:keys [results a b]} (reduce next-variant {:results [[]] :a a :b b} common)

            ;; anything after the final common element gets appended, or return a single empty sequence
            tail (or (seq (keep seq [a b])) [[]])]

        ;; cross product the trailing elements to create the known variations
        (mapcat (fn [t] (map #(concat % t) results)) tail)))))


(defn merge-timelines
  "Takes a series of timelines and merge them through pairwise operations."
  [inputs]
  (reduce (fn [acc input] (apply concat-unique (map #(merge-pair % input) acc)))
          [[]]  ;; Start with a single empty timeline
          inputs))


;; The final merge operation may need to be re-executed on the output, so use a fixpoint to get there
(def final-merge-timelines (partial fixpoint merge-timelines))


(defn validate-input
  "Ensure that the input data is of the expected format: Arrays of Arrays.
   Inner array elements are typically strings, but may be other types."
  [data]
  (when-not (sequential? data)
    (throw (ex-info "Input must be a JSON array" {:input data})))
  (when-not (every? sequential? data)
    (throw (ex-info "Timelines must each be arrays of data" {:input data}))))


(defn -main
  "Process the file provided on the command line. Uses stdin if no input file is provided.
   Prints results to stdout"
  [& [input-file]]
  (try
    (with-open [file (if input-file (io/reader input-file) *in*)]  ;; open the given file, or stdin
      (let [data (json/read file)]

        ;; minimal type checking on the input
        (validate-input data)

        ;; perform the operation, and print the result
        (json/write (final-merge-timelines data) *out*)
        (println)))

    (catch IOException i (eprintln "File error: " (.getMessage i)))
    (catch ExceptionInfo e (eprintln "Error: " (.getMessage e)))))


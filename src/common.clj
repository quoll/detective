(ns common
  "A namespace implementing some common operations that are not included in clojure.core.")


(defn concat-unique
  "A version of concat that removes duplicates from the sequence.
   Repeats of an element will shift that element to the end of the concatenation."
  ([a] a)
  ([a b] (concat (remove (set b) a) b))
  ([a b & r]
    (apply concat-unique (concat-unique a b) r)))


(defn fixpoint
  "Iterate on a function until the result is a fixpoint."
  [f a]
  (let [s (iterate f a)]
    (some identity (map #(#{%1} %2) s (rest s)))))


(defn eprintln
  "Prints to stderr, and finishes with a newline"
  [& args]
  (binding [*out* *err*]
    (apply println args)))


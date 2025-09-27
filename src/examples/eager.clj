(ns examples.eager)

(defn square [x] (* x x))

(defn sum-squares-seq [n]
  (vec (map square (range n))))

(defn sum-squares
  [n]
  (into [] (map square) (range n)))



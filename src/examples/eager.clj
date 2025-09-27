(ns examples.eager)

(defn square [x] (* x x))

(defn sum-squares-seq [n]
  (vec (map square (range n))))

(defn sum-squares
  [n]
  (into [] (map square) (range n)))

(defn preds-seq []                                          ; "Elapsed time: 3.607412 msecs"
  (->> (all-ns)
       (map ns-publics)
       (mapcat vals)
       (filter #(clojure.string/ends-with? % "?"))
       (map #(str (.-sym %)))
       vec))

(defn preds []                                              ; "Elapsed time: 3.053389 msecs"
  (into []                                                  ; Output
        (comp                                               ; Transducer
          (map ns-publics)
          (mapcat vals)
          (filter #(clojure.string/ends-with? % "?"))
          (map #(str (.-sym %))))
        (all-ns)))                                          ; Input

(defn non-blank? [s]
  (not (clojure.string/blank? s)))

(defn non-blank-lines-seq [file-name]
  (let [rdr (clojure.java.io/reader file-name)]
    (filter non-blank? (line-seq rdr))))

; WARN: This doesn't work because as return a lazy list
; after the first uses the stream will be closed.
(defn non-blank-lines-seq-with [file-name]
  (with-open [rdr (clojure.java.io/reader file-name)]
    (filter non-blank? (line-seq rdr))))

(defn non-blank-lines [file-name]
  (with-open [rdr (clojure.java.io/reader file-name)]
    (into [] (filter non-blank?) (line-seq rdr))))

(defn non-blank-lines-eduction [rdr]
  (eduction (filter non-blank?) (line-seq rdr)))

(defn line-count [file-name]
  (with-open [rdr (clojure.java.io/reader file-name)]
    (reduce (fn [cnt el] (inc cnt)) 0 (non-blank-lines-eduction rdr))))
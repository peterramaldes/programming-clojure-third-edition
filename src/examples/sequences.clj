(ns examples.sequences
  (:require [clojure.java.io :refer [reader]]
            [clojure.string :refer [blank?]]
            [clojure.set :refer :all]))

;; Don't do this.
(let [m (re-matcher #"\w+" "the quick brown fox")]
  (loop [match (re-find m)]
    (when match
      (println match)
      (recur (re-find m)))))

(defn minutes-to-millis [mins] (* mins 1000 60))

(defn recently-modified? [file]
  (> (.lastModified file)
     (- (System/currentTimeMillis) (minutes-to-millis 30))))

(defn non-blank? [line] (not (blank? line)))

(defn non-git? [file] (not (.contains (.toString file) ".git")))

(defn clojure-source? [file] (.endsWith (.toString file) ".clj"))

(defn clojure-loc [base-file]
  (reduce
   +
   (for [file (file-seq base-file)
         :when (and (clojure-source? file) (non-git? file))]
     (with-open [rdr (reader file)]
       (count (filter non-blank? (line-seq rdr)))))))

(def song {:name "Agnus Dei"
           :artist "Krzysztof Penderecki"
           :album "Polish Requiem"
           :genre "Classical"})

(comment
  (assoc song :kind "MPEG Audio File")
  (dissoc song :genre)
  (select-keys song [:name :artist])
  (merge song {:size 8118166 :time 507245}))

(merge-with
 concat
 {:rubble ["Barney"], :flintstone ["Fred"]}
 {:rubble ["Bett"], :flintstone ["Wilma"]}
 {:rubble ["Bam-Bam"], :flintstone ["Pebbles"]})

(def languages #{"java" "c" "d" "clojure"})
(def beverages #{"java" "chai" "pop"})

(comment
  (union languages beverages)
  (intersection languages beverages)
  (difference languages beverages)
  (select #(= 1 (count %)) languages)
  )

(def compositions
  #{{:name "The Art of the Fugue" :composer "J. S. Bach"}
    {:name "Musical Offering" :composer "J. S. Bach"}
    {:name "Requiem" :composer "Giuseppe Verdi"}
    {:name "Requiem" :composer "W. A. Mozart"}})

(def composers
  #{{:composer "J. S. Bach" :country "Germany"}
    {:composer "W. A. Mozart" :country "Austria"}
    {:composer "Giuseppe Verdi" :country "Italy"}})

(def nations
  #{{:nation "Germany" :language "German"}
    {:nation "Austria" :language "German"}
    {:nation "Italy" :language "Italian"}})
    
(comment
  (rename compositions {:name :title})           ; Similar to update column name
  (select #(= (:name %) "Requiem") compositions) ; Similar with where
  (project compositions [:name])                 ; Similar with select
  )

(comment
  (for [m compositions c composers] (concat m c)) ; Similar to join
  (join compositions composers)                   ; there's a match on the key/value from/to compositions composer
  (join composers nations {:country :nation})     ; on country = nation
  (project
   (join
    (select #(= (:name %) "Requiem") compositions)
    composers)
   [:country])
  )
   
(defn parity [n]
  (loop [n n par 0]
    (if (= n 0)
      par
      (recur (dec n) (- 1 par)))))

(defn my-even? [n] (= 0 (parity n)))
(defn my-odd? [n] (= 1 (parity n)))

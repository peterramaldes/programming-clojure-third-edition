(ns examples.concurrency
  (:require [examples.chat :refer :all]))

(def counter (ref 0))

(defn next-counter [] (dosync (alter counter inc)))

(comment
  (next-counter)
  )

(def backup-agent (agent "output/messages-backup.clj"))

(defn add-message-with-backup [msg]
  (dosync
    (let [snapshot (commute messages conj msg)]
      (send-off backup-agent (fn [filename] (spit filename snapshot) filename)) snapshot)))


(ns examples.chat)

(defrecord Message [sender text])

(->Message "Aaron" "Hello")

(def messages (ref ()))

; bad idea
(defn naive-add-message [msg]
  (dosync (ref-set messages (cons msg @messages))))

(defn add-message [msg]
  (dosync (alter messages conj msg)))

(comment
  (add-message "Foo")
  (add-message (->Message "User 1" "Hello"))
  (add-message (->Message "User 2" "Hello from User 2"))
  )

(defn add-message-commute [msg]
  (dosync (commute messages conj msg)))

(comment
  (add-message-commute (->Message "User 3" "Bar"))
  )

(defn valid-message? [msg]
  (and (:sender msg) (:text msg)))

(def validate-message-list #(every? valid-message? %))

(def messages (ref () :validator validate-message-list))

(comment
  (add-message "foo")
  (add-message (->Message "User 1" "Foo"))
  @messages
  )

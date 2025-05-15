(ns search
  (:require
   [clojure.java.io :as io]
   [medley.core :as m]
   [clojure.string :as string]))

(defonce index (atom {}))

(defn extract-words
  [text]
  (re-seq #"\w+" text))

(defn reset-index!
  []
  (reset! index {}))

(defn get-words-frequencies
  [text doc-id]
  (->> text
       extract-words
       (map string/lower-case)
       frequencies
       (m/map-vals (fn [freq] {doc-id freq}))))

(defn ingest
  [text doc-id]
  (let [freqs (get-words-frequencies text doc-id)
        my-merge #(merge-with merge % freqs)]
    (swap! index my-merge)))

(defn search
  [text]
  (let [words (extract-words text)]
    (->> words
         (map #(get @index % {}) ) 
         (apply merge-with +)
         (m/map-vals #(/ % (count words)))
         (into [])
         (sort-by (complement second)))))

(defn -main [& args]
  )

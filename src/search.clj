(ns search
  (:require
   [medley.core :as m]
   [clojure.string :as string]))

(defonce index (atom {}))

(defn reset-index!
  []
  (reset! index {}))

(defn get-words-frequencies
  [text doc-id]
  (->> text
       (re-seq #"\w+")
       (map string/lower-case)
       frequencies
       (m/map-vals (fn [freq] {doc-id freq}))))

(defn ingest
  [text doc-id]
  (let [freqs (get-words-frequencies text doc-id)
        my-merge #(merge-with merge % freqs)]
    (swap! index my-merge)))

(defn search
  [word]
  (get @index word {}))

(defn -main [& args]
  )

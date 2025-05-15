(ns search
  (:require
   [clojure.java.io :as io]
   [clojure.string :as string]
   [clojure.tools.cli :refer [parse-opts]]
   [medley.core :as m]))

(def MAX_RECIPES 10)
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
  [index text]
  (let [words (extract-words text)]
    (->> words
         (map #(get index % {}) ) 
         (apply merge-with +)
         (m/map-vals #(/ % (count words)))
         (into [])
         (sort-by second)
         reverse
         (take MAX_RECIPES))))

(defn format-results
  [results]
  (for [[doc-id rank] results]
    (format "%s:%s" doc-id (float rank))))

(def cli-options
  [["-h" "--help"]])

(defn ingest-directory
  [dir]
  (reset-index!)
  (doseq [f (file-seq (io/file dir))
          :when (.isFile f)]
    (ingest (slurp f) (str f))))

(defn -main [& args]
  (let [{:keys [arguments]} (parse-opts args cli-options)
        [dir text] arguments]
    (ingest-directory dir)
    (->> text
         (search @index)
         format-results
         (string/join "\n")
         printf)))

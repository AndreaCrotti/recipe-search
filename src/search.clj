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

(defn rank
  "The rank algorithm does a simple ratio of words that appear in each
  document, and the total number of words.
  There are many more sophisticated algorithms but given the kind of
  data we have this seems to work well enough, and it was the simplest
  solution I could think of."
  [freqs words]
  (let [all-docs (set (flatten (map keys (vals freqs))))]
    (->> (for [d all-docs
           :let [found (m/filter-vals #(contains? (-> % keys set) d) freqs)]]
           [d (/ (count found) (count words))])
         (sort-by second)               ;
         reverse
         (take MAX_RECIPES))))

(defn search
  [index words]
  (let [freqs (m/filter-keys #(contains? (set words) %) index)]
    (rank freqs words)))

(defn format-results
  [results]
  (for [[doc-id rank] results]
    (format "%s:%s" doc-id (float rank))))

(defn ingest-directory
  [dir]
  (reset-index!)
  (doseq [f (file-seq (io/file dir))
          :when (.isFile f)]
    (ingest (slurp f) (str f))))

(defn -main [& args]
  (let [{:keys [arguments]} (parse-opts args [])
        [dir & words] arguments]
    (ingest-directory dir)
    (->> words
         (search @index)
         format-results
         (string/join "\n")
         printf)))

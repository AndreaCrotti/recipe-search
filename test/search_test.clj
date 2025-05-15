(ns search-test
  (:require [clojure.test :refer [deftest testing is]]
            [search]))

(def pork-ribs "Pork ribs are cut from the neck fillet and are always juicy and tasty however you cook them. Serve this for dinner with summer greens and brown rice noodles, or with chips or new potatoes, perhaps with a bit of chilli sauce on the side. They’re easiest to eat if you abandon your cutlery and use your hands.")

(def pork-larb "Also called larb or laap, this pork dish has a beautiful balance of freshness, crunch and spiciness. There are quite a few ingredients, but preparing and assembling them is quick and simple. You could also add a little cooked diced potato or butternut squash. Little Gem is particularly good for scooping up the meat, but any other lettuce you'd like will also be fine. This is often served with a mango salad.")

(def d1 "doc1.txt")
(def d2 "doc2.txt")

(defmacro with-data
  [docs & body]
  `(do
    (search/reset-index!)
    (doseq [[text# doc-id#] ~docs]
      (search/ingest text# doc-id#))
    ~@body))

(defn search [text]
  (search/search @search/index text))

(deftest search-test
  (testing "Word not found"
    (with-data [[pork-ribs d1]]
      (is (= [] (search "missing")))))

  (testing "Can find matches from one word"
    (with-data [[pork-ribs d1]]
      (is (= [[d1 1]]
             (search "pork")))))

  (testing "Can find matches from multiple words"
    (with-data [[pork-ribs d1]]
      (is (= [[d1 1]]
             (search "pork ribs")))
      (is (= [[d1 (/ 1 2)]]
             (search "pork missing") ))))

  (testing "Ingesting multiple documents"
    (with-data [[pork-ribs d1] [pork-larb d2]]
      (testing "Return sorted results"
        (is (= [[d2 1], [d1 1]] (search "pork")))))))

(deftest frequencies-test
  (testing "Can generate word frequencies"
    (is (= {"hello" {d1 1}, "world" {d1 1}}
           (search/get-words-frequencies "Hello world!" d1)))))

(deftest rank-test
  (testing "Can rank analysis results"
    (is (= {"hello" [[d1 (/ 1 2)] [d2 1]]}
           (search/rank {"hello" {d1 3, d2 1}
                         "world" {d2 2}}
                        ["hello" "world"])))))

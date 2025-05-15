(ns search-test
  (:require [clojure.test :refer [deftest testing is]]
            [search]))

(def pork-ribs "Pork ribs are cut from the neck fillet and are always juicy and tasty however you cook them. Serve this for dinner with summer greens and brown rice noodles, or with chips or new potatoes, perhaps with a bit of chilli sauce on the side. They’re easiest to eat if you abandon your cutlery and use your hands.")

(def pork-larb "Also called larb or laap, this pork dish has a beautiful balance of freshness, crunch and spiciness. There are quite a few ingredients, but preparing and assembling them is quick and simple. You could also add a little cooked diced potato or butternut squash. Little Gem is particularly good for scooping up the meat, but any other lettuce you'd like will also be fine. This is often served with a mango salad.")

(defmacro with-data
  [docs & body]
  `(do
    (search/reset-index!)
    (doseq [[text# doc-id#] ~docs]
      (search/ingest text# doc-id#))
    ~@body))

(deftest search-test
  (testing "Word not found"
    (with-data [[pork-ribs "doc1.txt"]]
      (is (= [] (search/search "missing")))))

  (testing "Can find matches from one word"
    (with-data [[pork-ribs "doc1.txt"]]
      (is (= [["doc1.txt" 1]]
             (search/search "pork")))))

  (testing "Can find matches from multiple words"
    (with-data [[pork-ribs "doc1.txt"]]
      (is (= [["doc1.txt" 1]]
             (search/search "pork ribs")))
      (is (= [["doc1.txt" (/ 1 2)]]
             (search/search "pork missing") ))))

  (testing "Ingesting multiple documents"
    (with-data [[pork-ribs "doc1.txt"] [pork-larb "doc2.txt"]]
      (testing "Return sorted results"
        (is (= [["doc1.txt" 1], ["doc2.txt" 1]] (search/search "pork")))))))

(deftest frequencies-test
  (testing "Can generate word frequencies"
    (is (= {"hello" {"doc1.txt" 1}, "world" {"doc1.txt" 1}}
           (search/get-words-frequencies "Hello world!" "doc1.txt")))))

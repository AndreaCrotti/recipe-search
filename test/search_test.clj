(ns search-test
  (:require [clojure.test :refer [deftest testing is]]
            [search]))

(def sample-text "Pork ribs are cut from the neck fillet and are always juicy and tasty however you cook them. Serve this for dinner with summer greens and brown rice noodles, or with chips or new potatoes, perhaps with a bit of chilli sauce on the side. They’re easiest to eat if you abandon your cutlery and use your hands.")


(deftest search-test
  (testing "Can find matches"
    (search/ingest sample-text "doc1.txt")
    (is (= (search/search "pork") {"doc1.txt" 1}))))

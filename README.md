# Solution

## Rationale

In a real product for search we would use something like Lucene/Postgres full text search support or similar technologies, which would allow to easily scale to any number of documents.
For this particular exercise we are just ingesting all the documents into a reverse index that's implemented as an atom.

The index atom would look like this:

```clojure
{"word" {"document1.txt" 2, "document2.txt" 10}
 "hello" {"document1.txt" 4}}
```

A map keyed by the words with how many times each word was found in each document.
The rank algorithm does a simple ratio of words that appear in each document, and the total number of words.

For example, given the index:

```clojure
{"word" {"document1.txt" 2, "document2.txt" 10}
 "hello" {"document1.txt" 4}}
```

If I look for the words `["hello" "world"]`, the ranking will return an ordered list of vectors like

```clojure
[["document1.txt" 1]
 ["document2.txt" 0.5]]
```

Because `document1` contained both of the search terms but `document2` contained only one of them.
There are many more sophisticated algorithms to use, but I found that this was working well enough.
Given that we are looking in a recipes database, we would most likely look for ingredients, so the fact that one word appears once or many times in a document does not really make a difference in practice.

## How to run

Run the program with `./bin/run "your search terms"`, this will ingest all the recipes in `./data/recipes` and return a ranked list of results.

## Testing

Unit tests can be run with `./bin/unit_test`.
There is also an end_to_end test, that checks the actual output of the script, making sure that if we look for "broccoli soup stilton", the first result that comes back is the `data/recipes/broccoli-soup-with-stilton.txt` file.

## Performances

The most expensive operation is the ingestion of the data, but in an API that would need to be done once.

```clojure
search> (time (ingest-directory "data/recipes"))
"Elapsed time: 600.253927 msecs"
```

Once the index has been populated searching for a word is rather fast:

```clojure
search> (time (search/search @index ["broccoli"]))
"Elapsed time: 5.3324 msecs"
(["data/recipes/roasted-veg-toad-in-the-hole-with-onion.txt" 1]
 ["data/recipes/broccoli-tomato-wild-garlic-wheatberries.txt" 1]
 ["data/recipes/broccoli-shiitake-hoisin-stirfry.txt" 1]
 ["data/recipes/roasted-broccoli-tahina-dressing.txt" 1]
```

One potential issue is that the time complexity is linear with the number of words, but I didn't have time to look into further optimizations.


# Instructions

## Recipe search

We have to give users the ability to search for recipes. We have some text files containing recipe descriptions written in English. We would like to be able to search over the set of these text files to find recipes given at a minimum a single word e.g. `tomato`.

Example text files: [recipes.zip](https://media.riverford.co.uk/downloads/hiring/sse/recipes.zip)

We would like a program that can provide a search function over these files, each search returning a small number (e.g around 1-10) relevant recipes if possible.

The text files are of differing sizes, and are encoded as utf-8. New text files are coming in all the time, so we should not assume a static set of recipes.

The name of each file is considered the id of the recipe.

Our requirements have been listed by a key business stakeholder:

## Essential requirements:

-	Search results should be relevant, e.g. a search for broccoli stilton soup should return at least broccoli stilton soup.
-	Ideally the results will be sorted so that the most relevant result is first in the result list.
-	Searches should complete quickly so users are not kept waiting – this tool needs to serve many users so lower latency will mean we can serve more concurrent searches - ideally searches will take < 10ms.
-	Documentation that describes how to set up and run your solution. The easier it is to run your solution (e.g. without needing to install bulky IDEs to build it), the better.


# Solution

## Rationale

## How to run

Run tests with `./bin/kaocha`, run the program with `./bin/run "your search terms"`.

## Performances


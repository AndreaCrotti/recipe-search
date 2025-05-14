#!/usr/bin/env bash

RECIPE_DIRECORY=$1
TO_MATCH=$2

rg $TO_MATCH $RECIPE_DIRECORY -l -i --count-matches | sort -t: -k2 -nr

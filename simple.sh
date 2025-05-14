#!/usr/bin/env bash

TO_MATCH=$1

rg $TO_MATCH recipes/ -l -i --count-matches | sort -t: -k2 -nr

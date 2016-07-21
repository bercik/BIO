#!/bin/bash

for f in *.bd; do
   ./biodoc.py $f
done

./html/mergedoc.py

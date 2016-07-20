#!/bin/bash

for f in *.bd; do
   ./biodoc.py $f
done

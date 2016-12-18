#!/bin/bash

if [ "$#" -ne 1 ]; then
   echo "Illegal number of parameters"
   exit -1
fi
num=$1

for bio_file in *.bio; do
   bioc_file=$bio_file"c"
   bioc $bio_file -o $bioc_file
   echo $bio_file
   echo "time:"
   echo `time bio $bioc_file $num`
   echo "============="
done

for c_file in *.c; do
   cout_file=$c_file"out"
   gcc -std=c99 -O3 $c_file -o $cout_file -lm
   echo $c_file
   echo "time:"
   echo `time ./$cout_file $num`
   echo "============="
done

for py_file in *.py; do
   echo $py_file
   echo "time:"
   echo `time python3 $py_file $num`
   echo "============="
done


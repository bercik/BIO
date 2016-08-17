#!/bin/bash
grep -c '^!name\s\+' *.bd
total=`grep -c '^!name\s\+' *.bd | awk -F: '{sum+=$2} END{print sum}'`
echo "total: $total"

#!/bin/bash

for i in 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 
do
	for j in 5000 10000 20000 30000 40000
	do
		echo "Improved run $i for sample size $j"
		java Main -i $j ../input/ca-AstroPh.txt > ~/amherst/cosc254/cosc254-triangle-counting/output/output.${j}I.$i.txt &
	done
done

#!/bin/sh

iterations=$1
secondsInRun=$2
initialListSize=$3

mvn exec:java -Dexec.mainClass="edu.buffalo.cse.cse605.BenchmarkRunner" -Dexec.args="1 $1 $2 $3" > 1-thread.out &
mvn exec:java -Dexec.mainClass="edu.buffalo.cse.cse605.BenchmarkRunner" -Dexec.args="2 $1 $2 $3" > 2-thread.out &
mvn exec:java -Dexec.mainClass="edu.buffalo.cse.cse605.BenchmarkRunner" -Dexec.args="4 $1 $2 $3" > 4-thread.out &
mvn exec:java -Dexec.mainClass="edu.buffalo.cse.cse605.BenchmarkRunner" -Dexec.args="8 $1 $2 $3" > 8-thread.out &
mvn exec:java -Dexec.mainClass="edu.buffalo.cse.cse605.BenchmarkRunner" -Dexec.args="16 $1 $2 $3" > 16-thread.out
sleep 120
mvn exec:java -Dexec.mainClass="edu.buffalo.cse.cse605.BenchmarkRunner" -Dexec.args="32 $1 $2 $3" > 32-thread.out
sleep 120
mvn exec:java -Dexec.mainClass="edu.buffalo.cse.cse605.BenchmarkRunner" -Dexec.args="64 $1 $2 $3" > 64-thread.out


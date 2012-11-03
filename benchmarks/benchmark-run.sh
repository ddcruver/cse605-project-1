#!/bin/sh

mvn exec:java -Dexec.mainClass="edu.buffalo.cse.cse605.BenchmarkRunner" -Dexec.args="1 10 20 10000" > 1-thread.out &
mvn exec:java -Dexec.mainClass="edu.buffalo.cse.cse605.BenchmarkRunner" -Dexec.args="2 10 20 10000" > 2-thread.out &
mvn exec:java -Dexec.mainClass="edu.buffalo.cse.cse605.BenchmarkRunner" -Dexec.args="4 10 20 10000" > 4-thread.out &
mvn exec:java -Dexec.mainClass="edu.buffalo.cse.cse605.BenchmarkRunner" -Dexec.args="8 10 20 10000" > 8-thread.out &
mvn exec:java -Dexec.mainClass="edu.buffalo.cse.cse605.BenchmarkRunner" -Dexec.args="16 10 20 10000" > 16-thread.out
sleep 120
mvn exec:java -Dexec.mainClass="edu.buffalo.cse.cse605.BenchmarkRunner" -Dexec.args="32 10 20 10000" > 32-thread.out
sleep 120
mvn exec:java -Dexec.mainClass="edu.buffalo.cse.cse605.BenchmarkRunner" -Dexec.args="64 10 20 10000" > 64-thread.out


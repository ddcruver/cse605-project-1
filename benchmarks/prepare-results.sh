#!/bin/sh

cut -d, -f 1-2 benchmarks-1t*.out > 1.treads
cut -d, -f 2 benchmarks-2t*.out > 2.treads
cut -d, -f 2 benchmarks-4t*.out > 4.treads
cut -d, -f 2 benchmarks-8t*.out > 8.treads
cut -d, -f 2 benchmarks-16t*.out > 16.treads
cut -d, -f 2 benchmarks-32t*.out > 32.treads
cut -d, -f 2 benchmarks-64t*.out > 64.treads

paste -d, 1.threads 2.threads 4.threads 8.threads 16.threads 32.threads 64.threads
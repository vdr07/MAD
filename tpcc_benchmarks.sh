#!/bin/bash

JSON_PATH="results/json"
EXECUTION_TYPE="no_dc"
RESULTS_PATH="$JSON_PATH/$EXECUTION_TYPE"

echo "Json Path: $JSON_PATH"
echo "Execution Type: $EXECUTION_TYPE"
echo "Results Path: $RESULTS_PATH"

mkdir -p "$JSON_PATH"
mkdir -p "$RESULTS_PATH"

# TPC-C
echo "TPC-C"
make benchmark=tpcc

mkdir -p "$RESULTS_PATH/tpcc"

echo "TPC-C Mono"

if [ -e "$RESULTS_PATH/tpcc/tpcc_mono.json" ]; then
    echo "TPC-C Mono was already executed, skipping"
else
    rm -f analyzer/src/benchmarks/tpcc/decomposition.json
    cp analyzer/src/benchmarks/tpcc/mono_decomposition.json analyzer/src/benchmarks/tpcc/decomposition.json
    ./mad.sh --analyze tpcc | tee results/tpcc_mono
    cat results/tpcc_mono | grep -v -e "INFO" -e "WARN" -e "Soot" -e "Transformer" -e "Buildfile" -e "analyzing" -e "structure1" -e "structure3" -e "java].$" > results/tpcc_mono_short
    rm results/tpcc_mono
    mv analyzer/results.json "$RESULTS_PATH/tpcc/tpcc_mono.json"
fi


echo "TPC-C Best"
if [ -e "$RESULTS_PATH/tpcc/tpcc_best.json" ]; then
    echo "TPC-C Best was already executed, skipping"
else
  rm -f analyzer/src/benchmarks/tpcc/decomposition.json
  cp analyzer/src/benchmarks/tpcc/best_decomposition.json analyzer/src/benchmarks/tpcc/decomposition.json
  ./mad.sh --analyze tpcc | tee results/tpcc_best
  cat results/tpcc_best | grep -v -e "INFO" -e "WARN" -e "Soot" -e "Transformer" -e "Buildfile" -e "analyzing" -e "structure1" -e "structure3" -e "java].$" > results/tpcc_best_short
  rm results/tpcc_best
  mv analyzer/results.json "$RESULTS_PATH/tpcc/tpcc_best.json"
fi

echo "TPC-C Full"
if [ -e "$RESULTS_PATH/tpcc/tpcc_full.json" ]; then
    echo "TPC-C Full was already executed, skipping"
else
  rm -f analyzer/src/benchmarks/tpcc/decomposition.json
  cp analyzer/src/benchmarks/tpcc/full_decomposition.json analyzer/src/benchmarks/tpcc/decomposition.json
  ./mad.sh --analyze tpcc | tee results/tpcc_full
  cat results/tpcc_full | grep -v -e "INFO" -e "WARN" -e "Soot" -e "Transformer" -e "Buildfile" -e "analyzing" -e "structure1" -e "structure3" -e "java].$" > results/tpcc_full_short
  rm results/tpcc_full
  mv analyzer/results.json "$RESULTS_PATH/tpcc/tpcc_full.json"
fi
#!/bin/bash

JSON_PATH="results/json"
EXECUTION_TYPE="no_dc"
RESULTS_PATH="$JSON_PATH/$EXECUTION_TYPE"

echo "Json Path: $JSON_PATH"
echo "Execution Type: $EXECUTION_TYPE"
echo "Results Path: $RESULTS_PATH"

mkdir -p "$JSON_PATH"
mkdir -p "$RESULTS_PATH"

echo "jpetstore"
make benchmark=jpetstore
mkdir -p "$RESULTS_PATH/jpetstore"

echo "jpetstore Mono"
if [ -e "$RESULTS_PATH/jpetstore/jpetstore_mono.json" ]; then
    echo "jpetstore Mono was already executed, skipping"
else
  rm -f analyzer/src/benchmarks/jpetstore/decomposition.json
  cp analyzer/src/benchmarks/jpetstore/mono_decomposition.json analyzer/src/benchmarks/jpetstore/decomposition.json
  ./mad.sh --analyze jpetstore | tee results/jpetstore_mono
  cat results/jpetstore_mono | grep -v -e "INFO" -e "WARN" -e "Soot" -e "Transformer" -e "Buildfile" -e "analyzing" -e "structure1" -e "structure3" -e "java].$" > results/jpetstore_mono_short
  rm results/jpetstore_mono
  mv analyzer/results.json "$RESULTS_PATH/jpetstore/jpetstore_mono.json"
fi

echo "jpetstore Best"
if [ -e "$RESULTS_PATH/jpetstore/jpetstore_best.json" ]; then
    echo "jpetstore Best was already executed, skipping"
else
  rm -f analyzer/src/benchmarks/jpetstore/decomposition.json
  cp analyzer/src/benchmarks/jpetstore/best_decomposition.json analyzer/src/benchmarks/jpetstore/decomposition.json
  ./mad.sh --analyze jpetstore | tee results/jpetstore_best
  cat results/jpetstore_best | grep -v -e "INFO" -e "WARN" -e "Soot" -e "Transformer" -e "Buildfile" -e "analyzing" -e "structure1" -e "structure3" -e "java].$" > results/jpetstore_best_short
  rm results/jpetstore_best
  mv analyzer/results.json "$RESULTS_PATH/jpetstore/jpetstore_best.json"
fi

echo "jpetstore Full"
if [ -e "$RESULTS_PATH/jpetstore/jpetstore_full.json" ]; then
    echo "jpetstore Full was already executed, skipping"
else
  rm -f analyzer/src/benchmarks/jpetstore/decomposition.json
  cp analyzer/src/benchmarks/jpetstore/full_decomposition.json analyzer/src/benchmarks/jpetstore/decomposition.json
  ./mad.sh --analyze jpetstore | tee results/jpetstore_full
  cat results/jpetstore_full | grep -v -e "INFO" -e "WARN" -e "Soot" -e "Transformer" -e "Buildfile" -e "analyzing" -e "structure1" -e "structure3" -e "java].$" > results/jpetstore_full_short
  rm results/jpetstore_full
  mv analyzer/results.json "$RESULTS_PATH/jpetstore/jpetstore_full.json"
fi

echo "react"
make benchmark=spring_mvc_react
mkdir -p "$RESULTS_PATH/react"

echo "react Mono"
if [ -e "$RESULTS_PATH/react/react_mono.json" ]; then
    echo "react Mono was already executed, skipping"
else
  rm -f analyzer/src/benchmarks/spring_mvc_react/decomposition.json
  cp analyzer/src/benchmarks/spring_mvc_react/mono_decomposition.json analyzer/src/benchmarks/spring_mvc_react/decomposition.json
  ./mad.sh --analyze spring_mvc_react | tee results/spring_mvc_react_mono
  cat results/spring_mvc_react_mono | grep -v -e "INFO" -e "WARN" -e "Soot" -e "Transformer" -e "Buildfile" -e "analyzing" -e "structure1" -e "structure3" -e "java].$" > results/spring_mvc_react_mono_short
  rm results/spring_mvc_react_mono
  mv analyzer/results.json "$RESULTS_PATH/react/react_mono.json"
fi

echo "react Best"
if [ -e "$RESULTS_PATH/react/react_best.json" ]; then
    echo "react Best was already executed, skipping"
else
  rm -f analyzer/src/benchmarks/spring_mvc_react/decomposition.json
  cp analyzer/src/benchmarks/spring_mvc_react/best_decomposition.json analyzer/src/benchmarks/spring_mvc_react/decomposition.json
  ./mad.sh --analyze spring_mvc_react | tee results/spring_mvc_react_best
  cat results/spring_mvc_react_best | grep -v -e "INFO" -e "WARN" -e "Soot" -e "Transformer" -e "Buildfile" -e "analyzing" -e "structure1" -e "structure3" -e "java].$" > results/spring_mvc_react_best_short
  rm results/spring_mvc_react_best
  mv analyzer/results.json "$RESULTS_PATH/react/react_best.json"
fi

echo "react Full"
if [ -e "$RESULTS_PATH/react/react_full.json" ]; then
    echo "react Full was already executed, skipping"
else
  rm -f analyzer/src/benchmarks/spring_mvc_react/decomposition.json
  cp analyzer/src/benchmarks/spring_mvc_react/full_decomposition.json analyzer/src/benchmarks/spring_mvc_react/decomposition.json
  ./mad.sh --analyze spring_mvc_react | tee results/spring_mvc_react_full
  cat results/spring_mvc_react_full | grep -v -e "INFO" -e "WARN" -e "Soot" -e "Transformer" -e "Buildfile" -e "analyzing" -e "structure1" -e "structure3" -e "java].$" > results/spring_mvc_react_full_short
  rm results/spring_mvc_react_full
  mv analyzer/results.json "$RESULTS_PATH/react/react_full.json"
fi
#!/bin/bash

JSON_PATH="results/json"
EXECUTION_TYPE="dc"
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

echo "find_sport_mates"
make benchmark=find_sport_mates
mkdir -p "$RESULTS_PATH/find_sports"

echo "find_sport_mates Mono"
if [ -e "$RESULTS_PATH/find_sports/find_sport_mates_mono.json" ]; then
    echo "find_sport_mates Mono was already executed, skipping"
else
  rm analyzer/src/benchmarks/find_sport_mates/decomposition.json
  cp analyzer/src/benchmarks/find_sport_mates/mono_decomposition.json analyzer/src/benchmarks/find_sport_mates/decomposition.json
  ./mad.sh --analyze find_sport_mates | tee results/find_sport_mates_mono
  cat results/find_sport_mates_mono | grep -v -e "INFO" -e "WARN" -e "Soot" -e "Transformer" -e "Buildfile" -e "analyzing" -e "structure1" -e "structure3" -e "java].$" > results/find_sport_mates_mono_short
  rm results/find_sport_mates_mono
  mv analyzer/results.json "$RESULTS_PATH/find_sports/find_sport_mates_mono.json"
fi

echo "find_sport_mates Best/Full"
if [ -e "$RESULTS_PATH/find_sports/find_sport_mates_best_full.json" ]; then
    echo "find_sport_mates Best/Full was already executed, skipping"
else
  rm analyzer/src/benchmarks/find_sport_mates/decomposition.json
  cp analyzer/src/benchmarks/find_sport_mates/best_decomposition.json analyzer/src/benchmarks/find_sport_mates/decomposition.json
  ./mad.sh --analyze find_sport_mates  | tee results/find_sport_mates_best
  cat results/find_sport_mates_best | grep -v -e "INFO" -e "WARN" -e "Soot" -e "Transformer" -e "Buildfile" -e "analyzing" -e "structure1" -e "structure3" -e "java].$" > results/find_sport_mates_best_short
  rm results/find_sport_mates_best
  mv analyzer/results.json "$RESULTS_PATH/find_sports/find_sport_mates_best_full.json"
fi

echo "jpabook"
make benchmark=jpabook
mkdir -p "$RESULTS_PATH/jpabook"

echo "jpabook Mono"

if [ -e "$RESULTS_PATH/jpabook/jpabook_mono.json" ]; then
    echo "jpabook Mono was already executed, skipping"
else
  rm analyzer/src/benchmarks/jpabook/decomposition.json
  cp analyzer/src/benchmarks/jpabook/mono_decomposition.json analyzer/src/benchmarks/jpabook/decomposition.json
  ./mad.sh --analyze jpabook | tee results/jpabook_mono
  cat results/jpabook_mono | grep -v -e "INFO" -e "WARN" -e "Soot" -e "Transformer" -e "Buildfile" -e "analyzing" -e "structure1" -e "structure3" -e "java].$" > results/jpabook_mono_short
  rm results/jpabook_mono
  mv analyzer/results.json "$RESULTS_PATH/jpabook/jpabook_mono.json"

fi

echo "jpabook Best"
if [ -e "$RESULTS_PATH/jpabook/jpabook_best.json" ]; then
    echo "jpabook Best was already executed, skipping"
else
  rm -f analyzer/src/benchmarks/jpabook/decomposition.json
  cp analyzer/src/benchmarks/jpabook/best_decomposition.json analyzer/src/benchmarks/jpabook/decomposition.json
  ./mad.sh --analyze jpabook | tee results/jpabook_best
  cat results/jpabook_best | grep -v -e "INFO" -e "WARN" -e "Soot" -e "Transformer" -e "Buildfile" -e "analyzing" -e "structure1" -e "structure3" -e "java].$" > results/jpabook_best_short
  rm results/jpabook_best
  mv analyzer/results.json "$RESULTS_PATH/jpabook/jpabook_best.json"
fi

echo "jpabook Full"
if [ -e "$RESULTS_PATH/jpabook/jpabook_full.json" ]; then
    echo "jpabook Full was already executed, skipping"
else
  rm -f analyzer/src/benchmarks/jpabook/decomposition.json
  cp analyzer/src/benchmarks/jpabook/full_decomposition.json analyzer/src/benchmarks/jpabook/decomposition.json
  ./mad.sh --analyze jpabook | tee results/jpabook_full
  cat results/jpabook_full | grep -v -e "INFO" -e "WARN" -e "Soot" -e "Transformer" -e "Buildfile" -e "analyzing" -e "structure1" -e "structure3" -e "java].$" > results/jpabook_full_short
  rm results/jpabook_full
  mv analyzer/results.json "$RESULTS_PATH/jpabook/jpabook_full.json"
fi


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

echo "petclinic"
make benchmark=petclinic
mkdir -p "$RESULTS_PATH/petclinic"

echo "petclinic Mono"
if [ -e "$RESULTS_PATH/petclinic/petclinic_mono.json" ]; then
    echo "petclinic Mono was already executed, skipping"
else
  rm -f analyzer/src/benchmarks/petclinic/decomposition.json
  cp analyzer/src/benchmarks/petclinic/mono_decomposition.json analyzer/src/benchmarks/petclinic/decomposition.json
  ./mad.sh --analyze petclinic | tee results/petclinic_mono
  cat results/petclinic_mono | grep -v -e "INFO" -e "WARN" -e "Soot" -e "Transformer" -e "Buildfile" -e "analyzing" -e "structure1" -e "structure3" -e "java].$" > results/petclinic_mono_short
  rm results/petclinic_mono
  mv analyzer/results.json "$RESULTS_PATH/petclinic/petclinic_mono.json"
fi

echo "petclinic Best"
if [ -e "$RESULTS_PATH/petclinic/petclinic_best.json" ]; then
    echo "petclinic Best was already executed, skipping"
else
  rm -f analyzer/src/benchmarks/petclinic/decomposition.json
  cp analyzer/src/benchmarks/petclinic/best_decomposition.json analyzer/src/benchmarks/petclinic/decomposition.json
  ./mad.sh --analyze petclinic | tee results/petclinic_best
  cat results/petclinic_best | grep -v -e "INFO" -e "WARN" -e "Soot" -e "Transformer" -e "Buildfile" -e "analyzing" -e "structure1" -e "structure3" -e "java].$" > results/petclinic_best_short
  rm results/petclinic_best
  mv analyzer/results.json "$RESULTS_PATH/petclinic/petclinic_best.json"
fi

echo "petclinic Full"
if [ -e "$RESULTS_PATH/petclinic/petclinic_full.json" ]; then
    echo "petclinic Full was already executed, skipping"
else
  rm -f analyzer/src/benchmarks/petclinic/decomposition.json
  cp analyzer/src/benchmarks/petclinic/full_decomposition.json analyzer/src/benchmarks/petclinic/decomposition.json
  ./mad.sh --analyze petclinic | tee results/petclinic_full
  cat results/petclinic_full | grep -v -e "INFO" -e "WARN" -e "Soot" -e "Transformer" -e "Buildfile" -e "analyzing" -e "structure1" -e "structure3" -e "java].$" > results/petclinic_full_short
  rm results/petclinic_full
  mv analyzer/results.json "$RESULTS_PATH/petclinic/petclinic_full.json"
fi

echo "myweb"
make benchmark=myweb
mkdir -p "$RESULTS_PATH/myweb"

echo "myweb Mono"
if [ -e "$RESULTS_PATH/myweb/myweb_mono.json" ]; then
    echo "myweb Mono was already executed, skipping"
else
  rm -f analyzer/src/benchmarks/myweb/decomposition.json
  cp analyzer/src/benchmarks/myweb/mono_decomposition.json analyzer/src/benchmarks/myweb/decomposition.json
  ./mad.sh --analyze myweb | tee results/myweb_mono
  cat results/myweb_mono | grep -v -e "INFO" -e "WARN" -e "Soot" -e "Transformer" -e "Buildfile" -e "analyzing" -e "structure1" -e "structure3" -e "java].$" > results/myweb_mono_short
  rm results/myweb_mono
  mv analyzer/results.json "$RESULTS_PATH/myweb/myweb_mono.json"
fi

echo "myweb Best"
if [ -e "$RESULTS_PATH/myweb/myweb_best.json" ]; then
    echo "myweb Best was already executed, skipping"
else
  rm -f analyzer/src/benchmarks/myweb/decomposition.json
  cp analyzer/src/benchmarks/myweb/best_decomposition.json analyzer/src/benchmarks/myweb/decomposition.json
  ./mad.sh --analyze myweb | results/myweb_best
  cat results/myweb_best | grep -v -e "INFO" -e "WARN" -e "Soot" -e "Transformer" -e "Buildfile" -e "analyzing" -e "structure1" -e "structure3" -e "java].$" > results/myweb_best_short
  rm results/myweb_best
  mv analyzer/results.json "$RESULTS_PATH/myweb/myweb_best.json"
fi

echo "myweb Full"
if [ -e "$RESULTS_PATH/myweb/myweb_full.json" ]; then
    echo "myweb Full was already executed, skipping"
else
  rm -f analyzer/src/benchmarks/myweb/decomposition.json
  cp analyzer/src/benchmarks/myweb/full_decomposition.json analyzer/src/benchmarks/myweb/decomposition.json
  ./mad.sh --analyze myweb | tee results/myweb_full
  cat results/myweb_full | grep -v -e "INFO" -e "WARN" -e "Soot" -e "Transformer" -e "Buildfile" -e "analyzing" -e "structure1" -e "structure3" -e "java].$" > results/myweb_full_short
  rm results/myweb_full
  mv analyzer/results.json "$RESULTS_PATH/myweb/myweb_full.json"
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
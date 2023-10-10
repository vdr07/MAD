#!/bin/bash

echo "TPC-C"
make benchmark=tpcc | grep "ç"
echo "TPC-C Mono"
mv analyzer/src/benchmarks/tpcc/mono_decomposition.json analyzer/src/benchmarks/tpcc/decomposition.json
./clotho.sh --analyze tpcc > results/tpcc_mono
mv analyzer/src/benchmarks/tpcc/decomposition.json analyzer/src/benchmarks/tpcc/mono_decomposition.json
echo "TPC-C Best"
mv analyzer/src/benchmarks/tpcc/best_decomposition.json analyzer/src/benchmarks/tpcc/decomposition.json
./clotho.sh --analyze tpcc > results/tpcc_best
mv analyzer/src/benchmarks/tpcc/decomposition.json analyzer/src/benchmarks/tpcc/best_decomposition.json
echo "TPC-C Full"
mv analyzer/src/benchmarks/tpcc/full_decomposition.json analyzer/src/benchmarks/tpcc/decomposition.json
./clotho.sh --analyze tpcc > results/tpcc_full
mv analyzer/src/benchmarks/tpcc/decomposition.json analyzer/src/benchmarks/tpcc/full_decomposition.json

echo "jpabook"
make benchmark=jpabook | grep "ç"
echo "jpabook Mono"
mv analyzer/src/benchmarks/jpabook/mono_decomposition.json analyzer/src/benchmarks/jpabook/decomposition.json
./clotho.sh --analyze jpabook > results/jpabook_mono
mv analyzer/src/benchmarks/jpabook/decomposition.json analyzer/src/benchmarks/jpabook/mono_decomposition.json
echo "jpabook Best"
mv analyzer/src/benchmarks/jpabook/best_decomposition.json analyzer/src/benchmarks/jpabook/decomposition.json
./clotho.sh --analyze jpabook > results/jpabook_best
mv analyzer/src/benchmarks/jpabook/decomposition.json analyzer/src/benchmarks/jpabook/best_decomposition.json
echo "jpabook Full"
mv analyzer/src/benchmarks/jpabook/full_decomposition.json analyzer/src/benchmarks/jpabook/decomposition.json
./clotho.sh --analyze jpabook > results/jpabook_full
mv analyzer/src/benchmarks/jpabook/decomposition.json analyzer/src/benchmarks/jpabook/full_decomposition.json

echo "petclinic"
make benchmark=petclinic | grep "ç"
echo "petclinic Mono"
mv analyzer/src/benchmarks/petclinic/mono_decomposition.json analyzer/src/benchmarks/petclinic/decomposition.json
./clotho.sh --analyze petclinic > results/petclinic_mono
mv analyzer/src/benchmarks/petclinic/decomposition.json analyzer/src/benchmarks/petclinic/mono_decomposition.json
echo "petclinic Best"
mv analyzer/src/benchmarks/petclinic/best_decomposition.json analyzer/src/benchmarks/petclinic/decomposition.json
./clotho.sh --analyze petclinic > results/petclinic_best
mv analyzer/src/benchmarks/petclinic/decomposition.json analyzer/src/benchmarks/petclinic/best_decomposition.json
echo "petclinic Full"
mv analyzer/src/benchmarks/petclinic/full_decomposition.json analyzer/src/benchmarks/petclinic/decomposition.json
./clotho.sh --analyze petclinic > results/petclinic_full
mv analyzer/src/benchmarks/petclinic/decomposition.json analyzer/src/benchmarks/petclinic/full_decomposition.json

echo "myweb"
make benchmark=myweb | grep "ç"
echo "myweb Mono"
mv analyzer/src/benchmarks/myweb/mono_decomposition.json analyzer/src/benchmarks/myweb/decomposition.json
./clotho.sh --analyze myweb > results/myweb_mono
mv analyzer/src/benchmarks/myweb/decomposition.json analyzer/src/benchmarks/myweb/mono_decomposition.json
echo "myweb Best"
mv analyzer/src/benchmarks/myweb/best_decomposition.json analyzer/src/benchmarks/myweb/decomposition.json
./clotho.sh --analyze myweb > results/myweb_best
mv analyzer/src/benchmarks/myweb/decomposition.json analyzer/src/benchmarks/myweb/best_decomposition.json
echo "myweb Full"
mv analyzer/src/benchmarks/myweb/full_decomposition.json analyzer/src/benchmarks/myweb/decomposition.json
./clotho.sh --analyze myweb > results/myweb_full
mv analyzer/src/benchmarks/myweb/decomposition.json analyzer/src/benchmarks/myweb/full_decomposition.json

echo "react"
make benchmark=spring_mvc_react | grep "ç"
echo "react Mono"
mv analyzer/src/benchmarks/spring_mvc_react/mono_decomposition.json analyzer/src/benchmarks/spring_mvc_react/decomposition.json
./clotho.sh --analyze spring_mvc_react > results/spring_mvc_react_mono
mv analyzer/src/benchmarks/spring_mvc_react/decomposition.json analyzer/src/benchmarks/spring_mvc_react/mono_decomposition.json
echo "react Best"
mv analyzer/src/benchmarks/spring_mvc_react/best_decomposition.json analyzer/src/benchmarks/spring_mvc_react/decomposition.json
./clotho.sh --analyze spring_mvc_react > results/spring_mvc_react_best
mv analyzer/src/benchmarks/spring_mvc_react/decomposition.json analyzer/src/benchmarks/spring_mvc_react/best_decomposition.json
echo "react Full"
mv analyzer/src/benchmarks/spring_mvc_react/full_decomposition.json analyzer/src/benchmarks/spring_mvc_react/decomposition.json
./clotho.sh --analyze spring_mvc_react > results/spring_mvc_react_full
mv analyzer/src/benchmarks/spring_mvc_react/decomposition.json analyzer/src/benchmarks/spring_mvc_react/full_decomposition.json


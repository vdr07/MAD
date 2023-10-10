#!/bin/bash

echo "TPC-C"
make benchmark=tpcc | grep "ç"
echo "TPC-C Mono"
mv analyzer/src/benchmarks/tpcc/mono_decomposition.json analyzer/src/benchmarks/tpcc/decomposition.json
./clotho.sh --analyze tpcc > results/tpcc_mono
cat results/tpcc_mono | grep -v -e "INFO" -e "WARN" -e "Soot" -e "Transformer" -e "Buildfile" -e "analyzing" -e "structure1" -e "structure3" > results/tpcc_mono_short
rm results/tpcc_mono
mv analyzer/src/benchmarks/tpcc/decomposition.json analyzer/src/benchmarks/tpcc/mono_decomposition.json
echo "TPC-C Best"
mv analyzer/src/benchmarks/tpcc/best_decomposition.json analyzer/src/benchmarks/tpcc/decomposition.json
./clotho.sh --analyze tpcc > results/tpcc_best
cat results/tpcc_best | grep -v -e "INFO" -e "WARN" -e "Soot" -e "Transformer" -e "Buildfile" -e "analyzing" -e "structure1" -e "structure3" > results/tpcc_best_short
rm results/tpcc_best
mv analyzer/src/benchmarks/tpcc/decomposition.json analyzer/src/benchmarks/tpcc/best_decomposition.json
echo "TPC-C Full"
mv analyzer/src/benchmarks/tpcc/full_decomposition.json analyzer/src/benchmarks/tpcc/decomposition.json
./clotho.sh --analyze tpcc > results/tpcc_full
cat results/tpcc_full | grep -v -e "INFO" -e "WARN" -e "Soot" -e "Transformer" -e "Buildfile" -e "analyzing" -e "structure1" -e "structure3" > results/tpcc_full_short
rm results/tpcc_full
mv analyzer/src/benchmarks/tpcc/decomposition.json analyzer/src/benchmarks/tpcc/full_decomposition.json

echo "jpabook"
make benchmark=jpabook | grep "ç"
echo "jpabook Mono"
mv analyzer/src/benchmarks/jpabook/mono_decomposition.json analyzer/src/benchmarks/jpabook/decomposition.json
./clotho.sh --analyze jpabook > results/jpabook_mono
cat results/jpabook_mono | grep -v -e "INFO" -e "WARN" -e "Soot" -e "Transformer" -e "Buildfile" -e "analyzing" -e "structure1" -e "structure3" > results/jpabook_mono_short
rm results/jpabook_mono
mv analyzer/src/benchmarks/jpabook/decomposition.json analyzer/src/benchmarks/jpabook/mono_decomposition.json
echo "jpabook Best"
mv analyzer/src/benchmarks/jpabook/best_decomposition.json analyzer/src/benchmarks/jpabook/decomposition.json
./clotho.sh --analyze jpabook > results/jpabook_best
cat results/jpabook_best | grep -v -e "INFO" -e "WARN" -e "Soot" -e "Transformer" -e "Buildfile" -e "analyzing" -e "structure1" -e "structure3" > results/jpabook_best_short
rm results/jpabook_best
mv analyzer/src/benchmarks/jpabook/decomposition.json analyzer/src/benchmarks/jpabook/best_decomposition.json
echo "jpabook Full"
mv analyzer/src/benchmarks/jpabook/full_decomposition.json analyzer/src/benchmarks/jpabook/decomposition.json
./clotho.sh --analyze jpabook > results/jpabook_full
cat results/jpabook_full | grep -v -e "INFO" -e "WARN" -e "Soot" -e "Transformer" -e "Buildfile" -e "analyzing" -e "structure1" -e "structure3" > results/jpabook_full_short
rm results/jpabook_full
mv analyzer/src/benchmarks/jpabook/decomposition.json analyzer/src/benchmarks/jpabook/full_decomposition.json

echo "petclinic"
make benchmark=petclinic | grep "ç"
echo "petclinic Mono"
mv analyzer/src/benchmarks/petclinic/mono_decomposition.json analyzer/src/benchmarks/petclinic/decomposition.json
./clotho.sh --analyze petclinic > results/petclinic_mono
cat results/petclinic_mono | grep -v -e "INFO" -e "WARN" -e "Soot" -e "Transformer" -e "Buildfile" -e "analyzing" -e "structure1" -e "structure3" > results/petclinic_mono_short
rm results/petclinic_mono
mv analyzer/src/benchmarks/petclinic/decomposition.json analyzer/src/benchmarks/petclinic/mono_decomposition.json
echo "petclinic Best"
mv analyzer/src/benchmarks/petclinic/best_decomposition.json analyzer/src/benchmarks/petclinic/decomposition.json
./clotho.sh --analyze petclinic > results/petclinic_best
cat results/petclinic_best | grep -v -e "INFO" -e "WARN" -e "Soot" -e "Transformer" -e "Buildfile" -e "analyzing" -e "structure1" -e "structure3" > results/petclinic_best_short
rm results/petclinic_best
mv analyzer/src/benchmarks/petclinic/decomposition.json analyzer/src/benchmarks/petclinic/best_decomposition.json
echo "petclinic Full"
mv analyzer/src/benchmarks/petclinic/full_decomposition.json analyzer/src/benchmarks/petclinic/decomposition.json
./clotho.sh --analyze petclinic > results/petclinic_full
cat results/petclinic_full | grep -v -e "INFO" -e "WARN" -e "Soot" -e "Transformer" -e "Buildfile" -e "analyzing" -e "structure1" -e "structure3" > results/petclinic_full_short
rm results/petclinic_full
mv analyzer/src/benchmarks/petclinic/decomposition.json analyzer/src/benchmarks/petclinic/full_decomposition.json

echo "myweb"
make benchmark=myweb | grep "ç"
echo "myweb Mono"
mv analyzer/src/benchmarks/myweb/mono_decomposition.json analyzer/src/benchmarks/myweb/decomposition.json
./clotho.sh --analyze myweb > results/myweb_mono
cat results/myweb_mono | grep -v -e "INFO" -e "WARN" -e "Soot" -e "Transformer" -e "Buildfile" -e "analyzing" -e "structure1" -e "structure3" > results/myweb_mono_short
rm results/myweb_mono
mv analyzer/src/benchmarks/myweb/decomposition.json analyzer/src/benchmarks/myweb/mono_decomposition.json
echo "myweb Best"
mv analyzer/src/benchmarks/myweb/best_decomposition.json analyzer/src/benchmarks/myweb/decomposition.json
./clotho.sh --analyze myweb > results/myweb_best
cat results/myweb_best | grep -v -e "INFO" -e "WARN" -e "Soot" -e "Transformer" -e "Buildfile" -e "analyzing" -e "structure1" -e "structure3" > results/myweb_best_short
rm results/myweb_best
mv analyzer/src/benchmarks/myweb/decomposition.json analyzer/src/benchmarks/myweb/best_decomposition.json
echo "myweb Full"
mv analyzer/src/benchmarks/myweb/full_decomposition.json analyzer/src/benchmarks/myweb/decomposition.json
./clotho.sh --analyze myweb > results/myweb_full
cat results/myweb_full | grep -v -e "INFO" -e "WARN" -e "Soot" -e "Transformer" -e "Buildfile" -e "analyzing" -e "structure1" -e "structure3" > results/myweb_full_short
rm results/myweb_full
mv analyzer/src/benchmarks/myweb/decomposition.json analyzer/src/benchmarks/myweb/full_decomposition.json

echo "react"
make benchmark=spring_mvc_react | grep "ç"
echo "react Mono"
mv analyzer/src/benchmarks/spring_mvc_react/mono_decomposition.json analyzer/src/benchmarks/spring_mvc_react/decomposition.json
./clotho.sh --analyze spring_mvc_react > results/spring_mvc_react_mono
cat results/spring_mvc_react_mono | grep -v -e "INFO" -e "WARN" -e "Soot" -e "Transformer" -e "Buildfile" -e "analyzing" -e "structure1" -e "structure3" > results/spring_mvc_react_mono_short
rm results/spring_mvc_react_mono
mv analyzer/src/benchmarks/spring_mvc_react/decomposition.json analyzer/src/benchmarks/spring_mvc_react/mono_decomposition.json
#echo "react Best"
#mv analyzer/src/benchmarks/spring_mvc_react/best_decomposition.json analyzer/src/benchmarks/spring_mvc_react/decomposition.json
#./clotho.sh --analyze spring_mvc_react > results/spring_mvc_react_best
#cat results/spring_mvc_react_best | grep -v -e "INFO" -e "WARN" -e "Soot" -e "Transformer" -e "Buildfile" -e "analyzing" -e "structure1" -e "structure3" > results/spring_mvc_react_best_short
#rm results/spring_mvc_react_best
#mv analyzer/src/benchmarks/spring_mvc_react/decomposition.json analyzer/src/benchmarks/spring_mvc_react/best_decomposition.json
#echo "react Full"
#mv analyzer/src/benchmarks/spring_mvc_react/full_decomposition.json analyzer/src/benchmarks/spring_mvc_react/decomposition.json
#./clotho.sh --analyze spring_mvc_react > results/spring_mvc_react_full
#cat results/spring_mvc_react_full | grep -v -e "INFO" -e "WARN" -e "Soot" -e "Transformer" -e "Buildfile" -e "analyzing" -e "structure1" -e "structure3" > results/spring_mvc_react_full_short
#rm results/spring_mvc_react_full
#mv analyzer/src/benchmarks/spring_mvc_react/decomposition.json analyzer/src/benchmarks/spring_mvc_react/full_decomposition.json




## Microservice Anomaly Detector: SMT-based Automatic Design-Time Detection of Anomalies in Migrations to Microservices
MAD (Microservice Anomaly Detector) is a testing framework for identifying anomalies that result from the decomposition of a monolith into microservices during design time.
Leveraging a static analyzer, it takes as input a (SQL) database-backed JAVA monolith application and a given decomposition and identifies possible data anomalies derived from the decomposition.
Furthermore, MAD classifies the anomalies according to definitions described in [Atya et al](https://ieeexplore.ieee.org/abstract/document/839388?casa_token=zuOCltn8xycAAAAA:vrespMjx6ygF-NPiUPWi2MyaOwlK_CUYzRWOnMXzDZvrb7XEUKdmhA8OG7lN-N1emW6_RaDD8Lk), providing developers a glimpse of the challenges that a given decomposition will entail. 

MAD's implementation is a fork of [CLOTHO](https://github.com/Kiarahmani/CLOTHO).
---

### Dependencies 
- [Java 1.8.0](https://java.com/en/download/help/index_installing.xml)
- [Z3 Theorem Prover](https://github.com/Z3Prover/z3)
- [Docker](https://www.docker.com/)


---

### Setup
Create a local repo of the project:
``` sh
git clone https://github.com/Kiarahmani/CLOTHO.git
```
Make sure docker daemon is running and current user is added to docker group and has privileges ([read more](https://docs.docker.com/install/linux/linux-postinstall/)). 

Move to the project directory and run the following command to create a cluster
of 2 Cassandra nodes each running in a docker container: 
``` sh
./clotho.sh --setup 2
```
You can verify that the cluster is correctly set up by running: 
```sh 
./clotho.sh --cluster
```

You should see something like: 
```
Datacenter: DC1
===============
Status=Up/Down
|/ State=Normal/Leaving/Joining/Moving
--  Address     Load       Tokens       Owns (effective)  Host ID                               Rack
UN  172.17.0.2  325.94 KiB  256          100.0%            2b1a9362-9071-4976-8434-315667548f3e  RAC1
Datacenter: DC2
===============
Status=Up/Down
|/ State=Normal/Leaving/Joining/Moving
--  Address     Load       Tokens       Owns (effective)  Host ID                               Rack
UN  172.17.0.3  309.56 KiB  256          100.0%            d3780a0e-7ebc-4d8a-a7e8-60ba9a74bdf9  RAC2
```


### Madalena's notes:

- Install an older version of Z3 - I have z3-4.8.10
- Change the paths on scripts/env.sh to point correctly to your rt.jar and jce.jar
- Change the path on analyzer/build.xml line 73 to the path of your z3 build
- If this doesn't work, try copying libz3.dylib into the analyzer dir
- After these steps, you should be able to do the static analysis


### Static Analysis 
Now that the cluster is up and running, you should choose a <benchmark_name> from pre-defined examples {dirty_read, dirty_write, long_fork, write_skew, lost_update} (or as we explain later, implement your own in analyzer/src/benchmarks/).

The following command will compile the source codes, including the chosen benchmark under test:
``` sh
make benchmark=<benchmark_name>
```
Now you can run CLOTHO's static analyzer by the following command (you can optionlly configure the analysis by editing `templates/config.properties`).
``` sh
./clotho.sh --analyze <benchmark_name>
```
Once the analysis finishes, CLOTHO will report the number of static anomalies found in <benchmark_name>, for example:
```
     [java] ================================
     [java] === AR compile time:  256ms
     [java] === Anomalies found:  1
     [java] === Analysis time:	  1753 ms
     [java] === Avg Ext. Time:	  1753 ms
```

Now you can view anomaly <anomaly_number> by running: 
```sh
./clotho.sh --show <benchmark_name> <anomaly_number>
```
For example:

![alt text](https://raw.githubusercontent.com/Kiarahmani/CLOTHO/master/readme/anomaly_1.dot.png "Dirty Read Anomaly")


### Madalena's notes

The previous analysis is made using no extra rules == Eventual Consistency

I added some other rules with the intent to discover anomalies while making use of other consistency models

The rules are written under analyzer/src/Z3/StaticAssertions.java

Line 109: mk_causal_vis

Line 119: mk_causal_cons_updates

Line 133: mk_causal_cons

Line 143: mk_read_comm

Line 153: mk_rep_read

Line 163: mk_linearizable

To make use of them, you just need to uncomment (or uncomment) the lines 199 - 203 in analyzer/src/Z3/Z3Driver.java

The combination of mk_read_comm + mk_rep_read + mk_linearizable makes serializability and seems to be working correctly.

---
Copyright (c) 2019 Kia Rahmani


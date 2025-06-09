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

### Result Reproducibility

MAD follows the same running instruction as of CLOTHO, which can be found in detail [here](https://github.com/Kiarahmani/CLOTHO/blob/master/README.md).
For reproducibility, we provide `run_benchmark.sh`, which analyses the seven benchmarks and respective decomponsotions evaluated in the paper.

---
Copyright (c) 2019 Kia Rahmani


FROM ubuntu:20.04 AS builder
LABEL authors="jrsoares"

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get install -y locales && \
    locale-gen en_US.UTF-8 && \
    update-locale LANG=en_US.UTF-8

ENV LANG=en_US.UTF-8
ENV LANGUAGE=en_US:en
ENV LC_ALL=en_US.UTF-8

# Base dependencies
RUN apt-get update && apt-get install -y \
    openjdk-8-jdk \
    git \
    python3 \
    ca-certificates \
    curl \
    gnupg \
    lsb-release \
    curl \
    docker.io \
    make \
    ant \
    wget \
    binutils \
    build-essential

# Download Z3 4.11.2 source
RUN wget https://github.com/Z3Prover/z3/archive/refs/tags/z3-4.11.2.tar.gz -O /tmp/z3-4.11.2.tar.gz && \
    tar -xzf /tmp/z3-4.11.2.tar.gz -C /opt && \
    rm /tmp/z3-4.11.2.tar.gz

# Build Z3 with Java bindings
RUN cd /opt/z3-z3-4.11.2 && \
    python3 scripts/mk_make.py --java && \
    cd build && \
    make -j$(nproc) && \
    make install

# Prevent daemon from trying to start
RUN systemctl disable docker || true

# Install Maven 3.6.3
ARG MAVEN_VERSION=3.6.3
RUN curl -fsSL https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
    | tar -xz -C /opt && \
    ln -s /opt/apache-maven-${MAVEN_VERSION}/bin/mvn /usr/bin/mvn

# Export environment variables required for MAD
ENV CLOTHO_RT_PATH=/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar \
    CLOTHO_JCE_PATH=/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/jce.jar \
    JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"

FROM builder AS runner
# Clone Mad repository
RUN git clone https://github.com/vdr07/MAD.git

WORKDIR "MAD"
RUN git pull
RUN chmod +x mad.sh
RUN make benchmark=tpcc

CMD ["sleep", "infinity"]
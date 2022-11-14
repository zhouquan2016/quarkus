FROM quay.io/quarkus/ubi-quarkus-graalvmce-builder-image:22.2-java17 AS build
USER quarkus
WORKDIR /code
COPY --chown=quarkus:quarkus . /code/
RUN mkdir -p ~/maven
RUN curl  https://dlcdn.apache.org/maven/maven-3/3.8.6/binaries/apache-maven-3.8.6-bin.tar.gz -L -o ~/maven/maven.tar.gz
RUN tar xvf ~/maven/*.gz --strip-component=1 -C ~/maven
RUN rm -f ~/maven/*.gz
RUN ~/maven/bin/mvn  package -Pall -Pnative  -Dmaven.test.skip=true
RUN rm -rf /code/*
ENV MAVAN_HOME ~/maven
ENV PATH $PATH:$MAVAN_HOME/bin
FROM quay.io/quarkus/ubi-quarkus-graalvmce-builder-image:22.2-java17 AS build
USER quarkus
WORKDIR /home/quarkus/code/

ARG MAVEN_HOME=/home/quarkus/maven
RUN echo $MAVEN_HOME
RUN mkdir -p $MAVEN_HOME
RUN curl  https://dlcdn.apache.org/maven/maven-3/3.8.6/binaries/apache-maven-3.8.6-bin.tar.gz -L -o $MAVEN_HOME/maven.tar.gz
RUN tar xvf $MAVEN_HOME/*.gz --strip-component=1 -C $MAVEN_HOME
RUN rm -f $MAVEN_HOME/*.gz
ENV MAVEN_HOME=$MAVEN_HOME
ENV PATH=$PATH:$MAVEN_HOME/bin

COPY --chown=quarkus:quarkus . /home/quarkus/code/
RUN mvn  package -Pall -Pnative  -Dmaven.test.skip=true
RUN rm -rf /home/quarkus/code/

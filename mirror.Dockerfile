FROM quay.io/quarkus/ubi-quarkus-native-image:22.2.0-java17
WORKDIR /work
RUN curl https://dlcdn.apache.org/maven/maven-3/3.8.6/binaries/apache-maven-3.8.6-bin.tar.gz -O
RUN mkdir maven && tar xvf apache-maven-3.8.6-bin.tar.gz -C maven --strip-component=1
RUN rm -f *.gz
RUN echo "export PATH=$PATH:/work/maven/bin" >> /etc/profile && source /etc/profile
RUN mvn -version
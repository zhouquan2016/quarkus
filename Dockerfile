## Stage 1 : build with maven builder image with native capabilities
FROM registry.cn-hangzhou.aliyuncs.com/zhqn/quarkus-maven-build:1.0.0 AS build
COPY --chown=quarkus:quarkus pom.xml /code/
USER quarkus
ARG MVN=${MAVAN_HOME}/bin/mvn
WORKDIR /code
RUN $MVN -B org.apache.maven.plugins:maven-dependency-plugin:3.1.2:go-offline
COPY src /code/src
RUN $MVN package -Pnative -Dmaven.test.skip=true
#
### Stage 2 : create the docker final image
FROM quay.io/quarkus/quarkus-micro-image:1.0
WORKDIR /work/
COPY --from=build /code/target/*-runner /work/application
#
## set up permissions for user `1001`
RUN chmod 775 /work /work/application \
  && chown -R 1001 /work \
  && chmod -R "g+rwX" /work \
  && chown -R 1001:root /work
#
EXPOSE 8080
USER 1001

CMD ["./application", "-Dquarkus.http.host=0.0.0.0"]
## Stage 1 : build with maven builder image with native capabilities
FROM registry.cn-hangzhou.aliyuncs.com/zhqn/quarkus-maven:java17 AS build
USER quarkus
WORKDIR /code
COPY --chown=quarkus:quarkus . /code/
RUN mvn clean package -Pnative -pl common,platform -Dmaven.test.skip=true

## Stage 2 : create the docker final image
FROM quay.io/quarkus/quarkus-micro-image:1.0
WORKDIR /work/
COPY --from=build /code/platform/target/*-runner /work/application

# set up permissions for user `1001`
RUN chmod 775 /work /work/application \
  && chown -R 1001 /work \
  && chmod -R "g+rwX" /work \
  && chown -R 1001:root /work

EXPOSE 8082
USER 1001

CMD ["./application", "-Dquarkus.http.host=0.0.0.0"]
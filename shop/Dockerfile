## Stage 1 : build with maven builder image with native capabilities
FROM registry.cn-hangzhou.aliyuncs.com/zhqn/quarkus-base:1.0.0 AS build
USER quarkus
WORKDIR /code
COPY --chown=quarkus:quarkus . /code/
RUN mvn clean package -Pnative -Pshop -Dmaven.test.skip=true

## Stage 2 : create the docker final image
FROM quay.io/quarkus/quarkus-micro-image:1.0
WORKDIR /work/
RUN mkdir /work/config
COPY --from=build /code/shop/target/*-runner /work/application

# set up permissions for user `1001`
RUN chmod 775 /work /work/application \
  && chown -R 1001 /work \
  && chmod -R "g+rwX" /work \
  && chown -R 1001:root /work

EXPOSE 8081
EXPOSE 9091
USER 1001

CMD ["./application", "-Dquarkus.http.host=0.0.0.0", "-Dquarkus.config.locations=/work/config"]
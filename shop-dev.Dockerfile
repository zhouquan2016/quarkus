FROM registry.cn-hangzhou.aliyuncs.com/zhqn/quarkus-base:1.0.0 AS build
USER quarkus
WORKDIR /code
COPY --chown=quarkus:quarkus . /code/
RUN mvn clean package -Pshop -Dquarkus.swagger-ui.always-include=true -Dmaven.test.skip=true

FROM openjdk:20-slim-buster as runtime
WORKDIR /quarkus-app
COPY --from=build /code/shop/target/quarkus-app/ /quarkus-app/
ENTRYPOINT ["java", "-jar", "/quarkus-run.jar"]
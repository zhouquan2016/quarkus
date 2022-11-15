FROM registry.cn-hangzhou.aliyuncs.com/zhqn/quarkus-base:1.0.0 AS build
USER quarkus
WORKDIR /code
COPY --chown=quarkus:quarkus . /code/
RUN mvn clean package -Pshop -Dquarkus.swagger-ui.always-include=true -Dmaven.test.skip=true

FROM openjdk:20-slim-buster as runtime
WORKDIR /quarkus
COPY --from=build /code/shop/target/quarkus-app/quarkus-run.jar /quarkus/run.jar
ENTRYPOINT ["java", "-jar", "/quarkus/run.jar"]
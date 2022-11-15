FROM registry.cn-hangzhou.aliyuncs.com/zhqn/quarkus-base:1.0.0 AS build
USER quarkus
WORKDIR /code
COPY --chown=quarkus:quarkus . /code/
RUN mvn clean package -Pplatform -Dquarkus.swagger-ui.always-include=true -Dmaven.test.skip=true

FROM openjdk:20-slim-buster as runtime
WORKDIR /quarkus-app
COPY --from=build /code/platform/target/quarkus-app/ /quarkus-app/
ENTRYPOINT ["java", "-Dquarkus.http.host=0.0.0.0", "-Dquarkus.config.locations=/work/config", "-jar", "quarkus-run.jar"]
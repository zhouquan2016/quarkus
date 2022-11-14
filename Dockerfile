FROM registry.cn-hangzhou.aliyuncs.com/zhqn/quarkus-maven:java17
USER quarkus
WORKDIR /code
COPY --chown=quarkus:quarkus ./pom.xml /code/
COPY --chown=quarkus:quarkus ./common/pom.xml /code/common/
COPY --chown=quarkus:quarkus ./shop/pom.xml /code/shop/
COPY --chown=quarkus:quarkus ./platform/pom.xml /code/platform/
RUN mvn compile -Dmaven.test.skip=true
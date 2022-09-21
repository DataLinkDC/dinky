# stage 0, 依赖缓存
FROM maven:3.8.4-jdk-8 as deps-stage
WORKDIR /app
ADD dlink.tar.gz ./
RUN mvn -T 1C -B dependency:go-offline --fail-never || true

# stage 0, 引入plugins
FROM maven:3.8.4-jdk-8 as plugin-stage
WORKDIR /app
ADD https://archive.apache.org/dist/flink/flink-1.14.5/flink-1.14.5-bin-scala_2.12.tgz /tmp
RUN tar zxvf /tmp/flink-1.14.5-bin-scala_2.12.tgz -C /app --strip-components=1


# stage 1, build stage
FROM maven:3.8.4-jdk-8 as build-stage
WORKDIR /app
COPY --from=deps-stage /root/.m2/repository /root/.m2/repository
COPY --from=deps-stage /app/ /app
ADD .  /app
RUN mvn clean package -Dmaven.test.skip=true
RUN mkdir /tmp/dist
RUN tar zxvf ./build/dlink-release-*.tar.gz -C /tmp/dist --strip-components=1


# stage 2, production stage
FROM openjdk:8-oracle as production-stage
WORKDIR /app
RUN mkdir plugins
COPY --from=build-stage /tmp/dist /app
COPY --from=plugin-stage /app/lib /app/plugins


CMD [ "/bin/sh", "-c", "/app/auto.sh run" ]
# stage 0, 依赖缓存
FROM maven:3.8.4-jdk-8 as deps-stage
WORKDIR /app
ADD ./pom.xml ./pom.xml
ADD ./dinky-core/pom.xml ./dinky-core/pom.xml
ADD ./dinky-executor/pom.xml ./dinky-executor/pom.xml
ADD ./dinky-connectors/pom.xml ./dinky-connectors/pom.xml
ADD ./dinky-connectors/dinky-connector-jdbc-1.14/pom.xml ./dinky-connectors/dinky-connector-jdbc-1.14/pom.xml
ADD ./dinky-connectors/dinky-connector-jdbc-1.13/pom.xml ./dinky-connectors/dinky-connector-jdbc-1.13/pom.xml
ADD ./dinky-connectors/dinky-connector-jdbc-1.12/pom.xml ./dinky-connectors/dinky-connector-jdbc-1.12/pom.xml
ADD ./dinky-connectors/dinky-connector-jdbc-1.11/pom.xml ./dinky-connectors/dinky-connector-jdbc-1.11/pom.xml
ADD ./dinky-alert/pom.xml ./dinky-alert/pom.xml
ADD ./dinky-alert/dinky-alert-base/pom.xml ./dinky-alert/dinky-alert-base/pom.xml
ADD ./dinky-alert/dinky-alert-wechat/pom.xml ./dinky-alert/dinky-alert-wechat/pom.xml
ADD ./dinky-alert/dinky-alert-dingtalk/pom.xml ./dinky-alert/dinky-alert-dingtalk/pom.xml
ADD ./dinky-metadata/pom.xml ./dinky-metadata/pom.xml
ADD ./dinky-metadata/dinky-metadata-postgresql/pom.xml ./dinky-metadata/dinky-metadata-postgresql/pom.xml
ADD ./dinky-metadata/dinky-metadata-clickhouse/pom.xml ./dinky-metadata/dinky-metadata-clickhouse/pom.xml
ADD ./dinky-metadata/dinky-metadata-mysql/pom.xml ./dinky-metadata/dinky-metadata-mysql/pom.xml
ADD ./dinky-metadata/dinky-metadata-phoenix/pom.xml ./dinky-metadata/dinky-metadata-phoenix/pom.xml
ADD ./dinky-metadata/dinky-metadata-sqlserver/pom.xml ./dinky-metadata/dinky-metadata-sqlserver/pom.xml
ADD ./dinky-metadata/dinky-metadata-hive/pom.xml ./dinky-metadata/dinky-metadata-hive/pom.xml
ADD ./dinky-metadata/dinky-metadata-base/pom.xml ./dinky-metadata/dinky-metadata-base/pom.xml
ADD ./dinky-metadata/dinky-metadata-doris/pom.xml ./dinky-metadata/dinky-metadata-doris/pom.xml
ADD ./dinky-metadata/dinky-metadata-oracle/pom.xml ./dinky-metadata/dinky-metadata-oracle/pom.xml
ADD ./dinky-metadata/dinky-metadata-presto/pom.xml ./dinky-metadata/dinky-metadata-presto/pom.xml
ADD ./dinky-admin/pom.xml ./dinky-admin/pom.xml
ADD ./dinky-extends/pom.xml ./dinky-extends/pom.xml
ADD ./dinky-app/pom.xml ./dinky-app/pom.xml
ADD ./dinky-app/dinky-app-1.13/pom.xml ./dinky-app/dinky-app-1.13/pom.xml
ADD ./dinky-app/dinky-app-1.14/pom.xml ./dinky-app/dinky-app-1.14/pom.xml
ADD ./dinky-app/dinky-app-1.12/pom.xml ./dinky-app/dinky-app-1.12/pom.xml
ADD ./dinky-app/dinky-app-base/pom.xml ./dinky-app/dinky-app-base/pom.xml
ADD ./dinky-app/dinky-app-1.11/pom.xml ./dinky-app/dinky-app-1.11/pom.xml
ADD ./dinky-gateway/pom.xml ./dinky-gateway/pom.xml
ADD ./dinky-common/pom.xml ./dinky-common/pom.xml
ADD ./dinky-daemon/pom.xml ./dinky-daemon/pom.xml
ADD ./dinky-assembly/pom.xml ./dinky-assembly/pom.xml
ADD ./dinky-client/pom.xml ./dinky-client/pom.xml
ADD ./dinky-client/dinky-client-1.11/pom.xml ./dinky-client/dinky-client-1.11/pom.xml
ADD ./dinky-client/dinky-client-base/pom.xml ./dinky-client/dinky-client-base/pom.xml
ADD ./dinky-client/dinky-client-hadoop/pom.xml ./dinky-client/dinky-client-hadoop/pom.xml
ADD ./dinky-client/dinky-client-1.12/pom.xml ./dinky-client/dinky-client-1.12/pom.xml
ADD ./dinky-client/dinky-client-1.14/pom.xml ./dinky-client/dinky-client-1.14/pom.xml
ADD ./dinky-client/dinky-client-1.13/pom.xml ./dinky-client/dinky-client-1.13/pom.xml
ADD ./dinky-function/pom.xml ./dinky-function/pom.xml
ADD ./dinky-web/pom.xml ./dinky-web/pom.xml
RUN mvn -T 1C -B dependency:go-offline --fail-never || true

# stage 0, 引入plugins
FROM maven:3.8.4-jdk-8 as plugin-stage
WORKDIR /app
ADD https://archive.apache.org/dist/flink/flink-1.13.3/flink-1.13.3-bin-scala_2.12.tgz /tmp
RUN tar zxvf /tmp/flink-1.13.3-bin-scala_2.12.tgz -C /app --strip-components=1


# stage 1, build stage
FROM maven:3.8.4-jdk-8 as build-stage
WORKDIR /app
COPY --from=deps-stage /root/.m2/repository /root/.m2/repository
COPY --from=deps-stage /app/ /app
ADD .  /app
RUN mvn clean package -Dmaven.test.skip=true
RUN mkdir /tmp/dist
RUN tar zxvf ./build/dinky-release-*.tar.gz -C /tmp/dist --strip-components=1


# stage 2, production stage
FROM openjdk:8-oracle as production-stage
WORKDIR /app
RUN mkdir plugins
COPY --from=build-stage /tmp/dist /app
COPY --from=plugin-stage /app/lib /app/plugins


CMD [ "/bin/sh", "-c", "java -Dloader.path=./lib,./plugins -Ddruid.mysql.usePingMethod=false -jar -Xms512M -Xmx2048M ./dinky-admin-*.jar" ]

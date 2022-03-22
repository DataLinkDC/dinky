# stage 0, build stage
FROM maven:3.8.4-jdk-8 as build-stage
WORKDIR /app
ADD ./pom.xml ./pom.xml
ADD ./dlink-admin/pom.xml ./dlink-admin/pom.xml
ADD ./dlink-alert/pom.xml ./dlink-alert/pom.xml
ADD ./dlink-alert/dlink-alert-base/pom.xml ./dlink-alert/dlink-alert-base/pom.xml
ADD ./dlink-alert/dlink-alert-dingtalk/pom.xml ./dlink-alert/dlink-alert-dingtalk/pom.xml
ADD ./dlink-alert/dlink-alert-wechat/pom.xml ./dlink-alert/dlink-alert-wechat/pom.xml
ADD ./dlink-app/pom.xml ./dlink-app/pom.xml
ADD ./dlink-assembly/pom.xml ./dlink-assembly/pom.xml
ADD ./dlink-client/pom.xml ./dlink-client/pom.xml
ADD ./dlink-client/dlink-client-1.11/pom.xml ./dlink-client/dlink-client-1.11/pom.xml
ADD ./dlink-client/dlink-client-1.12/pom.xml ./dlink-client/dlink-client-1.12/pom.xml
ADD ./dlink-client/dlink-client-1.13/pom.xml ./dlink-client/dlink-client-1.13/pom.xml
ADD ./dlink-client/dlink-client-1.14/pom.xml ./dlink-client/dlink-client-1.14/pom.xml
ADD ./dlink-client/dlink-client-base/pom.xml ./dlink-client/dlink-client-base/pom.xml
ADD ./dlink-client/dlink-client-hadoop/pom.xml ./dlink-client/dlink-client-hadoop/pom.xml
ADD ./dlink-common/pom.xml ./dlink-common/pom.xml
ADD ./dlink-connectors/pom.xml ./dlink-connectors/pom.xml
ADD ./dlink-connectors/dlink-connector-jdbc-1.12/pom.xml ./dlink-connectors/dlink-connector-jdbc-1.12/pom.xml
ADD ./dlink-connectors/dlink-connector-jdbc-1.13/pom.xml ./dlink-connectors/dlink-connector-jdbc-1.13/pom.xml
ADD ./dlink-core/pom.xml ./dlink-core/pom.xml
ADD ./dlink-daemon/pom.xml ./dlink-daemon/pom.xml
ADD ./dlink-executor/pom.xml ./dlink-executor/pom.xml
ADD ./dlink-extends/pom.xml ./dlink-extends/pom.xml
ADD ./dlink-function/pom.xml ./dlink-function/pom.xml
ADD ./dlink-gateway/pom.xml ./dlink-gateway/pom.xml
ADD ./dlink-metadata/pom.xml ./dlink-metadata/pom.xml
ADD ./dlink-metadata/dlink-metadata-base/pom.xml ./dlink-metadata/dlink-metadata-base/pom.xml
ADD ./dlink-metadata/dlink-metadata-clickhouse/pom.xml ./dlink-metadata/dlink-metadata-clickhouse/pom.xml
ADD ./dlink-metadata/dlink-metadata-doris/pom.xml ./dlink-metadata/dlink-metadata-doris/pom.xml
ADD ./dlink-metadata/dlink-metadata-mysql/pom.xml ./dlink-metadata/dlink-metadata-mysql/pom.xml
ADD ./dlink-metadata/dlink-metadata-oracle/pom.xml ./dlink-metadata/dlink-metadata-oracle/pom.xml
ADD ./dlink-metadata/dlink-metadata-phoenix/pom.xml ./dlink-metadata/dlink-metadata-phoenix/pom.xml
ADD ./dlink-metadata/dlink-metadata-postgresql/pom.xml ./dlink-metadata/dlink-metadata-postgresql/pom.xml
ADD ./dlink-metadata/dlink-metadata-sqlserver/pom.xml ./dlink-metadata/dlink-metadata-sqlserver/pom.xml
ADD ./dlink-web/pom.xml ./dlink-web/pom.xml
RUN mvn dependency:go-offline
ADD .  /app
RUN sh build.sh
RUN mkdir /tmp/dist
RUN tar zxvf ./build/dlink-release-*.tar.gz -C /tmp/dist --strip-components=1

## 引入plugins
#RUN mkdir plugins
#ADD ./docker/dinky/server/libs/pom.xml /app/plugins/pom.xml
#RUN mvn dependency:copy-dependencies -f /app/plugins/pom.xml -DoutputDirectory=/tmp/dist/plugins

# stage 1, production stage
FROM maven:3.8.4-jdk-8 as production-stage
WORKDIR /app
COPY --from=build-stage /tmp/dist/ /app


CMD ["java",\
     "-Dloader.path=./lib,./plugins",\
     "-Ddruid.mysql.usePingMethod=false",\
     "-jar",\
     "-Xms512M",\
     "-Xmx2048M",\
     "./dlink-admin-0.5.1.jar" ]
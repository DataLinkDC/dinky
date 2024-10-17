ARG FLINK_VERSION
ARG DINKY_VERSION

FROM flink:${FLINK_VERSION}-scala_2.12-java8 as flink-base

FROM  node:18.15.0-alpine3.17 AS ui-build
WORKDIR /build/

ENV NODE_OPTIONS=--openssl-legacy-provider
ENV UMI_ENV=production

# 单独分离 package.json，是为了安装依赖可最大限度利用缓存
ADD ./dinky-web/package.json /build/package.json
RUN npm install --legacy-peer-deps
ADD ./dinky-web .
RUN npm run build

FROM maven:3.9-eclipse-temurin-8-alpine AS build
WORKDIR /build/
ARG FLINK_VERSION
ARG DINKY_VERSION
ENV FLINK_VERSION=${FLINK_VERSION}
ENV DINKY_VERSION=${DINKY_VERSION}

ADD . .
COPY --from=ui-build /build/dist/ /build/dinky-web/dist/

RUN mvn package -Dmaven.test.skip=true -P prod,flink-single-version,flink-${FLINK_VERSION},fast
RUN mkdir release &&  \
    tar -C release -xvf build/dinky-release-${FLINK_VERSION}-${DINKY_VERSION}.tar.gz && \
    mv release/dinky-release-* release/dinky


FROM eclipse-temurin:8-jre-jammy

ARG FLINK_VERSION
ENV FLINK_VERSION=${FLINK_VERSION}
ENV DINKY_HOME=/opt/dinky/
ENV H2_DB=./tmp/db/h2

WORKDIR /opt/dinky/

USER root

COPY --from=build /build/release/dinky /opt/dinky/
COPY --from=flink-base  /opt/flink/lib/*.jar  /opt/dinky/extends/flink${FLINK_VERSION}/flink/
RUN rm -f  /opt/dinky/extends/flink${FLINK_VERSION}/flink/flink-table-planner-loader*.jar

COPY --from=flink-base  /opt/flink/opt/flink-table-planner*.jar /opt/dinky/extends/flink${FLINK_VERSION}/flink/

RUN mkdir /opt/dinky/customJar && chmod -R 777 /opt/dinky/ && sed -i 's/-Xms512M -Xmx2048M -XX:PermSize=512M/-XX:+UseContainerSupport -XX:InitialRAMPercentage=70.0 -XX:MaxRAMPercentage=70.0/g' auto.sh

EXPOSE 8888

CMD  ./auto.sh startOnPending
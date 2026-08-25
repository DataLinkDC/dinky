ARG FLINK_VERSION

FROM flink:${FLINK_VERSION}-scala_2.12-java8 as flink-base


FROM eclipse-temurin:8-jre-jammy

ARG FLINK_VERSION
ENV FLINK_VERSION=${FLINK_VERSION}
ENV DINKY_HOME=/opt/dinky/
ENV H2_DB=./tmp/db/h2
ENV JVM_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70.0"
ADD build/dinky-release*.tar.gz /opt
RUN ls /opt && mv /opt/dinky-release* /opt/dinky


WORKDIR /opt/dinky/

USER root
COPY --from=flink-base  /opt/flink/lib/*.jar  /opt/dinky/extends/flink${FLINK_VERSION}/flink/

COPY --from=flink-base  /opt/flink/opt/flink-table-planner*.jar /opt/dinky/extends/flink${FLINK_VERSION}/flink/

RUN mkdir /opt/dinky/customJar && chmod -R 777 /opt/dinky/

EXPOSE 8888

CMD  ./bin/auto.sh startOnPending

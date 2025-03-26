ARG FLINK_VERSION

FROM flink:${FLINK_VERSION}-scala_2.12-java8 as flink-base


FROM eclipse-temurin:8-jre-jammy

ARG FLINK_VERSION
ENV FLINK_VERSION=${FLINK_VERSION}
ENV DINKY_HOME=/opt/dinky/
ENV H2_DB=./tmp/db/h2
ADD build/dinky-release*.tar.gz /opt
RUN ls /opt && mv /opt/dinky-release* /opt/dinky


WORKDIR /opt/dinky/

USER root
COPY --from=flink-base  /opt/flink/lib/*.jar  /opt/dinky/extends/flink${FLINK_VERSION}/flink/
RUN rm -f  /opt/dinky/extends/flink${FLINK_VERSION}/flink/flink-table-planner-loader*.jar

COPY --from=flink-base  /opt/flink/opt/flink-table-planner*.jar /opt/dinky/extends/flink${FLINK_VERSION}/flink/

RUN mkdir /opt/dinky/customJar && chmod -R 777 /opt/dinky/ && sed -i 's/-Xms512M -Xmx2048M -XX:PermSize=512M/-XX:+UseContainerSupport -XX:InitialRAMPercentage=70.0 -XX:MaxRAMPercentage=70.0/g' ./bin/auto.sh

EXPOSE 8888

CMD  ./bin/auto.sh startOnPending

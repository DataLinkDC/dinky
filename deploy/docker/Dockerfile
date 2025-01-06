ARG FLINK_VERSION
ARG DINKY_VERSION

FROM flink:${FLINK_VERSION}-scala_2.12-java8 as builder
ARG FLINK_VERSION
ADD build/dinky-release*.tar.gz /opt
RUN  mv /opt/dinky-release* /opt/dinky  && mkdir /opt/dinky/config/static
ADD  build/dist/ /opt/dinky/config/static

FROM flink:${FLINK_VERSION}-scala_2.12-java8
ARG FLINK_VERSION
COPY --from=builder /opt/dinky /opt/dinky
RUN rm -f /opt/flink/lib/flink-table-planner-loader*.jar  &&  cp /opt/flink/opt/flink-python*.jar /opt/flink/lib/ && cp /opt/flink/opt/flink-table-planner*.jar /opt/flink/lib/ 2>/dev/null || : && ln -s /opt/flink/lib/*  /opt/dinky/extends/flink${FLINK_VERSION}/
ENV FLINK_VERSION=${FLINK_VERSION}
ENV DINKY_HOME=/opt/dinky/
ENV H2_DB=./tmp/db/h2


USER root

WORKDIR /opt/dinky/


RUN mkdir /opt/dinky/customJar && chmod -R 777 /opt/dinky/ && mkdir /dinky && chmod -R 777 /dinky/ && sed -i 's/-Xms512M -Xmx2048M -XX:ermSize=512M/-XX:+UseContainerSupport -XX:InitialRAMPercentage=70.0 -XX:MaxRAMPercentage=70.0/g' ./bin/auto.sh

EXPOSE 8888

CMD  ./bin/auto.sh startOnPending

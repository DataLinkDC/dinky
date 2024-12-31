ARG FLINK_VERSION
ARG DINKY_VERSION

FROM flink:${FLINK_VERSION}-scala_2.12-java8
RUN rm -f /opt/flink/lib/flink-table-planner-loader*.jar  &&  cp /opt/flink/opt/flink-python*.jar /opt/flink/lib/ && cp /opt/flink/opt/flink-table-planner*.jar /opt/flink/lib/ 2>/dev/null || :
ARG FLINK_VERSION
ENV FLINK_VERSION=${FLINK_VERSION}
ENV DINKY_HOME=/opt/dinky/
ENV H2_DB=./tmp/db/h2


USER root

ADD build/dinky-release*.tar.gz /opt
RUN  mv /opt/dinky-release* /opt/dinky && ln -s /opt/flink/lib/*  /opt/dinky/extends/flink${FLINK_VERSION}/ && mkdir /opt/dinky/config/static
ADD  build/dist/ /opt/dinky/config/static

WORKDIR /opt/dinky/


RUN mkdir /opt/dinky/customJar && chmod -R 777 /opt/dinky/ && sed -i 's/-Xms512M -Xmx2048M -XX:PermSize=512M/-XX:+UseContainerSupport -XX:InitialRAMPercentage=70.0 -XX:MaxRAMPercentage=70.0/g' ./bin/auto.sh

EXPOSE 8888

CMD  ./bin/auto.sh startOnPending

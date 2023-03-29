# dinky-server build dokcer(no web)
# mvn clean install -Dmaven.test.skip=true -P !web,aliyun,prod,scala-2.12,fast,flink-1.14
FROM openjdk:8u342-oracle as build-stage

# dinky version env
ARG DINKY_VERSION=0.8.0
ARG FLINK_VERSION=1.14
ENV DINKY_VERSION=${DINKY_VERSION}
ENV FLINK_VERSION=${FLINK_VERSION}

# add dinky-server(no web)
ADD ./build/dinky-release-${DINKY_VERSION}.tar.gz  /opt/

# mkdir file
USER root
RUN mv /opt/dinky-release-${DINKY_VERSION} /opt/dinky/
RUN mkdir -p /opt/dinky/run && mkdir -p /opt/dinky/logs &&  touch /opt/dinky/logs/dinky.log

# download flink file
#ADD https://archive.apache.org/dist/flink/flink-1.14.6/flink-1.14.6-bin-scala_2.12.tgz /tmp
#RUN tar zxvf /tmp/flink-1.14.6-bin-scala_2.12.tgz -C /opt
#RUN cp -r /opt/flink-1.14.6/lib /opt/dinky/plugins/flink${FLINK_VERSION}

RUN chmod -R 777 /opt/dinky/

WORKDIR /opt/dinky/

EXPOSE 8888


ENTRYPOINT  java -cp ./lib/*:./config/*:./plugins/*:./plugins/flink${FLINK_VERSION}/* org.dinky.Dinky
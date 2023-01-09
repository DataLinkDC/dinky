FROM openjdk:8u342-oracle as build-stage

ARG DINKY_VERSION
ENV DINKY_VERSION=${DINKY_VERSION}

ADD ./build/dlink-release-${DINKY_VERSION}.tar.gz  /opt/

USER root
RUN mv /opt/dlink-release-${DINKY_VERSION} /opt/dinky/
RUN mkdir -p /opt/dinky/run && mkdir -p /opt/dinky/logs &&  touch /opt/dinky/logs/dlink.log
RUN chmod -R 777 /opt/dinky/

FROM openjdk:8u342-oracle as production-stage
COPY --from=build-stage /opt/dinky/ /opt/dinky/
WORKDIR /opt/dinky/

EXPOSE 8888

CMD  ./auto.sh restart && tail -f /opt/dinky/logs/dlink.log
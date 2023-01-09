
FROM flink:1.14.5 as production-stage

USER root
COPY ./auto.sh  /opt/dinky/
COPY ./lib  /opt/dinky/lib/
COPY ./plugins  /opt/dinky/plugins/
COPY ./config /opt/dinky/config/
COPY ./jar  /opt/dinky/jar/
WORKDIR /opt/dinky/

RUN cp /opt/flink/lib/*.jar  /opt/dinky/plugins/
RUN mkdir -p /opt/dinky/run
RUN mkdir -p /opt/dinky/logs
RUN touch /opt/dinky/logs/dlink.log
RUN chmod -R 777 /opt/dinky/

EXPOSE 8888

CMD  ./auto.sh restart && tail -f /opt/dinky/logs/dlink.log
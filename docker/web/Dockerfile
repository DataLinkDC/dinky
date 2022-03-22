# stage 0, build stage
FROM maven:3.8.4-jdk-8 as build-stage
WORKDIR /app
ADD .  /app
RUN sh build.sh
RUN mkdir /tmp/dist
RUN tar zxvf ./build/dlink-release-*.tar.gz -C /tmp/dist --strip-components=1


FROM nginx:1.21.6 AS production-stage
WORKDIR /app
COPY --from=build-stage /tmp/dist/html /app
COPY ./docker/web/dinky.conf.template /etc/nginx/templates/default.conf.template
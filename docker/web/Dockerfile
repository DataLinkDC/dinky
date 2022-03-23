# stage 0, build stage
FROM sivacohan/dinky-server:lastest as build-stage

FROM nginx:1.21.6 AS production-stage
WORKDIR /app
COPY --from=build-stage /app/html /app
COPY ./docker/web/dinky.conf.template /etc/nginx/templates/default.conf.template
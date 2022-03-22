# stage 0, build stage
FROM maven:3.8.4-jdk-8 as build-stage
WORKDIR /app
ADD ./pom.xml /app/pom.xml
RUN mvn install
ADD .   /app
RUN sh build.sh
RUN mkdir /tmp/dist
RUN tar zxvf /app/build/dlink-release-*.tar.gz -C /tmp/dist --strip-components=1

# stage 1, production stage
FROM mysql:5.7 as production-stage
COPY --from=build-stage /tmp/dist/sql/dlink.sql /docker-entrypoint-initdb.d/01-dlink.sql
COPY --from=build-stage /tmp/dist/sql/dlink_history.sql /docker-entrypoint-initdb.d/02-dlink_history.sql

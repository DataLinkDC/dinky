# stage 0, build stage
FROM maven:3.8.4-jdk-8 as build-stage
WORKDIR /app
ADD .  /app
RUN  mvn -T 1C clean package -Dmaven.test.skip=true
RUN mkdir /tmp/dist
RUN tar zxvf ./build/dlink-release-*.tar.gz -C /tmp/dist
#
## 引入plugins
#RUN mkdir plugins
#ADD ./docker/server/pom.xml /app/plugins/pom.xml
#RUN mvn dependency:copy-dependencies -f /app/plugins/pom.xml -DoutputDirectory=/tmp/dist/plugins
#
## stage 1, production stage
#FROM maven:3.8.4-jdk-8 as production-stage
#WORKDIR /app
#COPY --from=build-stage /tmp/dist/ /app
#
#
CMD ["java",\
     "-Dloader.path=./lib,./plugins",\
     "-Ddruid.mysql.usePingMethod=false",\
     "-jar",\
     "-Xms512M",\
     "-Xmx2048M",\
     "./dlink-admin-0.5.1.jar" ]
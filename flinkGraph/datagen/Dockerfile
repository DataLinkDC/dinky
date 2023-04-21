FROM maven:3.8.6-jdk-11-slim AS builder

RUN mkdir -p /opt/datagen;
COPY user_behavior.log /opt/datagen
COPY first-1.0-SNAPSHOT.jar /opt/datagen

WORKDIR /opt/datagen
ENV DATEGEN_HOME /opt/datagen
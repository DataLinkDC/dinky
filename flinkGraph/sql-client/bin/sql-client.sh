#!/bin/bash

${FLINK_HOME}/bin/sql-client.sh embedded -i ${FLINK_HOME}/conf/init.sql -l ${SQL_CLIENT_HOME}/lib
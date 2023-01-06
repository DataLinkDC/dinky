/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */


export const RUN_MODE = {
  LOCAL: 'local',
  STANDALONE: 'standalone',
  YARN_SESSION: 'yarn-session',
  YARN_PER_JOB: 'yarn-per-job',
  YARN_APPLICATION: 'yarn-application',
  KUBERNETES_SESSION: 'kubernetes-session',
  KUBERNETES_APPLICATION: 'kubernetes-application',
};

export const DIALECT = {
  FLINKSQL: 'FlinkSql',
  FLINKJAR: 'FlinkJar',
  FLINKSQLENV: 'FlinkSqlEnv',
  SQL: 'Sql',
  MYSQL: 'Mysql',
  ORACLE: 'Oracle',
  SQLSERVER: 'SqlServer',
  POSTGRESQL: 'PostgreSql',
  CLICKHOUSE: 'ClickHouse',
  DORIS: 'Doris',
  HIVE: 'Hive',
  PHOENIX: 'Phoenix',
  STARROCKS: 'StarRocks',
  PRESTO: 'Presto',
  KUBERNETES_APPLICATION: 'KubernetesApplaction',
  JAVA: 'Java',
  SCALA: 'Scala',
  PYTHON: 'Python',
};


export const isSql = (dialect: string) => {
  switch (dialect) {
    case DIALECT.SQL:
    case DIALECT.MYSQL:
    case DIALECT.ORACLE:
    case DIALECT.SQLSERVER:
    case DIALECT.POSTGRESQL:
    case DIALECT.CLICKHOUSE:
    case DIALECT.PHOENIX:
    case DIALECT.DORIS:
    case DIALECT.HIVE:
    case DIALECT.STARROCKS:
    case DIALECT.PRESTO:
      return true;
    default:
      return false;
  }
};

export const isExecuteSql = (dialect: string) => {
  if (!dialect) {
    return true;
  }
  switch (dialect) {
    case DIALECT.SQL:
    case DIALECT.MYSQL:
    case DIALECT.ORACLE:
    case DIALECT.SQLSERVER:
    case DIALECT.POSTGRESQL:
    case DIALECT.CLICKHOUSE:
    case DIALECT.DORIS:
    case DIALECT.PHOENIX:
    case DIALECT.FLINKSQL:
    case DIALECT.HIVE:
    case DIALECT.STARROCKS:
    case DIALECT.PRESTO:
      return true;
    default:
      return false;
  }
};

export const isTask = (dialect: string) => {
  if (!dialect) {
    return true;
  }
  switch (dialect) {
    case DIALECT.SQL:
    case DIALECT.MYSQL:
    case DIALECT.ORACLE:
    case DIALECT.SQLSERVER:
    case DIALECT.POSTGRESQL:
    case DIALECT.CLICKHOUSE:
    case DIALECT.DORIS:
    case DIALECT.PHOENIX:
    case DIALECT.FLINKSQL:
    case DIALECT.FLINKJAR:
    case DIALECT.HIVE:
    case DIALECT.STARROCKS:
    case DIALECT.PRESTO:
    case DIALECT.KUBERNETES_APPLICATION:
      return true;
    default:
      return false;
  }
};

export const isRunningTask = (jobInstanceId: number) => {
  if (jobInstanceId && jobInstanceId != 0) {
    return true;
  }
  return false;
};

export const isOnline = (type: string) => {
  switch (type) {
    case RUN_MODE.LOCAL:
    case RUN_MODE.STANDALONE:
    case RUN_MODE.YARN_SESSION:
    case RUN_MODE.KUBERNETES_SESSION:
      return true;
    default:
      return false;
  }
}


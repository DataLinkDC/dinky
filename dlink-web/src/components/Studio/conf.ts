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
  POSTGRESQL: 'PostGreSql',
  CLICKHOUSE: 'ClickHouse',
  DORIS: 'Doris',
  HIVE: 'Hive',
  PHOENIX: 'Phoenix',
  JAVA: 'Java',
};

export const CHART = {
  LINE: '折线图',
  BAR: '条形图',
  PIE: '饼图',
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


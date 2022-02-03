export const RUN_MODE = {
  LOCAL:'local',
  STANDALONE:'standalone',
  YARN_SESSION:'yarn-session',
  YARN_PER_JOB:'yarn-per-job',
  YARN_APPLICATION:'yarn-application',
  KUBERNETES_SESSION:'kubernetes-session',
  KUBERNETES_APPLICATION:'kubernetes-application',
};

export const DIALECT = {
  FLINKSQL:'FlinkSql',
  FLINKJAR:'FlinkJar',
  FLINKSQLENV:'FlinkSqlEnv',
  SQL:'Sql',
  MYSQL:'Mysql',
  ORACLE:'Oracle',
  SQLSERVER:'SqlServer',
  POSTGRESQL:'PostGreSql',
  CLICKHOUSE:'ClickHouse',
  DORIS:'Doris',
  JAVA:'Java',
};

export const CHART = {
  LINE:'折线图',
  BAR:'条形图',
  PIE:'饼图',
};

export const isSql = (dialect: string)=>{
  switch (dialect){
    case DIALECT.SQL:
    case DIALECT.MYSQL:
    case DIALECT.ORACLE:
    case DIALECT.SQLSERVER:
    case DIALECT.POSTGRESQL:
    case DIALECT.CLICKHOUSE:
    case DIALECT.DORIS:
      return true;
    default:
      return false;
  }
};

export const isOnline = (type: string)=>{
  switch (type){
    case RUN_MODE.LOCAL:
    case RUN_MODE.STANDALONE:
    case RUN_MODE.YARN_SESSION:
    case RUN_MODE.KUBERNETES_SESSION:
      return true;
    default:
      return false;
  }
}

export const TASKSTEPS = {
  UNKNOWN: 0,
  CREATE: 1,
  DEVELOP: 2,
  DEBUG: 3,
  RELEASE: 4,
  ONLINE: 5,
  CANCEL: 6,
};


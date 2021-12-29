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
  FLINKSQLENV:'FlinkSqlEnv',
  SQL:'Sql',
  MYSQL:'Mysql',
  ORACLE:'Oracle',
  POSTGRESQL:'PostGreSql',
  CLICKHOUSE:'ClickHouse',
  DORIS:'Doris',
  JAVA:'Java',
};

export const isSql = (type: string)=>{
  switch (type){
    case DIALECT.SQL:
    case DIALECT.MYSQL:
    case DIALECT.ORACLE:
    case DIALECT.POSTGRESQL:
    case DIALECT.CLICKHOUSE:
    case DIALECT.DORIS:
      return true;
    default:
      return false;
  }
}

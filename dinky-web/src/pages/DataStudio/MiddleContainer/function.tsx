import {DIALECT} from "@/services/constants";
import {
  FileIcon,
  FlinkSQLEnvSvg,
  FlinkSQLSvg,
  JavaSvg,
  LogSvg,
  MarkDownSvg,
  PythonSvg,
  ScalaSvg,
  ShellSvg,
  XMLSvg,
  YAMLSvg
} from "@/components/Icons/CodeLanguageIcon";
import {ClickHouseIcons, DorisIcons, HiveIcons, MysqlIcons, OracleIcons, PhoenixIcons,
  PostgresqlIcons, PrestoIcons, SqlServerIcons, StarRocksIcons} from "@/components/Icons/DBIcons";

export const getTabIcon = (type: string, size?: number) => {
  if (!type){
    return <FileIcon/>;
  }

  switch (type.toLowerCase()) {
    case DIALECT.JAVA:
      return <JavaSvg/>;
    case DIALECT.SCALA:
      return <ScalaSvg/>;
    case DIALECT.PYTHON:
    case DIALECT.PYTHON_LONG:
      return <PythonSvg/>;
    case DIALECT.MD:
    case DIALECT.MDX:
      return <MarkDownSvg/>;
    case DIALECT.XML:
      return <XMLSvg/>;
    case DIALECT.YAML:
    case DIALECT.YML:
      return <YAMLSvg/>;
    case DIALECT.SH:
    case DIALECT.BASH:
    case DIALECT.CMD:
      return <ShellSvg/>;
    case DIALECT.LOG:
      return <LogSvg/>;
    case DIALECT.FLINK_SQL:
      return <FlinkSQLSvg/>;
      case DIALECT.FLINKSQLENV:
      return <FlinkSQLEnvSvg/>;
    case DIALECT.MYSQL:
      return <MysqlIcons size={size}/>;
    case DIALECT.ORACLE:
      return <OracleIcons size={size}/>;
    case DIALECT.POSTGRESQL:
      return <PostgresqlIcons size={size}/>;
    case DIALECT.CLICKHOUSE:
      return <ClickHouseIcons size={size}/>;
    case DIALECT.SQLSERVER:
      return <SqlServerIcons size={size}/>;
    case DIALECT.DORIS :
      return <DorisIcons size={size}/>;
    case DIALECT.PHOENIX :
      return <PhoenixIcons size={size}/>;
    case DIALECT.HIVE :
      return <HiveIcons size={size}/>;
    case DIALECT.STARROCKS :
      return <StarRocksIcons size={size}/>;
    case DIALECT.PRESTO :
      return <PrestoIcons size={size}/>;
    default:
      return <FileIcon/>;
  }
};

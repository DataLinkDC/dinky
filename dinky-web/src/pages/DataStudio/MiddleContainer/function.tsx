import {DIALECT} from "@/services/constants";
import {FileIcon, FlinkSQLSvg, JavaSvg, LogSvg, MarkDownSvg, PythonSvg, ScalaSvg, ShellSvg, XMLSvg, YAMLSvg} from "@/components/Icons/CodeLanguageIcon";
import {ClickHouseIcons, DorisIcons, HiveIcons, MysqlIcons, OracleIcons, PhoenixIcons,
  PostgresqlIcons, PrestoIcons, SqlServerIcons, StarRocksIcons} from "@/components/Icons/DBIcons";

export const getTabIcon = (type: string, size?: number) => {
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
    case 'mysql':
      return <MysqlIcons size={size}/>;
    case 'oracle':
      return <OracleIcons size={size}/>;
    case 'postgresql':
      return <PostgresqlIcons size={size}/>;
    case 'clickhouse':
      return <ClickHouseIcons size={size}/>;
    case 'sqlserver':
      return <SqlServerIcons size={size}/>;
    case 'doris':
      return <DorisIcons size={size}/>;
    case 'phoenix':
      return <PhoenixIcons size={size}/>;
    case 'hive':
      return <HiveIcons size={size}/>;
    case 'starrocks':
      return <StarRocksIcons size={size}/>;
    case 'presto':
      return <PrestoIcons size={size}/>;
    default:
      return <FileIcon/>;
  }
};

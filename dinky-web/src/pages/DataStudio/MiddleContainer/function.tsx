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

import {
  FileIcon,
  FlinkJarSvg,
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
} from '@/components/Icons/CodeLanguageIcon';
import {
  ClickHouseIcons,
  DorisIcons,
  HiveIcons,
  MysqlIcons,
  OracleIcons,
  PhoenixIcons,
  PostgresqlIcons,
  PrestoIcons,
  SQLIcons,
  SqlServerIcons,
  StarRocksIcons
} from '@/components/Icons/DBIcons';
import { DIALECT } from '@/services/constants';

export const matchLanguage = (language = DIALECT.FLINK_SQL) => {
  switch (language.toLowerCase()) {
    case DIALECT.FLINK_SQL:
    case DIALECT.FLINKSQLENV:
    case DIALECT.FLINKJAR:
      return DIALECT.FLINK_SQL;
    case DIALECT.SQL:
    case DIALECT.SQLSERVER:
    case DIALECT.POSTGRESQL:
    case DIALECT.HIVE:
    case DIALECT.CLICKHOUSE:
    case DIALECT.ORACLE:
    case DIALECT.DORIS:
    case DIALECT.PHOENIX:
    case DIALECT.PRESTO:
    case DIALECT.MYSQL:
    case DIALECT.STARROCKS:
      return DIALECT.SQL;
    case DIALECT.PYTHON:
    case DIALECT.PYTHON_LONG:
      return DIALECT.PYTHON_LONG;
    case DIALECT.SCALA:
      return DIALECT.SCALA;
    case DIALECT.JAVA:
      return DIALECT.JAVA;
    default:
      return DIALECT.SQL;
  }
};
//
export const getTabIcon = (type: string, size?: number) => {
  if (!type) {
    return <FileIcon />;
  }

  switch (type.toLowerCase()) {
    case DIALECT.JAVA:
      return <JavaSvg />;
    case DIALECT.SCALA:
      return <ScalaSvg />;
    case DIALECT.PYTHON:
    case DIALECT.PYTHON_LONG:
      return <PythonSvg />;
    case DIALECT.MD:
    case DIALECT.MDX:
      return <MarkDownSvg />;
    case DIALECT.XML:
      return <XMLSvg />;
    case DIALECT.YAML:
    case DIALECT.YML:
      return <YAMLSvg />;
    case DIALECT.SH:
    case DIALECT.BASH:
    case DIALECT.CMD:
      return <ShellSvg />;
    case DIALECT.LOG:
      return <LogSvg />;
    case DIALECT.FLINKJAR:
      return <FlinkJarSvg />;
    case DIALECT.FLINK_SQL:
      return <FlinkSQLSvg />;
    case DIALECT.FLINKSQLENV:
      return <FlinkSQLEnvSvg />;
    case DIALECT.SQL:
      return <SQLIcons size={size} />;
    case DIALECT.MYSQL:
      return <MysqlIcons size={size} />;
    case DIALECT.ORACLE:
      return <OracleIcons size={size} />;
    case DIALECT.POSTGRESQL:
      return <PostgresqlIcons size={size} />;
    case DIALECT.CLICKHOUSE:
      return <ClickHouseIcons size={size} />;
    case DIALECT.SQLSERVER:
      return <SqlServerIcons size={size} />;
    case DIALECT.DORIS:
      return <DorisIcons size={size} />;
    case DIALECT.PHOENIX:
      return <PhoenixIcons size={size} />;
    case DIALECT.HIVE:
      return <HiveIcons size={size} />;
    case DIALECT.STARROCKS:
      return <StarRocksIcons size={size} />;
    case DIALECT.PRESTO:
      return <PrestoIcons size={size} />;
    default:
      return <FileIcon />;
  }
};

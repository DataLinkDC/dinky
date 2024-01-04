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
  ClickHouseIcons,
  DefaultDBIcons,
  DorisIcons,
  HiveIcons,
  MysqlIcons,
  OracleIcons,
  PhoenixIcons,
  PostgresqlIcons,
  PrestoIcons,
  SqlServerIcons,
  StarRocksIcons
} from '@/components/Icons/DBIcons';
import { QUERY_KEYWORD } from '@/pages/RegCenter/DataSource/components/constants';

/**
 * render DB icon
 * @param type
 * @param size
 */
export const renderDBIcon = (type: string, size?: number) => {
  switch (type.toLowerCase()) {
    case 'mysql':
      return <MysqlIcons size={size} />;
    case 'oracle':
      return <OracleIcons size={size} />;
    case 'postgresql':
      return <PostgresqlIcons size={size} />;
    case 'clickhouse':
      return <ClickHouseIcons size={size} />;
    case 'sqlserver':
      return <SqlServerIcons size={size} />;
    case 'doris':
      return <DorisIcons size={size} />;
    case 'phoenix':
      return <PhoenixIcons size={size} />;
    case 'hive':
      return <HiveIcons size={size} />;
    case 'starrocks':
      return <StarRocksIcons size={size} />;
    case 'presto':
      return <PrestoIcons size={size} />;
    default:
      return <DefaultDBIcons size={size} />;
  }
};

export const buildColumnsQueryKeyWord = (data: string[]) => {
  const result = data.concat(QUERY_KEYWORD).map((item: string | number) => ({
    value: item,
    label: item
  }));
  return result;
};

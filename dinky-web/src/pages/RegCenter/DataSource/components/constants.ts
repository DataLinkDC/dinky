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

import { l } from '@/utils/intl';

/**
 * data source type
 */
export const DATA_SOURCE_TYPE = {
  MYSQL: 'MySQL',
  ORACLE: 'Oracle',
  POSTGRESQL: 'PostgreSQL',
  SQLSERVER: 'SQLServer',
  CLICKHOUSE: 'ClickHouse',
  DORIS: 'Doris',
  STARROCKS: 'StarRocks',
  PRESTO: 'Presto',
  PHOENIX: 'Phoenix',
  HIVE: 'Hive',
  PAIMON: 'Paimon'
};
/**
 * data source type
 */

export const DATA_SOURCE_TYPE_OPTIONS = [
  {
    label: 'OLTP',
    options: [
      {
        label: 'MySQL',
        value: DATA_SOURCE_TYPE.MYSQL
      },
      {
        label: 'Oracle',
        value: DATA_SOURCE_TYPE.ORACLE
      },
      {
        label: 'PostgreSQL',
        value: DATA_SOURCE_TYPE.POSTGRESQL
      },
      {
        label: 'SQLServer',
        value: DATA_SOURCE_TYPE.SQLSERVER
      },
      {
        label: 'Phoenix',
        value: DATA_SOURCE_TYPE.PHOENIX
      }
    ]
  },
  {
    label: 'OLAP',
    options: [
      {
        label: 'ClickHouse',
        value: DATA_SOURCE_TYPE.CLICKHOUSE
      },
      {
        label: 'Doris',
        value: DATA_SOURCE_TYPE.DORIS
      },
      {
        label: 'StarRocks',
        value: DATA_SOURCE_TYPE.STARROCKS
      },
      {
        label: 'Presto',
        value: DATA_SOURCE_TYPE.PRESTO
      },
      {
        label: 'Paimon',
        value: DATA_SOURCE_TYPE.PAIMON
      }
    ]
  },
  {
    label: 'DataWarehouse/DataLake',
    options: [
      {
        label: 'Hive',
        value: DATA_SOURCE_TYPE.HIVE
      }
    ]
  }
];

/**
 * data source  group type
 */
export const GROUP_TYPE = [
  {
    key: 'source',
    value: 'source',
    label: l('rc.ds.source')
  },
  {
    key: 'warehouse',
    value: 'warehouse',
    label: l('rc.ds.warehouse')
  },
  {
    key: 'application',
    value: 'application',
    label: l('rc.ds.application')
  },
  {
    key: 'backup',
    value: 'backup',
    label: l('rc.ds.backup')
  },
  {
    key: 'other',
    value: 'other',
    label: l('rc.ds.other')
  }
];

/**
 * data source url , render autocomplete value
 */
export const AUTO_COMPLETE_TYPE = [
  {
    key: 'mysql',
    value: 'jdbc:mysql://localhost:3306/dinky?useSSL=false&serverTimezone=UTC',
    label: 'jdbc:mysql://localhost:3306/dinky?useSSL=false&serverTimezone=UTC'
  },
  {
    key: 'oracle',
    value: 'jdbc:oracle:thin:@localhost:1521:orcl',
    label: 'jdbc:oracle:thin:@localhost:1521:orcl'
  },
  {
    key: 'postgresql',
    value: 'jdbc:postgresql://localhost:5432/dinky',
    label: 'jdbc:postgresql://localhost:5432/dinky'
  },
  {
    key: 'clickhouse',
    value: 'jdbc:clickhouse://localhost:8123/dinky',
    label: 'jdbc:clickhouse://localhost:8123/dinky'
  },
  {
    key: 'sqlserver',
    value: 'jdbc:sqlserver://localhost:1433;DatabaseName=dinky',
    label: 'jdbc:sqlserver://localhost:1433;DatabaseName=dinky'
  },
  {
    key: 'doris',
    value: 'jdbc:mysql://localhost:9030/dinky?useSSL=false&serverTimezone=UTC',
    label: 'jdbc:mysql://localhost:9030/dinky?useSSL=false&serverTimezone=UTC'
  },
  {
    key: 'phoenix',
    value: 'jdbc:phoenix:localhost:2181',
    label: 'jdbc:phoenix:localhost:2181'
  },
  {
    key: 'hive',
    value: 'jdbc:hive2://localhost:10000/dinky',
    label: 'jdbc:hive2://localhost:10000/dinky'
  },
  {
    key: 'starrocks',
    value: 'jdbc:mysql://localhost:9030/dinky?useSSL=false&serverTimezone=UTC',
    label: 'jdbc:mysql://localhost:9030/dinky?useSSL=false&serverTimezone=UTC'
  },
  {
    key: 'presto',
    value: 'jdbc:presto://localhost:8080/dinky',
    label: 'jdbc:presto://localhost:8080/dinky'
  }
];

export const QUERY_KEYWORD = [
  'and',
  'or',
  'not',
  'in',
  'like',
  'between',
  'is null',
  'is not null',
  'is not',
  '=',
  '!=',
  '<',
  '>',
  '<=',
  '>=',
  '<>',
  '!',
  'desc',
  'asc',
  'not in ()'
];

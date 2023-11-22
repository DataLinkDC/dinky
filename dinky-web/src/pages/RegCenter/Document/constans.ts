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

export const DOCUMENT_CATEGORY = [
  {
    text: 'Variable',
    value: 'Variable'
  },
  {
    text: 'Module',
    value: 'Module'
  },
  {
    text: 'Operator',
    value: 'Operator'
  },
  {
    text: 'Function',
    value: 'Function'
  },
  {
    text: 'Property',
    value: 'Property'
  },
  {
    text: 'Method',
    value: 'Method'
  },
  {
    text: 'Reference',
    value: 'Reference'
  }
];

/**
 * document category enum map
 */
export const DOCUMENT_CATEGORY_ENUMS = {
  Variable: { text: 'Variable' },
  Module: { text: 'Module' },
  Operator: { text: 'Operator' },
  Function: { text: 'Function' },
  Property: { text: 'Property' },
  Method: { text: 'Method' },
  Reference: { text: 'Reference' }
};
/**
 * document type
 */
export const DOCUMENT_FUNCTION_TYPE = [
  {
    text: '优化参数',
    value: '优化参数'
  },
  {
    text: '建表语句',
    value: '建表语句'
  },
  {
    text: 'CataLog',
    value: 'CataLog'
  },
  {
    text: '设置参数',
    value: '设置参数'
  },
  {
    text: '内置函数',
    value: '内置函数'
  },
  {
    text: 'UDF',
    value: 'UDF'
  },
  {
    text: 'Other',
    value: 'Other'
  }
];

/**
 * document function type enum map
 */
export const DOCUMENT_FUNCTION_ENUMS = {
  优化参数: { text: '优化参数' },
  建表语句: { text: '建表语句' },
  CataLog: { text: 'CataLog' },
  设置参数: { text: '设置参数' },
  内置函数: { text: '内置函数' },
  UDF: { text: 'UDF' },
  Other: { text: 'Other' }
};

export const DOCUMENT_SUBTYPE = [
  {
    text: 'Batch/Streaming',
    value: 'Batch/Streaming'
  },
  {
    text: 'Batch',
    value: 'Batch'
  },
  {
    text: 'Streaming',
    value: 'Streaming'
  },
  {
    text: 'Other',
    value: 'Other'
  },
  {
    text: '比较函数',
    value: '比较函数'
  },
  {
    text: '逻辑函数',
    value: '逻辑函数'
  },
  {
    text: '算术函数',
    value: '算术函数'
  },
  {
    text: '字符串函数',
    value: '字符串函数'
  },
  {
    text: '时间函数',
    value: '时间函数'
  },
  {
    text: '类型转换函数功能',
    value: '类型转换函数功能'
  },
  {
    text: '条件函数',
    value: '条件函数'
  },
  {
    text: 'Collection 函数',
    value: 'Collection 函数'
  },
  {
    text: 'Value Construction函数',
    value: 'Value Construction函数'
  },
  {
    text: 'Value Access函数',
    value: 'Value Access函数'
  },
  {
    text: '分组函数',
    value: '分组函数'
  },
  {
    text: 'hash函数',
    value: 'hash函数'
  },
  {
    text: '聚合函数',
    value: '聚合函数'
  },
  {
    text: '列函数',
    value: '列函数'
  },
  {
    text: '表值聚合函数',
    value: '表值聚合函数'
  },
  {
    text: '其他函数',
    value: '其他函数'
  }
];

export const DOCUMENT_SUBTYPE_ENUMS = {
  'Batch/Streaming': { text: 'Batch/Streaming' },
  Batch: { text: 'Batch' },
  Streaming: { text: 'Streaming' },
  Other: { text: 'Other' },
  比较函数: { text: '比较函数' },
  逻辑函数: { text: '逻辑函数' },
  算术函数: { text: '算术函数' },
  字符串函数: { text: '字符串函数' },
  时间函数: { text: '时间函数' },
  条件函数: { text: '条件函数' },
  类型转换函数功能: { text: '类型转换函数功能' },
  'Collection 函数': { text: 'Collection 函数' },
  'Value Construction函数': { text: 'Value Construction函数' },
  'Value Access函数': { text: 'Value Access函数' },
  分组函数: { text: '分组函数' },
  hash函数: { text: 'hash函数' },
  聚合函数: { text: '聚合函数' },
  列函数: { text: '列函数' },
  表值聚合函数: { text: '表值聚合函数' },
  其他函数: { text: '其他函数' }
};

/**
 * versions  select options
 */
export const VERSIONS = [
  {
    text: 'Flink-1.13',
    value: '1.13'
  },
  {
    text: 'Flink-1.14',
    value: '1.14'
  },
  {
    text: 'Flink-1.15',
    value: '1.15'
  },
  {
    text: 'Flink-1.16',
    value: '1.16'
  },
  {
    text: 'Flink-1.17',
    value: '1.17'
  },
  {
    text: 'All Versions',
    value: 'All Versions'
  }
];

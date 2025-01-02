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

export const DOCUMENT_CATEGORY_ENUMS = {
  Variable: { text: 'Variable', value: 'Variable' },
  Module: { text: 'Module', value: 'Module' },
  Operator: { text: 'Operator', value: 'Operator' },
  Function: { text: 'Function', value: 'Function' },
  Property: { text: 'Property', value: 'Property' },
  Method: { text: 'Method', value: 'Method' },
  Reference: { text: 'Reference', value: 'Reference' }
};

/**
 * document function type enum map
 */
export const DOCUMENT_TYPE_ENUMS = {
  SQL_TEMPLATE: { text: l('rc.doc.type.codeSnippetOrTemplate'), value: 'SQL_TEMPLATE' },
  FLINK_OPTIONS: { text: l('rc.doc.type.flinkParam'), value: 'FLINK_OPTIONS' },
  FUN_UDF: { text: l('rc.doc.type.functionOrUDF'), value: 'FUN_UDF' },
  OTHER: { text: l('rc.doc.type.other'), value: 'OTHER' }
};

export const DOCUMENT_FUNCTION_TYPE_ENUMS = {
  COMPARE_FUNCTION: { text: l('rc.doc.function.type.compare'), value: 'COMPARE_FUNCTION' },
  LOGICAL_FUNCTION: { text: l('rc.doc.function.type.logical'), value: 'LOGICAL_FUNCTION' },
  ARITHMETIC_FUNCTIONS: {
    text: l('rc.doc.function.type.arithmetic'),
    value: 'ARITHMETIC_FUNCTIONS'
  },
  STRING_FUNCTIONS: { text: l('rc.doc.function.type.string'), value: 'STRING_FUNCTIONS' },
  TIME_FUNCTION: { text: l('rc.doc.function.type.time'), value: 'TIME_FUNCTION' },
  CONDITIONAL_FUNCTION: {
    text: l('rc.doc.function.type.conditional'),
    value: 'CONDITIONAL_FUNCTION'
  },
  TYPE_CONVER_FUNCTION: {
    text: l('rc.doc.function.type.typeConver'),
    value: 'TYPE_CONVER_FUNCTION'
  },
  COLLECTION_FUNCTION: { text: l('rc.doc.function.type.collection'), value: 'COLLECTION_FUNCTION' },
  VALUE_CONSTRUCTION_FUNCTION: {
    text: l('rc.doc.function.type.valueConstruction'),
    value: 'VALUE_CONSTRUCTION_FUNCTION Construction函数'
  },
  VALUE_ACCESS_FUNCTION: {
    text: l('rc.doc.function.type.valueAccess'),
    value: 'VALUE_ACCESS_FUNCTION'
  },
  GROUP_FUNCTION: { text: l('rc.doc.function.type.group'), value: 'GROUP_FUNCTION' },
  HASH_FUNCTION: { text: l('rc.doc.function.type.hash'), value: 'HASH_FUNCTION' },
  AGGREGATE_FUNCTION: { text: l('rc.doc.function.type.aggregate'), value: 'AGGREGATE_FUNCTION' },
  COLUMN_FUNCTION: { text: l('rc.doc.function.type.column'), value: 'COLUMN_FUNCTION' },
  TABLE_AGGREGATE_FUNCTION: {
    text: l('rc.doc.function.type.tableAggregate'),
    value: 'TABLE_AGGREGATE_FUNCTION'
  },
  OTHER_FUNCTION: { text: l('rc.doc.function.type.other'), value: 'OTHER_FUNCTION' }
};

/**
 * versions  select options
 */
export const VERSIONS = [
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
    text: 'Flink-1.18',
    value: '1.18'
  },
  {
    text: 'Flink-1.19',
    value: '1.19'
  },
  {
    text: 'Flink-1.20',
    value: '1.20'
  },
  {
    text: 'All Versions',
    value: 'All Versions'
  }
];

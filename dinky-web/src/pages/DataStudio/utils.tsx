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

import { DIALECT } from '@/services/constants';

/**
 * 断言 断言类型值是否在断言类型值列表中 | assert whether the assertion type value is in the assertion type value list
 * @param needAssertTypeValue
 * @param assertTypeValueList
 * @param needAssertTypeValueLowerCase
 * @param assertType
 */
export const assert = (
  needAssertTypeValue: string = '',
  assertTypeValueList: string[] | string = [],
  needAssertTypeValueLowerCase = false,
  assertType: 'notIncludes' | 'includes' | 'notEqual' | 'equal' = 'includes'
): boolean => {
  // 如果 needAssertTypeValue 为空, 则直接返回 false 不需要断言 | if needAssertTypeValue is empty, return false directly
  if (isEmpty(needAssertTypeValue)) {
    return false;
  }
  // 判断 assertTypeValueList 是字符串还是数组 | judge whether assertTypeValueList is a string or an array
  if (!Array.isArray(assertTypeValueList)) {
    assertTypeValueList = [assertTypeValueList];
  }
  // 如果是 assertType 是 notEqual 或 equal, 则 assertTypeValueList 只能有一个值 | if assertType is notEqual or equal, assertTypeValueList can only have one value
  if (assertType === 'notEqual' || assertType === 'equal') {
    assertTypeValueList = assertTypeValueList.slice(0, 1);
  }
  // 判断需要断言的值是否需要转小写 | determine whether the value to be asserted needs to be converted to lowercase
  if (needAssertTypeValueLowerCase) {
    needAssertTypeValue = needAssertTypeValue.toLowerCase();
    assertTypeValueList = assertTypeValueList.map((item) => item.toLowerCase());
  }
  if (assertType === 'notIncludes') {
    return !assertTypeValueList.includes(needAssertTypeValue);
  }
  if (assertType === 'includes') {
    return assertTypeValueList.includes(needAssertTypeValue);
  }
  if (assertType === 'notEqual') {
    return assertTypeValueList.every((item) => item !== needAssertTypeValue);
  }
  if (assertType === 'equal') {
    return assertTypeValueList.every((item) => item === needAssertTypeValue);
  }
  return false;
};

/**
 * @description: 判断是否为 SQL 方言 | assert is sql dialect
 * @param dialect
 * @param includedFlinkSQL
 */
export const isSql = (dialect: string = '', includedFlinkSQL: boolean = false) => {
  if (!dialect || dialect === '') {
    return false;
  }
  switch (dialect.toLowerCase()) {
    case DIALECT.SQL:
    case DIALECT.MYSQL:
    case DIALECT.ORACLE:
    case DIALECT.SQLSERVER:
    case DIALECT.POSTGRESQL:
    case DIALECT.CLICKHOUSE:
    case DIALECT.PHOENIX:
    case DIALECT.DORIS:
    case DIALECT.HIVE:
    case DIALECT.STARROCKS:
    case DIALECT.PRESTO:
      return true;
    case DIALECT.FLINK_SQL:
      return includedFlinkSQL;
    default:
      return false;
  }
};

/**
 * 判断 不为空或者不为 undefined | determine whether it is not empty or not undefined
 * @param value
 */
export const isNotEmpty = (value: any): boolean => {
  return value !== '' && value !== undefined && value !== null;
};

/**
 * 判断为空或者为 undefined | determine whether it is empty or undefined
 * @param value
 */
export const isEmpty = (value: any): boolean => {
  return !isNotEmpty(value);
};

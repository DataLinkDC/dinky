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

export const CODE_TYPE = {
  JAVA: 'Java',
  PYTHON: 'Python',
  SCALA: 'Scala'
};

/**
 * code type filter
 */
export const CODE_TYPE_FILTER = [
  {
    value: CODE_TYPE.JAVA,
    text: CODE_TYPE.JAVA
  },
  {
    value: CODE_TYPE.PYTHON,
    text: CODE_TYPE.PYTHON
  },
  {
    value: CODE_TYPE.SCALA,
    text: CODE_TYPE.SCALA
  }
];

/**
 * code type enum
 */
export const CODE_TYPE_ENUM = {
  JAVA: {
    text: 'Java'
  },
  PYTHON: {
    text: 'Python'
  },
  SCALA: {
    text: 'Scala'
  }
};

/**
 * function type
 */
export const FUNCTION_TYPE = {
  UDF: 'UDF',
  UDAF: 'UDAF',
  UDTF: 'UDTF'
};
/**
 * function type enum
 */
export const FUNCTION_TYPE_ENUM = {
  UDF: {
    text: 'UDF'
  },
  UDAF: {
    text: 'UDAF'
  },
  UDTF: {
    text: 'UDTF'
  }
};
/**
 * function type filter
 */
export const FUNCTION_TYPE_FILTER = [
  {
    value: FUNCTION_TYPE.UDF,
    text: FUNCTION_TYPE.UDF
  },
  {
    value: FUNCTION_TYPE.UDAF,
    text: FUNCTION_TYPE.UDAF
  },
  {
    value: FUNCTION_TYPE.UDTF,
    text: FUNCTION_TYPE.UDTF
  }
];

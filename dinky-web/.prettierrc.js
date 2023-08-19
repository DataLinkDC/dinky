/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

module.exports = {
  singleQuote: true, //  使用单引号
  semi: true, //  使用分号
  trailingComma: 'all', //  尾随逗号
  printWidth: 100, // 该选项指定一行最多允许的字符数。当代码中的一行超过该限制时，Prettier会自动将代码进行换行。建议设置为80到100之间的数字。
  proseWrap: 'never', // 该选项指定是否对markdown文件换行，默认为never，即不换行。
  endOfLine: 'lf',
  tabWidth: 2, // 该选项指定使用几个空格代替一个Tab键。一般建议为2或4个空格。
  jsxSingleQuote: true, // 使用单引号
  // 每个文件格式化的范围是文件的全部内容
  rangeStart: 0,
  rangeEnd: Infinity,
  overrides: [
    {
      files: '.prettierrc',
      options: {
        parser: 'json',
      },
    },
    {
      files: 'document.ejs',
      options: {
        parser: 'html',
      },
    },
    {
      files: '*.tsx',
      options: {
        trailingComma: 'none',
      },
    },
    {
      files: '*.ts',
      options: {
        trailingComma: 'none',
      },
    },
  ],
};

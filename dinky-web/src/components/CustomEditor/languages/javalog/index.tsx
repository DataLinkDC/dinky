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

export function LogLanguage(monaco: any) {
  // Register a new language
  monaco?.languages.register({
    id: 'javalog',
    extensions: ['.log'],
    aliases: ['javalog', 'Javalog', 'Javalog', 'jl', 'log']
  });
  monaco?.languages.setMonarchTokensProvider('javalog', {
    tokenizer: {
      root: [
        // 默认不区分大小写 //
        [/\[(\w*-\d*)+\]/, 'custom-thread'],
        [/(\w+(\.))+(\w+)(\(\d+\))?(:){1}/, 'custom-class'],
        [/(\w+(\.))+(\w+)(\(\d+\))?\s+/, 'custom-class'],
        [/error/, 'custom-error'],
        [/warring/, 'custom-warning'],
        [/warn/, 'custom-warning'],
        [/info/, 'custom-info'],
        [
          /^[1-9]\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])\s+(20|21|22|23|[0-1]\d):[0-5]\d:[0-5]\d\.\d{3}/,
          'custom-date'
        ],
        [
          /[1-9]\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])\s+(20|21|22|23|[0-1]\d):[0-5]\d:[0-5]\d\s(CST)/,
          'custom-date'
        ]
      ]
    },
    ignoreCase: true,
    unicode: true
  });
}

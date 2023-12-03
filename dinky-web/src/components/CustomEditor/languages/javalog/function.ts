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

import { CustomEditorLanguage } from '@/components/CustomEditor/languages/constants';
import { JAVA_LOG_KEYWORD } from '@/components/CustomEditor/languages/javalog/keyword';
import { Monaco } from '@monaco-editor/react';

export function buildMonarchTokensProvider(monacoLanguages?: Monaco['languages'] | undefined) {
  monacoLanguages?.setMonarchTokensProvider(CustomEditorLanguage.JavaLog, {
    defaultToken: '',
    tokenPostfix: '.log',
    keywords: JAVA_LOG_KEYWORD,
    operators: [],
    ignoreCase: true, // 忽略大小写
    builtinFunctions: [],
    builtinVariables: [],
    typeKeywords: [],
    scopeKeywords: [],
    pseudoColumns: [],
    brackets: [
      { open: '{', close: '}', token: 'delimiter.curly' },
      { open: '[', close: ']', token: 'delimiter.bracket' },
      { open: '(', close: ')', token: 'delimiter.parenthesis' },
      { open: '<', close: '>', token: 'delimiter.angle' }
    ],
    autoClosingPairs: [
      { open: '{', close: '}' },
      { open: '[', close: ']' },
      { open: '(', close: ')' },
      { open: '"', close: '"' },
      { open: "'", close: "'" },
      { open: '`', close: '`' },
      { open: '<', close: '>' }
    ],
    surroundingPairs: [
      { open: '{', close: '}' },
      { open: '[', close: ']' },
      { open: '(', close: ')' },
      { open: '"', close: '"' },
      { open: "'", close: "'" },
      { open: '`', close: '`' },
      { open: '<', close: '>' }
    ],
    folding: {
      markers: {
        start:
          /((CREATE|ALERT|DROP|USE\s+)?(TABLE|DATABASE|STREAM|FUNCTION|CATALOG|SCHEMA|VIEW)\b)|((EXECUTE\s+)?(JAR|CDCSOURCE)?\b)/i,
        end: /\)\\;\b/i
      }
    },
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
    unicode: true
  });
}

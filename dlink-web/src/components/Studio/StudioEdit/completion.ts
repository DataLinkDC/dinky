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


import * as monaco from "monaco-editor";

const Completion =[
  /**   * 内置函数   */
  {
    label: 'SUM(number)',
    kind: monaco.languages.CompletionItemKind.Function,
    insertText: 'SUM(${1:})',
    insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
    detail: '返回指定参数的求和'
  },
  {
    label: 'SQRT(number)',
    kind: monaco.languages.CompletionItemKind.Function,
    insertText: 'SQRT(${1:})',
    insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
    detail: '返回指定参数的平方根'
  },
  {
    label: 'SIN(number)',
    kind: monaco.languages.CompletionItemKind.Function,
    insertText: 'SIN(${1:})',
    insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
    detail: '返回指定参数的正弦值'
  },
  {
    label: 'SINH(number)',
    kind: monaco.languages.CompletionItemKind.Function,
    insertText: 'SINH(${1:})',
    insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
    detail: '返回指定参数的双曲正弦值'
  },
  {
    label: 'SIGN(number)',
    kind: monaco.languages.CompletionItemKind.Function,
    insertText: 'SIGN(${1:})',
    insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
    detail: '返回指定参数的符合'
  },
  {
    label: 'SUBSTRING(string,integer1,integer2)',
    kind: monaco.languages.CompletionItemKind.Function,
    insertText: 'SUBSTRING(${1:})',
    insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
    detail: '返回指定字符串的子字符串'
  },
];

export default Completion;

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

import { FlinkSQLLanguage } from '@/components/CustomEditor/languages/flinksql';
import { LogLanguage } from '@/components/CustomEditor/languages/javalog';
import { Monaco } from '@monaco-editor/react';

/**
 * 加载自定义语言 关键字
 * @param monaco
 * @constructor
 */
//  function LoadLanguagesKeyWord(monaco: Monaco | null) {
//      if (!monaco) {
//          return;
//      }
//      monaco?.languages?.getLanguages().forEach((language) => {
//          return monaco?.languages?.registerCompletionItemProvider(language.id , {
//              provideCompletionItems: function (model, position) {
//                  const word = model.getWordUntilPosition(position);
//                  const range = {
//                      startLineNumber: position.lineNumber,
//                      endLineNumber: position.lineNumber,
//                      startColumn: word.startColumn,
//                      endColumn: word.endColumn
//                  };
//                  return {
//                      suggestions: FLINK_SQL_KEYWORD.map((item) => {
//                          return {
//                              label: item,
//                              range: range,
//                              kind: monaco.languages.CompletionItemKind.Keyword,
//                              insertText: item
//                          };
//                      })
//                  };
//              }
//          });
//      })
// }

export function LoadCustomEditorLanguage(monaco: Monaco | null) {
  // LoadLanguagesKeyWord(monaco);
  LogLanguage(monaco);
  FlinkSQLLanguage(monaco);
}

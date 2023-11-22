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
import {CustomEditorLanguage} from "@/components/CustomEditor/languages/constants";

/**
 * 避免重复加载语言, 通过获取到 language 的 id 来判断是否已经加载过
 * @param monaco
 * @param language
 */
function canLoadLanguage(monaco: Monaco | undefined, language: string) {
  return !monaco?.languages?.getEncodedLanguageId(language);
}
export function LoadCustomEditorLanguage(monaco?: Monaco | undefined , registerCompletion: boolean = false) {
  if (canLoadLanguage(monaco, CustomEditorLanguage.FlinkSQL)) {
    FlinkSQLLanguage(monaco, registerCompletion);
  }

  if (canLoadLanguage(monaco, CustomEditorLanguage.JavaLog)) {
    LogLanguage(monaco);
  }
}


export function LoadCustomEditorLanguageWithCompletion(monaco?: Monaco | undefined) {
  LoadCustomEditorLanguage(monaco, true);
  console.log('LoadCustomEditorLanguageWithCompletion', monaco?.languages?.getLanguages());
}

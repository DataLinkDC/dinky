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

import { LoadCustomEditorLanguage } from '@/components/CustomEditor/languages';
import { convertCodeEditTheme } from '@/utils/function';
import { Monaco } from '@monaco-editor/react';

/**
 * 初始化编辑器 | init editor
 *  - 加载自定义语言 | load custom language
 *  - 转换主题 | convert theme
 * @param monaco
 * @param registerCompletion
 */
export function handleInitEditorAndLanguageOnBeforeMount(
  monaco: Monaco,
  registerCompletion: boolean = false
) {
  convertCodeEditTheme(monaco.editor);
  LoadCustomEditorLanguage(monaco.languages, monaco.editor, registerCompletion);
}

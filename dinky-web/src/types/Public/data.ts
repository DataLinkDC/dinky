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

export const THEME = {
  NAV_THEME: 'navTheme',
  dark: 'realDark',
  light: 'light'
};

/**
 * MonacoEditor of CodeEdit's theme
 */
export const CODE_EDIT_THEME = {
  LIGHT: 'light',
  DARK: 'vs-dark'
};

export const MonacoEditorOptions = {
  // selectOnLineNumbers: true,
  renderSideBySide: false, //  side by side
  autoIndent: 'None', //  auto indent
  fontSize: 14, //  font size
  automaticLayout: true, //  auto layout
  scrollBeyondLastLine: false, //is scroll beyond the last line
  autoDetectHighContrast: true // auto detect high contrast
};

export type BaseBeanColumns = ExcludeNameAndEnableColumns & {
  name: string;
  enabled: boolean;
};

export type ExcludeNameAndEnableColumns = {
  id: number;
  createTime: Date;
  updateTime: Date;
};

export type SuggestionLabel = {
  label: string;
  detail?: string;
  description?: string;
};

export type SuggestionInfo = {
  key: string | number;
  label: SuggestionLabel;
  kind: number;
  insertText: string;
  detail?: string;
};

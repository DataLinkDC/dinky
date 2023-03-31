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

import {TENANT_ID} from "@/services/constants";
import cookies from "js-cookie";
import {THEME,CODE_EDIT_THEME} from "@/types/Public/data";
import {editor} from "monaco-editor";

/**
 * PUT tenantId TO localStorage & cookies
 * @param tenantId
 */
export function setTenantStorageAndCookie(tenantId: number) {
  // save as localStorage
  localStorage.setItem(TENANT_ID, tenantId.toString());
  // save as cookies
  cookies.set(TENANT_ID, tenantId.toString(), {path: "/"});
}

/**
 * get tenant id
 * @param tenantId
 */
export function getTenantByLocalStorage() {
  return localStorage.getItem(TENANT_ID);
}

/**
 * parseJsonStr
 * @param jsonStr
 */
export function parseJsonStr(jsonStr: string) {
  return JSON.parse(JSON.stringify(jsonStr));
}

/**
 * get theme by localStorage's theme
 */
export function getLocalTheme () {
  return localStorage.getItem(THEME.NAV_THEME);
}

/**
 * get code edit theme by localStorage's theme
 * @constructor
 */
export function ConvertCodeEditTheme () {

  /**
   * user can define a new theme by calling the defineTheme method on the editor.
   */
  editor.defineTheme(CODE_EDIT_THEME.VS_CUSTOME, {
    base: 'vs', // 指定基础主题 , 可选值: 'vs', 'vs-dark', 'hc-black' , base theme
    inherit: true, // 是否继承基础主题配置 , 默认为 true, is to inherit the base theme
    // rules is an array of rules. The array must not be sparse (i.e. do not use holes).
    rules: [
      { token: 'comment', foreground: '#008800', fontStyle: 'italic' },
      { token: 'keyword', foreground: '#f30282', fontStyle: 'bold' },
      { token: 'string', foreground: '#0b14cc' },
      { token: 'delimiter', foreground: '#ffffff' }
    ],
    // colors is an object of color identifiers and their color values.
    colors: {
      'editor.background': '#141414', //  editor background color
      'editor.lineHighlightBackground': '#0000ff20', //  editor line highlight background color
      'editorLineNumber.foreground': '#ffffff', //   editor line number color
      'editorCursor.foreground': '#ffffff', //  editor cursor color
      'editorIndentGuide.background': '#ffffff', //  editor indent guide color
      'editor.foreground': '#ffffff', //  editor selection highlight border color
    }
  });


  const theme = getLocalTheme();
  switch (theme) {
    case THEME.dark:
      return CODE_EDIT_THEME.VS_CUSTOME;
    case THEME.light:
      return CODE_EDIT_THEME.DARK;
    default:
      return CODE_EDIT_THEME.HC_BLACK;
  }
}

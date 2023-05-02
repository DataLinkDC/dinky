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
import {THEME, CODE_EDIT_THEME} from "@/types/Public/data";
import {editor} from "monaco-editor";
import {useEffect, useState} from "react";

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
export function getLocalTheme() {
  return localStorage.getItem(THEME.NAV_THEME);
}

/**
 * get code edit theme by localStorage's theme
 * @constructor
 */
export function convertCodeEditTheme() {

  /**
   * user can define a new theme by calling the defineTheme method on the editor.
   */
  editor.defineTheme(CODE_EDIT_THEME.VS_CUSTOME, {
    base: "vs", // 指定基础主题 , 可选值: 'vs', 'vs-dark', 'hc-black' , base theme
    inherit: true, // 是否继承基础主题配置 , 默认为 true, is to inherit the base theme
    // rules is an array of rules. The array must not be sparse (i.e. do not use holes).
    rules: [
      {token: "comment", foreground: "#008800", fontStyle: "italic"},
      {token: "keyword", foreground: "#064cff", fontStyle: "bold"},
      {token: "string", foreground: "#507dee"},
      {token: "delimiter", foreground: "#c04b4b"},
      {token: "readonly", foreground: "#fa0707", background: "#141414", fontStyle: "italic"},
      {token: "number", foreground: "#ffffff"},

    ],
    // colors is an object of color identifiers and their color values.
    colors: {
      "editor.background": "#141414", //  editor background color
      "editor.lineHighlightBackground": "#141414", //  editor line highlight background color
      "editorLineNumber.foreground": "#ffffff", //   editor line number color
      "editorCursor.foreground": "#ffffff", //  editor cursor color
      "editorIndentGuide.background": "#ffffff", //  editor indent guide color
      "editor.foreground": "#ffffff", //  editor selection highlight border color
      "editor.selectionBackground": "#4ba1ef", //  editor selection highlight color
      "editor.selectionHighlightBorder": "#4ba1ef", //  editor selection highlight border color
      "editor.findMatchBackground": "#4ba1ef", //  editor find match highlight color
      "editor.wordHighlightBackground": "#8bb2d2", //  editor word highlight color
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
};


/**
 * use SSE build single data
 * @param url
 */
export const useSSEBuildSingleData = (url: string) => {
  const [data, setData] = useState<any>(null);

  useEffect(() => {
    const eventSource = new EventSource(url);
    eventSource.onmessage = (event) => {
      const newData = JSON.parse(event.data);
      setData(newData);
    };
    return () => {
      eventSource.close();
    };
  }, [url]);

  return data;
};

/**
 * use SSE build array data
 * @param url
 */
export const useSSEBuildArrayData = (url: string) => {
  const [data, setData] = useState<any[]>([]);

  useEffect(() => {
    const eventSource = new EventSource(url);
    eventSource.onmessage = (event) => {
      const newData = JSON.parse(event.data);
      setData((prevData) => [...prevData, newData]);
    };
    return () => {
      eventSource.close();
    };
  }, [url]);

  return data;
};

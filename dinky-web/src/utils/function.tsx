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

import {DIALECT, TENANT_ID} from "@/services/constants";
import cookies from "js-cookie";
import {THEME, CODE_EDIT_THEME} from "@/types/Public/data";
import {editor} from "monaco-editor";
import React, {useEffect, useState} from "react";
import {trim} from "lodash";
import {
  FileIcon,
  FolderSvgExpand,
  JavaSvg,
  LogSvg,
  MarkDownSvg,
  ScalaSvg,
  ShellSvg,
  XMLSvg,
  YAMLSvg
} from "@/components/Icons/CodeLanguageIcon";
import path from "path";

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
      {token: "delimiter", foreground: "#041d81"},
      {token: "readonly", foreground: "#e73a6e", background: "#141414", fontStyle: "italic"},
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


/**
 * get file icon by file type
 * @param type
 */
export const getLanguage = (type: string): string => {
  switch (type) {
    case DIALECT.JAVA:
    case DIALECT.LOG:
      return DIALECT.JAVA;
    case DIALECT.MD:
    case DIALECT.MDX:
      return DIALECT.MARKDOWN;
    case DIALECT.XML:
      return DIALECT.XML;
    case DIALECT.YAML:
    case DIALECT.YML:
      return DIALECT.YAML;
    case DIALECT.JSON:
      return DIALECT.JSON;
    case DIALECT.SH:
    case DIALECT.BASH:
    case DIALECT.CMD:
      return DIALECT.SHELL;
    case DIALECT.SCALA:
      return DIALECT.SCALA;
    case DIALECT.PYTHON:
      return DIALECT.PYTHON;
    case DIALECT.SQL:
      return DIALECT.SQL;
    default:
      return DIALECT.JAVASCRIPT;
  }
};

/**
 * get the icon according to the file suffix
 * @param type file type
 */
export const getIcon = (type: string) => {
  switch (type) {
    case DIALECT.JAVA:
      return <JavaSvg/>;
    case DIALECT.SCALA:
      return <ScalaSvg/>;
    case DIALECT.MD:
    case DIALECT.MDX:
      return <MarkDownSvg/>;
    case DIALECT.XML:
      return <XMLSvg/>;
    case DIALECT.YAML:
    case DIALECT.YML:
      return <YAMLSvg/>;
    case DIALECT.SH:
    case DIALECT.BASH:
    case DIALECT.CMD:
      return <ShellSvg/>;
    case DIALECT.LOG:
      return <LogSvg/>;
    default:
      return <FileIcon/>;
  }
};


/**
 * Get the icon according to the file suffix
 * @param type file suffix
 * @param splitChar split character
 * @param isLeft is left
 */
export const renderIcon = (type: string, splitChar: string, isLeft: boolean) => {
  if (isLeft) {
    return <FolderSvgExpand/>;
  } else {
    if (trim(splitChar).length === 0) {
      return getIcon(type);
    }else {
      let suffixOfType = type.toString().split(splitChar).reverse()[0];
      return getIcon(suffixOfType);
    }
  }
};


/**
 * Get the language according to the file suffix
 * @param type file suffix
 * @param splitChar split character
 */
export const renderLanguage = (type: string, splitChar: string) => {
  if (trim(splitChar).length === 0) {
    getLanguage(type);
  } else {
    let suffixOfType = type.toString().split(splitChar).reverse()[0];
    return getLanguage(suffixOfType);
  }
};


/**
 * get the folder separator according to the platform
 */
export const folderSeparator = () => {
  return path.sep;
}




/**
 * build tree data
 * @param data
 * @returns {any}
 */
export const buildTreeData = (data: any): any => data?.map((item: any) => {

  // build key
  let buildKey = item.path + folderSeparator() + item.name;

  // if has children , recursive build
  if (item.children) {
    return {
      isLeaf: !item.leaf,
      name: item.name,
      parentId: item.path,
      icon: renderIcon(item.name, ".", item.leaf),
      content: item.content,
      path: item.path,
      title: item.name,
      key: buildKey,
      children: buildTreeData(item.children)
    };
  }
  return {
    isLeaf: !item.leaf,
    name: item.name,
    parentId: item.path,
    icon: renderIcon(item.name, ".", item.leaf),
    content: item.content,
    path: item.path,
    title: item.name,
    key: buildKey,
  };
});

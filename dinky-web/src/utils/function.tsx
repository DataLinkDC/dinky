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

import {
  ConfigSvg,
  FileIcon,
  FlinkSQLSvg,
  FolderSvgExpand,
  JarSvg,
  JavaSvg,
  LogSvg,
  MarkDownSvg,
  PythonSvg,
  ScalaSvg,
  ShellSvg,
  XMLSvg,
  YAMLSvg,
  ZipSvg
} from '@/components/Icons/CodeLanguageIcon';
import {
  DATETIME_FORMAT,
  DIALECT,
  LANGUAGE_KEY,
  LANGUAGE_ZH,
  TENANT_ID
} from '@/services/constants';
import { CODE_EDIT_THEME, THEME } from '@/types/Public/data';
import { l } from '@/utils/intl';
import { Monaco } from '@monaco-editor/react';
import dayjs from 'dayjs';
import cookies from 'js-cookie';
import { trim } from 'lodash';
import { editor, KeyCode, KeyMod } from 'monaco-editor';
import path from 'path';
import { format } from 'sql-formatter';

/**
 * get language by localStorage's umi_locale , if not exist , return zh-CN
 */
export function getLocalStorageLanguage() {
  return localStorage.getItem(LANGUAGE_KEY) ?? LANGUAGE_ZH;
}

/**
 * set key value to localStorage
 * @param key
 * @param value
 */
export function setKeyToLocalStorage(key: string, value: string) {
  localStorage.setItem(key, value);
}

/**
 * get value by localStorage's key
 * @param key
 */
export function getValueFromLocalStorage(key: string) {
  return localStorage.getItem(key) ?? '';
}

/**
 * get tenant id
 */
export function getTenantByLocalStorage() {
  return getValueFromLocalStorage(TENANT_ID);
}

/**
 * get cookie by key
 * @param key
 */
export function getCookieByKey(key: string) {
  return cookies.get(key) ?? '';
}

/**
 * set cookie by key
 * @param key
 * @param value
 * @param options
 */
export function setCookieByKey(key: string, value: string, options?: {}) {
  cookies.set(key, value, options);
}

/**
 * PUT tenantId TO localStorage & cookies
 * @param tenantId
 */
export function setTenantStorageAndCookie(tenantId: number) {
  // save as localStorage
  setKeyToLocalStorage(TENANT_ID, tenantId.toString());
  // save as cookies
  setCookieByKey(TENANT_ID, tenantId.toString(), { path: '/' });
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
export function getLocalTheme(): string {
  return localStorage.getItem(THEME.NAV_THEME) ?? THEME.light;
}

export function setLocalThemeToStorage(defaultTheme?: string) {
  localStorage.setItem(THEME.NAV_THEME, defaultTheme ?? getLocalTheme());
}

/**
 * register editor key binding | 注册编辑器快捷键
 *
 * @param editorInstance
 */
function registerEditorKeyBinding(editorInstance?: editor.IStandaloneCodeEditor) {
  // 添加 ctrl + z 撤销
  editorInstance?.addCommand(KeyMod.CtrlCmd | KeyCode.KeyZ, () => {
    editorInstance?.trigger('anyString', 'undo', '');
  });
  // 添加 ctrl + y 恢复
  editorInstance?.addCommand(KeyMod.CtrlCmd | KeyCode.KeyY, () => {
    editorInstance?.trigger('anyString', 'redo', '');
  });
  // 格式化所有代码
  editorInstance?.addCommand(KeyMod.Alt | KeyCode.Digit3, () => {
    editorInstance?.trigger('anyString', 'editor.action.formatDocument', '');
    editorInstance?.setValue(format(editorInstance?.getValue()));
  });
  // 格式化选定内容
  editorInstance?.addCommand(KeyMod.Alt | KeyCode.Digit4, () => {
    editorInstance?.trigger('anyString', 'editor.action.formatSelection', '');
    editorInstance?.setValue(format(editorInstance?.getValue()));
  });
}

/**
 * <p> 如果使用了 sql-formatter , 可以使用以下参数 </p> <br/>
 * language: the SQL dialect to use (when using format()). | 要使用的SQL方言（当使用format（）时） <br/>
 * dialect: the SQL dialect to use (when using formatDialect() since version 12). | 要使用的SQL方言（自版本12起）<br/>
 * tabWidth: amount of indentation to use. | 要使用的缩进量<br/>
 * useTabs: to use tabs for indentation. | 要使用制表符进行缩进<br/>
 * keywordCase: uppercases or lowercases keywords. | 关键字大小写<br/>
 * identifierCase: uppercases or lowercases identifiers. (experimental!) | 标识符大小写<br/>
 * indentStyle: defines overall indentation style.| 总体缩进样式<br/>
 * logicalOperatorNewline: newline before or after boolean operator (AND, OR, XOR). | 布尔运算符（AND，OR，XOR）的换行位置<br/>
 * expressionWidth: maximum number of characters in parenthesized expressions to be kept on single line. | 带括号的表达式中保持在一行的最大字符数<br/>
 * linesBetweenQueries: how many newlines to insert between queries. | 在查询之间插入的换行数<br/>
 * denseOperators: packs operators densely without spaces. | 密集地封装运算符，没有空格<br/>
 * newlineBeforeSemicolon: places semicolon on separate line. | 将分号放在单独的行上 <br/>
 * params: collection of values for placeholder replacement. | 占位符替换的值的集合<br/>
 * paramTypes: specifies parameter placeholders types to support | 指定要支持的参数占位符类型 <br/>
 * register editor action
 * @param editorInstance editor instance
 */
function registerEditorAction(editorInstance?: editor.IStandaloneCodeEditor) {
  // 格式化所有代码 添加到 右键菜单 | format document
  editorInstance?.addAction({
    id: 'format',
    label: l('shortcut.key.format'),
    keybindings: [KeyMod.CtrlCmd | KeyCode.Digit3],
    contextMenuGroupId: 'custom',
    contextMenuOrder: 1.5,
    run: () => {
      editorInstance?.trigger('anyString', 'editor.action.formatDocument', '');
      editorInstance?.setValue(format(editorInstance?.getValue(), { language: 'spark' }));
    }
  });
  // 格式化选定内容 添加到 右键菜单 | format selection
  editorInstance?.addAction({
    id: 'formatSelection',
    label: l('shortcut.key.formatSelection'),
    keybindings: [KeyMod.CtrlCmd | KeyCode.Digit4],
    contextMenuGroupId: 'custom',
    contextMenuOrder: 1.5,
    run: () => {
      editorInstance?.trigger('anyString', 'editor.action.formatSelection', '');
      editorInstance?.setValue(format(editorInstance?.getValue(), { language: 'spark' }));
    }
  });
  // 注释该行 添加到 右键菜单 | comment line
  editorInstance?.addAction({
    id: 'commentLine',
    label: l('shortcut.key.notes'),
    keybindings: [KeyMod.CtrlCmd | KeyCode.Slash],
    contextMenuGroupId: 'custom',
    contextMenuOrder: 1.5,
    run: () => {
      editorInstance?.trigger('anyString', 'editor.action.commentLine', '');
    }
  });
  // 转为 大写 添加到 右键菜单 | to uppercase
  editorInstance?.addAction({
    id: 'upperCase',
    label: l('shortcut.key.upperCase'),
    keybindings: [KeyMod.CtrlCmd | KeyCode.KeyU],
    contextMenuGroupId: 'custom',
    contextMenuOrder: 1.5,
    run: () => {
      editorInstance?.trigger('anyString', 'editor.action.transformToUppercase', '');
    }
  });
  // 转为 小写 添加到 右键菜单 | to lowercase
  editorInstance?.addAction({
    id: 'lowerCase',
    label: l('shortcut.key.lowerCase'),
    keybindings: [KeyMod.CtrlCmd | KeyCode.KeyL],
    contextMenuGroupId: 'custom',
    contextMenuOrder: 1.5,
    run: () => {
      editorInstance?.trigger('anyString', 'editor.action.transformToLowercase', '');
    }
  });
}

/**
 * register editor key binding and action | 注册编辑器快捷键和右键菜单
 * @param editorInstance
 */
export function registerEditorKeyBindingAndAction(editorInstance?: editor.IStandaloneCodeEditor) {
  registerEditorKeyBinding(editorInstance);
  registerEditorAction(editorInstance);
}

/**
 * get code edit theme by localStorage's theme
 * @constructor
 */
export function convertCodeEditTheme(editorInstance?: Monaco['editor']) {
  if (!editorInstance) {
    editorInstance = editor;
  }
  if (editorInstance === undefined) {
    return CODE_EDIT_THEME.LIGHT;
  } else {
    /**
     * 定义亮色 覆盖vs主题,增加扩展规则
     */
    editorInstance?.defineTheme?.(CODE_EDIT_THEME.LIGHT, {
      base: 'vs', // 指定基础主题 , 可选值: 'vs', 'vs-dark', 'hc-black' , base theme
      inherit: true, // 是否继承主题配置
      rules: [
        // 注意,默认的不做修改 因为上边继承了父主题, 只添加自己定义的 , 否则会覆盖默认的 , 导致编辑器样式不一致
        { token: 'custom-info', foreground: '#808080' },
        { token: 'custom-thread', foreground: '#9fa19f' },
        { token: 'custom-class', foreground: '#1060d9' },
        { token: 'custom-error', foreground: '#ff0000', fontStyle: 'bold' },
        { token: 'custom-warning', foreground: '#FFA500', fontStyle: 'bold' },
        { token: 'custom-date', foreground: '#008800' },
        { token: 'custom-process', foreground: '#07f313' }
      ],
      colors: {},
      encodedTokensColors: []
    });

    /**
     * 定义暗色 覆盖vs-dark主题,增加扩展规则
     */
    editorInstance?.defineTheme?.(CODE_EDIT_THEME.DARK, {
      base: 'vs-dark', // 指定基础主题 , 可选值: 'vs', 'vs-dark', 'hc-black' , base theme
      inherit: true, // 是否继承主题配置
      rules: [
        // 注意,默认的不做修改 因为上边继承了父主题, 只添加自己定义的 , 否则会覆盖默认的 , 导致编辑器样式不一致
        { token: 'custom-info', foreground: '#008800' },
        { token: 'custom-thread', foreground: '#9fa19f' },
        { token: 'custom-class', foreground: '#1060d9' },
        { token: 'custom-error', foreground: '#ff0000', fontStyle: 'bold' },
        { token: 'custom-warning', foreground: '#FFA500', fontStyle: 'bold' },
        { token: 'custom-date', foreground: '#008800' },
        { token: 'custom-process', foreground: '#07f313' }
      ],
      colors: {},
      encodedTokensColors: []
    });
  }

  const theme = getLocalTheme();
  switch (theme) {
    case THEME.dark:
      return CODE_EDIT_THEME.DARK;
    case THEME.light:
      return CODE_EDIT_THEME.LIGHT;
    default:
      return CODE_EDIT_THEME.LIGHT;
  }
}

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
    case DIALECT.PYTHON_LONG:
      return DIALECT.PYTHON_LONG;
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
  if (!type) {
    return <FileIcon />;
  }
  switch (type.toLowerCase()) {
    case DIALECT.JAVA:
      return <JavaSvg />;
    case DIALECT.SCALA:
      return <ScalaSvg />;
    case DIALECT.PYTHON:
    case DIALECT.PYTHON_LONG:
      return <PythonSvg />;
    case DIALECT.MD:
    case DIALECT.MDX:
      return <MarkDownSvg />;
    case DIALECT.XML:
      return <XMLSvg />;
    case DIALECT.YAML:
    case DIALECT.YML:
      return <YAMLSvg />;
    case DIALECT.JAR:
      return <JarSvg />;
    case DIALECT.SH:
    case DIALECT.BASH:
    case DIALECT.CMD:
      return <ShellSvg />;
    case DIALECT.CONF:
      return <ConfigSvg />;
    case DIALECT.LOG:
      return <LogSvg />;
    case DIALECT.ZIP:
    case DIALECT.TAR:
    case DIALECT.TAR_GZ:
      return <ZipSvg />;
    case DIALECT.FLINK_SQL:
      return <FlinkSQLSvg />;
    default:
      return <FileIcon />;
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
    return <FolderSvgExpand />;
  } else {
    if (trim(splitChar).length === 0) {
      return getIcon(type);
    } else {
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
export const renderLanguage = (type = '', splitChar: string) => {
  if (trim(splitChar).length === 0) {
    return getLanguage(type);
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
};

/**
 * Generate time string
 * @param s_time datetime
 */
export const parseSecondStr = (s_time: number) => {
  let second_time = Math.floor(s_time);
  let time = second_time + l('global.time.second');
  if (second_time > 60) {
    let second = second_time % 60;
    let min = Math.floor(second_time / 60);
    time = min + l('global.time.minute') + second + l('global.time.second');
    if (min > 60) {
      min = Math.floor(second_time / 60) % 60;
      let hour = Math.floor(Math.floor(second_time / 60) / 60);
      time =
        hour +
        l('global.time.hour') +
        min +
        l('global.time.minute') +
        second +
        l('global.time.second');
      if (hour > 24) {
        hour = Math.floor(Math.floor(second_time / 60) / 60) % 24;
        let day = Math.floor(Math.floor(Math.floor(second_time / 60) / 60) / 24);
        time =
          day +
          l('global.time.day') +
          hour +
          l('global.time.hour') +
          min +
          l('global.time.minute') +
          second +
          l('global.time.second');
      }
    }
  }
  return time;
};

export function parseByteStr(limit: number) {
  if (limit == null) {
    return 'None';
  }
  let size = '';
  if (limit < 0.1 * 1024) {
    //小于0.1KB，则转化成B
    size = limit.toFixed(2) + 'B';
  } else if (limit < 0.1 * 1024 * 1024) {
    //小于0.1MB，则转化成KB
    size = (limit / 1024).toFixed(2) + 'KB';
  } else if (limit < 0.1 * 1024 * 1024 * 1024) {
    //小于0.1GB，则转化成MB
    size = (limit / (1024 * 1024)).toFixed(2) + 'MB';
  } else {
    //其他转化成GB
    size = (limit / (1024 * 1024 * 1024)).toFixed(2) + 'GB';
  }

  let sizeStr = size + ''; //转成字符串
  let index = sizeStr.indexOf('.'); //获取小数点处的索引
  let dou = sizeStr.substr(index + 1, 2); //获取小数点后两位的值
  if (dou == '00') {
    //判断后两位是否为00，如果是则删除00
    return sizeStr.substring(0, index) + sizeStr.substr(index + 3, 2);
  }
  return size;
}

export function parseNumStr(num: number) {
  let c =
    num.toString().indexOf('.') !== -1
      ? num.toLocaleString()
      : num.toString().replace(/(\d)(?=(\d{3})+$)/g, '$1,');
  return c;
}

/**
 * Generate MilliSecond time string
 * @param {number} second_time
 * @returns {any}
 */
export function parseMilliSecondStr(second_time: number | undefined): string {
  if (second_time == null) {
    return 'None';
  }
  if ((second_time / 1000) % 60 < 1) {
    return second_time + l('global.time.millisecond');
  }
  return parseSecondStr(second_time / 1000);
}

/**
 * Calculate how many days the dates differ
 * @returns {any}
 */
export function differenceDays(startDateString: any, endDateString: any): number {
  const startDate = new Date(startDateString);
  const endDate = new Date(endDateString);

  // 将日期对象的时间部分设为0，只保留日期
  startDate.setHours(0, 0, 0, 0);
  endDate.setHours(0, 0, 0, 0);

  // 计算两个日期的时间差（毫秒）
  const timeDifference = endDate.getTime() - startDate.getTime();

  // 将毫秒转换为天数
  const daysDifference = timeDifference / (1000 * 60 * 60 * 24);

  return Math.floor(daysDifference);
}

/**
 * Determine whether the file is supported
 * @returns {boolean}
 */
export const unSupportView = (name: string) => {
  return (
    name.endsWith('.jar') ||
    name.endsWith('.war') ||
    name.endsWith('.zip') ||
    name.endsWith('.tar.gz') ||
    name.endsWith('.tar') ||
    name.endsWith('.jpg') ||
    name.endsWith('.png') ||
    name.endsWith('.gif') ||
    name.endsWith('.bmp') ||
    name.endsWith('.jpeg') ||
    name.endsWith('.ico')
  );
};

/**
 * search tree node
 * @param originValue
 * @param {string} searchValue
 * @returns {any}
 */
export const searchTreeNode = (originValue: string, searchValue: string): any => {
  let title = <>{originValue}</>;

  // searchValue is not empty and trim() after length > 0
  if (searchValue && searchValue.trim().length > 0) {
    const searchIndex = originValue.indexOf(searchValue); // search index
    const beforeStr = originValue.substring(0, searchIndex); // before search value
    const afterStr = originValue.substring(searchIndex + searchValue.length); // after search value
    // when search index > -1, return render title, else return origin title
    title =
      searchIndex > -1 ? (
        <span>
          {beforeStr}
          <span className={'treeList tree-search-value'}>{searchValue}</span>
          {afterStr}
        </span>
      ) : (
        <span className={'treeList'}>{title}</span>
      );
  }
  return title;
};

export const transformTreeData = <T,>(data: T[]): T[] => {
  return data.map((item: T, index) => {
    return { ...item, key: index };
  });
};

export const transformTableDataToCsv = <T,>(column: string[], data: T[]): string => {
  let row = '';
  let csvData = '';
  for (const title of column) {
    row += '"' + title + '",';
  }
  const delimiter = '\r\n';
  csvData += row + delimiter; // 添加换行符号
  for (const item of data) {
    row = '';
    for (let key in item) {
      row += '"' + (item[key] ?? '') + '",';
    }
    csvData += row + delimiter; // 添加换行符号
  }
  return csvData;
};

export const formatDateToYYYYMMDDHHMMSS = (date: Date) => {
  return dayjs(date).format(DATETIME_FORMAT);
};

export const parseDateStringToDate = (dateString: Date) => {
  return dayjs(dateString).toDate();
};

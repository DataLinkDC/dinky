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

import {DIALECT, LANGUAGE_KEY, LANGUAGE_ZH, TENANT_ID} from '@/services/constants';
import cookies from 'js-cookie';
import {CODE_EDIT_THEME, THEME} from '@/types/Public/data';
import {editor} from 'monaco-editor';
import React, {useEffect, useState} from 'react';
import {trim} from 'lodash';
import {
  FileIcon,
  FolderSvgExpand,
  JavaSvg,
  LogSvg,
  MarkDownSvg,
  PythonSvg,
  ScalaSvg,
  ShellSvg,
  XMLSvg,
  YAMLSvg
} from '@/components/Icons/CodeLanguageIcon';
import path from 'path';
import {l} from '@/utils/intl';


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
    setCookieByKey(TENANT_ID, tenantId.toString(), {path: '/'});
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
export function getLocalTheme() :string {
    return localStorage.getItem(THEME.NAV_THEME) ?? THEME.dark;
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
        base: 'vs', // 指定基础主题 , 可选值: 'vs', 'vs-dark', 'hc-black' , base theme
        inherit: true, // 是否继承基础主题配置 , 默认为 true, is to inherit the base theme
        // rules is an array of rules. The array must not be sparse (i.e. do not use holes).
        rules: [
            {token: 'comment', foreground: '#008800', fontStyle: 'italic'},
            {token: 'keyword', foreground: '#064cff', fontStyle: 'bold'},
            {token: 'string', foreground: '#507dee'},
            {token: 'delimiter', foreground: '#041d81'},
            {token: 'readonly', foreground: '#e73a6e', background: '#141414', fontStyle: 'italic'},
            {token: 'number', foreground: '#ffffff'},

        ],
        // colors is an object of color identifiers and their color values.
        colors: {
            'editor.background': '#5d5b5b', //  editor background color
            'editor.lineHighlightBackground': '#959cb6', //  editor line highlight background color
            'editorLineNumber.foreground': '#ffffff', //   editor line number color
            'editorCursor.foreground': '#ffffff', //  editor cursor color
            'editorIndentGuide.background': '#ffffff', //  editor indent guide color
            'editor.foreground': '#ffffff', //  editor selection highlight border color
            'editor.selectionBackground': '#4ba1ef', //  editor selection highlight color
            'editor.selectionHighlightBorder': '#4ba1ef', //  editor selection highlight border color
            'editor.findMatchBackground': '#4ba1ef', //  editor find match highlight color
            'editor.wordHighlightBackground': '#8bb2d2', //  editor word highlight color
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
    switch (type) {
        case DIALECT.JAVA:
            return <JavaSvg/>;
        case DIALECT.SCALA:
            return <ScalaSvg/>;
        case DIALECT.PYTHON:
        case DIALECT.PYTHON_LONG:
            return <PythonSvg/>;
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
            time = hour + l('global.time.hour') + min + l('global.time.minute') + second + l('global.time.second');
            if (hour > 24) {
                hour = Math.floor(Math.floor(second_time / 60) / 60) % 24;
                let day = Math.floor(Math.floor(Math.floor(second_time / 60) / 60) / 24);
                time = day + l('global.time.day') + hour + l('global.time.hour') + min + l('global.time.minute') + second + l('global.time.second');
            }
        }
    }
    return time;
};

/**
 * Generate MilliSecond time string
 * @param {number} second_time
 * @returns {any}
 */
export function parseMilliSecondStr(second_time: number) {
    if (((second_time / 1000) % 60) < 1) {
        return second_time + l('global.time.millisecond');
    }
    return parseSecondStr(second_time / 1000);
}


/**
 * build tree data
 * @param data
 * @returns {any}
 */
export const buildTreeData = (data: any): any => data?.map((item: any) => {

    // build key
    let buildKey = item.path + folderSeparator() + item.name;

    const buildTitleLabel = () => {
        return <>{item.name}<span
            style={{color: 'gray'}}> &nbsp;&nbsp;{l('global.size', '', {size: item.size})}</span></>;
    }

    // if has children , recursive build
    if (item.children) {
        return {
            isLeaf: !item.leaf,
            id: item?.id,
            name: item.name,
            parentId: item.path ?? item.parentId,
            icon: renderIcon(item.name, '.', item.leaf),
            content: item.content,
            path: item.path,
            fullName: item?.fullName,
            title: buildTitleLabel(),
            desc: item?.desc ?? item?.description,
            key: buildKey,
            children: buildTreeData(item.children)
        };
    }
    return {
        isLeaf: !item.leaf,
        id: item?.id,
        name: item.name,
        parentId: item.path ?? item.parentId,
        icon: renderIcon(item.name, '.', item.leaf),
        content: item.content,
        path: item.path,
        fullName: item?.fullName,
        desc: item?.desc ?? item?.description,
        title: buildTitleLabel(),
        key: buildKey,
    };
});


/**
 * Determine whether the file is supported
 * @returns {boolean}
 */
export const unSupportView = (name: string) => {

    return name.endsWith(".jar")
        || name.endsWith(".war")
        || name.endsWith(".zip")
        || name.endsWith(".tar.gz")
        || name.endsWith(".tar")
        || name.endsWith(".jpg")
        || name.endsWith(".png")
        || name.endsWith(".gif")
        || name.endsWith(".bmp")
        || name.endsWith(".jpeg")
        || name.endsWith(".ico")
}



/**
 * search tree node
 * @param originValue
 * @param {string} searchValue
 * @returns {any}
 */
export const searchTreeNode = (originValue :string,searchValue: string): any => {

    let title = <>{originValue}</>;

    // searchValue is not empty and trim() after length > 0
    if (searchValue && searchValue.trim().length > 0) {
        const searchIndex = originValue.indexOf(searchValue); // search index
        const beforeStr = originValue.substr(0, searchIndex); // before search value
        const afterStr = originValue.substr(searchIndex + searchValue.length); // after search value
        // when search index > -1, return render title, else return origin title
        title = searchIndex > -1 ?
            <span>{beforeStr}<span className={'treeList tree-search-value'}>{searchValue}</span>{afterStr}</span>
            : <span className={'treeList'}>{title}</span> ;
    }
    return title
};
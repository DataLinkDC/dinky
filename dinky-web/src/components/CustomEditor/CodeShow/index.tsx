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

import MonacoEditor, {monaco} from "react-monaco-editor";
import {convertCodeEditTheme} from "@/utils/function";
import {MonacoEditorOptions} from "@/types/Public/data";
import {FloatButton, Space} from "antd";
import {
    DownCircleFilled,
    StopOutlined,
    SyncOutlined,
    UpCircleFilled
} from "@ant-design/icons";
import React, {useEffect, useState} from "react";
import {editor} from "monaco-editor";
import {l} from "@/utils/intl";
import IStandaloneCodeEditor = editor.IStandaloneCodeEditor;
import ScrollType = editor.ScrollType;

export type CodeShowFormProps = {
    height?: string;
    width?: string;
    language?: string;
    options?: any;
    code: string;
    lineNumbers?: string;
    theme?: string;
    autoWrap?: string;
    showFloatButton?: boolean;
    enableTimer?: boolean;
    refreshLogCallback?: () => void;
};

const CodeShow = (props: CodeShowFormProps) => {

    /**
     * 1. height: edit height
     * 2. width: edit width
     * 3. language: edit language
     * 4. options: edit options
     * 5. code: content
     * 6. readOnly: is readOnly, value: true | false
     * 7. lineNumbers: is show lineNumbers, value: on | off | relative | interval
     * 8. theme: edit theme , value: vs-dark | vs | hc-black
     * 9. autoWrap: is auto wrap, value: on | off | wordWrapColumn | bounded
     */
    const {
        height = "30vh", // if null or undefined, set default value
        width = "100%", // if null or undefined, set default value
        language,
        options = {
            ...MonacoEditorOptions, // set default options
        },
        code, // content
        lineNumbers, // show lineNumbers
        theme, // edit theme
        autoWrap = "on", //  auto wrap
        showFloatButton = false,
        enableTimer = false,
        refreshLogCallback,
    } = props;


    const [loading, setLoading] = useState<boolean>(false);
    const [stoping, setStoping] = useState<boolean>(false);
    const [editor, setEditor] = useState<IStandaloneCodeEditor>();


    const [timer, setTimer] = useState<NodeJS.Timer>();

    // register TypeScript language service
    monaco.languages.register({
        id: language || "typescript",
    });

    /**
     * @description: handle sync log
     * @returns {Promise<void>}
     */
    const handleSyncLog = async () => {
        setLoading(true);
        setTimeout(() => {
            refreshLogCallback?.();
            setLoading(false);
        }, 1000);
    };

    /**
     * @description: handle auto refresh log
     */
    useEffect(() => {
        if (enableTimer) {
            const timerSync = setInterval(() => {
                handleSyncLog();
            }, 5000);
            setTimer(timerSync)
            return () => {
                clearInterval(timerSync);
            }
        }
    }, [])

    /**
     * @description: handle stop auto refresh log
     */
   const handleStopAutoRefresh = () => {
       setStoping(true);
       setTimeout(() => {
           clearInterval(timer);
           setStoping(false)
       }, 1000);

    }


    /**
     * @description: handle scroll to top
     */
    const handleBackTop = () => {
        editor?.revealLine(1);
    }

    /**
     * @description: handle scroll to bottom
     */
    const handleBackBottom = () => {
        // @ts-ignore
        editor?.revealLine(editor?.getModel()?.getLineCount());
    }

    /**
     * @description: handle scroll to down
     */
    const handleDownScroll = () => {
        // @ts-ignore
        editor?.setScrollPosition({scrollTop: editor?.getScrollTop() + 500}, ScrollType.Smooth);
    }

    /**
     * @description: handle scroll to up
     */
    const handleUpScroll = () => {
        // @ts-ignore
        editor?.setScrollPosition({scrollTop: editor?.getScrollTop() - 500}, ScrollType.Smooth);
    }


    /**
     * @description: editorDidMount
     * @param {editor.IStandaloneCodeEditor} editor
     */
    const editorDidMount = (editor: IStandaloneCodeEditor) => {
        setEditor(editor)
        // 在编辑器加载完成后，设置自动布局和自动高亮显示
        editor.layout();
        editor.focus();
    }

    /**
     * @description: render
     */
    return (<>
        <div className={"monaco-float"}>
            <MonacoEditor
                width={width}
                height={height}
                value={loading ? "loading..." : code}
                language={language}
                options={{
                    ...options,
                    readOnly: true,
                    wordWrap: autoWrap,
                    autoDetectHighContrast: true,
                    selectOnLineNumbers: true,
                    fixedOverflowWidgets: true,
                    autoClosingDelete: "always",
                    lineNumbers,
                }}
                editorDidMount={editorDidMount}
                theme={theme ? theme : convertCodeEditTheme()}
            />
            <FloatButton.Group
                open={showFloatButton} shape="square"
                className={"float-button"}
            >
                <Space direction={"vertical"}>
                    {refreshLogCallback &&
                       <>
                           <FloatButton
                               tooltip={l('button.refresh')}
                               icon={<SyncOutlined spin={loading}/>}
                               onClick={handleSyncLog}
                           />
                           {enableTimer &&
                               <FloatButton
                                   tooltip={l('button.stopRefresh')}
                                   icon={<StopOutlined spin={stoping}/>}
                                   onClick={handleStopAutoRefresh}
                               />
                           }
                       </>
                    }
                    <FloatButton.BackTop
                        tooltip={l('button.backTop')}
                        onClick={handleBackTop}
                        visibilityHeight={0}/>
                    <FloatButton.BackTop
                        className={"back-bottom"}
                        tooltip={l('button.backBottom')}
                        onClick={handleBackBottom}
                        visibilityHeight={0}/>
                    <FloatButton
                        icon={<UpCircleFilled/>}
                        tooltip={l('button.upScroll')}
                        onClick={handleUpScroll}
                    />
                    <FloatButton
                        icon={<DownCircleFilled/>}
                        tooltip={l('button.downScroll')}
                        onClick={handleDownScroll}
                    />
                </Space>
            </FloatButton.Group>
        </div>
    </>);
};

export default CodeShow;

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
import {FloatButton} from "antd";
import {SyncOutlined} from "@ant-design/icons";
import {useState} from "react";

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
        refreshLogCallback,
    } = props;


    const [loading, setLoading] = useState<boolean>(false);


    // register TypeScript language service
    monaco.languages.register({
        id: language || "typescript",
    });

    // get monaco editor model
    const getModel = () => monaco.editor.getModels()[0];


    const handleSyncLog = async () => {
        setLoading(true);
        await refreshLogCallback?.();
        setLoading(false);
    };
    return (<>
        <div style={{display: "flex", justifyContent: "space-between"}}>
            <MonacoEditor
                width={width}
                height={height}
                value={code}
                language={language}
                options={{
                    ...options,
                    readOnly: true,
                    wordWrap: autoWrap,
                    autoDetectHighContrast: true,
                    lineNumbers,
                }}
                editorDidMount={(editor) => {
                    // 在编辑器加载完成后，设置自动布局和自动高亮显示
                    editor.layout();
                    editor.focus();
                    getModel().updateOptions({
                        tabSize: 2,
                    });
                }}
                theme={theme ? theme : convertCodeEditTheme()}
            />
            {showFloatButton &&
                <>
                    <FloatButton.Group shape="square" style={{
                        right: 70,
                        top: 70,
                        position: "relative",
                        display: "flow-root"
                    }}>
                        <FloatButton icon={<SyncOutlined spin={loading}/>} onClick={() => handleSyncLog()}/>
                        <FloatButton.BackTop visibilityHeight={0}/>
                    </FloatButton.Group>
                </>

            }
        </div>
    </>);
};

export default CodeShow;

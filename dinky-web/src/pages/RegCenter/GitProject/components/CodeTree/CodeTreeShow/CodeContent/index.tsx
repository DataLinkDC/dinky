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

import React from "react";
import {Empty} from "antd";
import {l} from "@/utils/intl";
import CodeShow from "@/components/CustomEditor/CodeShow";
import {renderLanguage} from "@/utils/function";
import {GitProjectTreeNode} from "@/types/RegCenter/data";


/**
 * CodeContentProps
 */
type CodeContentProps = {
    code: string;
    current: GitProjectTreeNode;
}

/**
 * code edit props
 */
const CodeEditProps = {
    height: "70vh",
    width: "100%",
    lineNumbers: "on",
};


export const CodeContent: React.FC<CodeContentProps> = (props) => {
    /**
     * Get the code and current node from props
     */
    const {code, current} = props;

    /**
     * Get the language according to the file suffix rules
     * @returns {string}
     */
    const getLanguage = () => {
        return renderLanguage(current.name, ".");
    };

    /**
     * Determine whether the file is supported
     * @returns {boolean}
     */
    const unSupportView = () => {
        const {name} = current;

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
     * Render the code display component
     * @returns {JSX.Element}
     */
    const render = () => {
        if (unSupportView()) {
            return <Empty className={"code-content-empty"} description={l("rc.gp.codeTree.unSupportView")}/>
        } else if (code === "" || code === null) {
            return <Empty className={"code-content-empty"} description={l("rc.gp.codeTree.clickShow")}/>
        } else {
            return <CodeShow {...CodeEditProps} language={getLanguage()} showFloatButton code={code}/>
        }
    }

    /**
     * If the code is empty, display the empty component, otherwise display the code
     */
    return <>
        {render()}
    </>;
};

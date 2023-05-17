/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

import CodeShow from "@/components/CustomEditor/CodeShow";
import {Modal} from "antd";
import {l} from "@/utils/intl";
import {GitProject} from "@/types/RegCenter/data";
import React, {useEffect} from "react";
import {handleData} from "@/services/BusinessCrud";
import {API_CONSTANTS} from "@/services/constants";

type ShowLogProps = {
    onCancel: (flag?: boolean) => void;
    modalVisible: boolean;
    values: Partial<GitProject>;
};

/**
 * code edit props
 */
const CodeEditProps = {
    height: "60vh",
    width: "100%",
    lineNumbers: "on",
    language: "java",
};


export const ShowLog: React.FC<ShowLogProps> = (props) => {
    /**
     * props
     */
    const {values, modalVisible, onCancel} = props;

    const [log, setLog] = React.useState<string>("");

    /**
     * query all step logs
     * @returns {Promise<void>}
     */
    const queryAllStepLogs = async () => {
        const result = await handleData(API_CONSTANTS.GIT_PROJECT_BUILD_ALL_LOGS, values.id);
        setLog(result);
    };


    /**
     * effect
     */
    useEffect(() => {
        queryAllStepLogs();
    }, []);


    /**
     *  render
     */
    return (
        <Modal
            title={l("rc.gp.log")}
            width={"80%"}
            open={modalVisible}
            onCancel={() => onCancel()}
            cancelText={l("button.close")}
            okButtonProps={{style: {display: "none"}}}
        >
            <CodeShow
                {...CodeEditProps}
                code={log}
                showFloatButton={true}
                refreshLogCallback={queryAllStepLogs}
            />
        </Modal>
    );
};

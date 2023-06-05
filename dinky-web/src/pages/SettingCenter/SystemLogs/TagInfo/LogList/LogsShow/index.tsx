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

import CodeShow from "@/components/CustomEditor/CodeShow";
import React from "react";
import {l} from "@/utils/intl";
import {Empty} from "antd";

const CodeEditProps = {
  height: "70vh",
  width: "100%",
  lineNumbers: "on",
  language: "java",
};

type LogsShowProps = {
  code: string;
  refreshLogCallback: () => void;
}
// todo:
//    If the log is too large and there are too many contents,
//    MonacoEditor will have performance problems.
//    It is planned to optimize it through `react-virtualized`

const LogsShow: React.FC<LogsShowProps> = (props) => {

  const {code, refreshLogCallback} = props;

  const restLogsShowProps = {
    showFloatButton: true,
    code,
    refreshLogCallback,
  };

  return <>
    { code ? <CodeShow {...restLogsShowProps} {...CodeEditProps} />
    : <Empty className={"code-content-empty"} description={l("sys.info.logList.tips")}/>
    }
  </>;
};

export default LogsShow;

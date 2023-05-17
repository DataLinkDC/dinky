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


import {Col, Row} from "antd";
import React, {useEffect, useState} from "react";
import LogsTree from "@/pages/SettingCenter/SystemInfo/TagInfo/LogList/LogsTree";
import LogsShow from "@/pages/SettingCenter/SystemInfo/TagInfo/LogList/LogsShow";
import {queryDataByParams} from "@/services/BusinessCrud";
import {API_CONSTANTS} from "@/services/constants";

const LogList = () => {
  const [treeData, setTreeData] = useState<Partial<any>[]>([]);
  const [log, setLog] = useState<string>("");
  const [clickFileName, setClickFileName] = useState<any>();

  const queryLogList = async () => {
    await queryDataByParams(API_CONSTANTS.SYSTEM_ROOT_LOG_LIST).then(res => {
      setTreeData(res);
    });
  };


  const queryLogContent = async (fileName: string) => {
    await queryDataByParams(API_CONSTANTS.SYSTEM_ROOT_LOG_READ, {path: fileName}).then(res => {
      setLog(res);
    });
  };


  useEffect(() => {
    queryLogList();
  }, []);

  const handleNodeClick = async (info: any) => {
    const {node: {path, isLeaf}} = info;
    if (isLeaf) {
      setClickFileName(path);
      await queryLogContent(path);
    }
  };


  const refreshLogByClickNode = async () => {
    await queryLogContent(clickFileName);
  };


  return <>
    <Row>
      <Col span={4} className={"siderTree"}>
        {/* tree */}
        <LogsTree treeData={treeData} onNodeClick={(info: any) => handleNodeClick(info)}/>
        {/*<SiderTree treeData={treeData} onNodeClick={(info: any) => handleNodeClick(info)} loading={loading}/>*/}
      </Col>
      <Col span={20}>
        {/* code */}
        <LogsShow code={log} refreshLogCallback={() => refreshLogByClickNode()}/>
        {/*<CodeContent code={codeValue} current={currentGitProjectTreeNode}/>*/}
      </Col>
    </Row>
  </>;
};

export default LogList;

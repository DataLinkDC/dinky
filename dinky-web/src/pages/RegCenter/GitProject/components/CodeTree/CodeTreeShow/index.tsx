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

import React, {useEffect, useState} from "react";
import {GitProject, GitProjectTreeNode} from "@/types/RegCenter/data";
import {API_CONSTANTS} from "@/services/constants";
import {handleData} from "@/services/BusinessCrud";
import {Col, Row} from "antd";
import {SiderTree} from "@/pages/RegCenter/GitProject/components/CodeTree/CodeTreeShow/SiderTree";
import {CodeContent} from "@/pages/RegCenter/GitProject/components/CodeTree/CodeTreeShow/CodeContent";

/**
 * CodeTreeShowProps
 */
type CodeTreeShowProps = {
  values: Partial<GitProject>;
}


export const CodeTreeShow: React.FC<CodeTreeShowProps> = (props) => {
  /**
   * some state
   */
  const {values} = props;
  const [treeData, setTreeData] = useState<Partial<GitProjectTreeNode>[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [codeValue, setCodeValue] = useState<string>("");
  const [currentGitProjectTreeNode, setCurrentGitProjectTreeNode] = useState<GitProjectTreeNode>({
    name: "",
    path: "",
    content: "",
    size: 0,
    leaf: false,
    children: [],
  });

  /**
   * query code tree
   */
  const queryCodeTree = async () => {
    setLoading(true);
    await handleData(API_CONSTANTS.GIT_PROJECT_CODE_TREE, values.id).then((res) => {
      setTreeData(res);
    });
    setLoading(false);
  };


  /**
   * init
   */
  useEffect(() => {
    queryCodeTree();
  }, [values.id]);


  /**
   * handle node click
   * @param selectedKeys
   * @param info
   */
  const handleNodeClick = (info: any) => {
    const {node} = info;
    setCurrentGitProjectTreeNode({...node});
    setCodeValue(node.content);
  };


  /**
   * render
   */
  return <>
    <Row>
      <Col span={6} className={"siderTree gitCodeTree"}>
        {/* tree */}
        <SiderTree treeData={treeData} onNodeClick={(info: any) => handleNodeClick(info)} loading={loading}/>
      </Col>
      <Col span={18}>
        {/* code */}
        <CodeContent code={codeValue} current={currentGitProjectTreeNode}/>
      </Col>
    </Row>
  </>;
};

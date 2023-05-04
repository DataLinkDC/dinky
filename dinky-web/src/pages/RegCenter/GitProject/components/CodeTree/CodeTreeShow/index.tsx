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

import React, {useEffect} from "react";
import {GitProject} from "@/types/RegCenter/data";
import {Col, Layout, Row, Space, Tree} from "antd";
import CodeShow from "@/components/CustomEditor/CodeShow";
import {getData, queryList} from "@/services/api";
import {API_CONSTANTS} from "@/services/constants";
import {ExpandOutlined} from "@ant-design/icons";

type CodeTreeShowProps = {
  values: Partial<GitProject>;
}

const {TreeNode} = Tree;

/**
 * code edit props
 */
const CodeEditProps = {
  height: "60vh",
  width: "100%",
  lineNumbers: "on",
};


export const CodeTreeShow: React.FC<CodeTreeShowProps> = (props) => {
  const {values} = props;
  const [codeTreeList, setCodeTreeList] = React.useState<string[]>([]);
  const buildCodeTree = (codeTreeList: string[]) => {
    // TODO: build code tree

  };

  /**
   * query code tree
   */
  const queryCodeTree = async () => {
    // await getData(API_CONSTANTS.GIT_GET_CODE_DIR_LIST, {id: values.id}).then((res) => {
    //   console.log(res);
    // })
  };

  /**
   * init
   */
  useEffect(() => {
    queryCodeTree();
  }, []);

  /**
   * sider style
   */
  const siderStyle: React.CSSProperties = {
    maxHeight: "60vh",
    overflowX: "auto",
    overflowY: "auto",
    borderRadius: "4px",
  };

  return <>
    <Row>
      <Col span={20} push={4}>
        <CodeShow
          {...CodeEditProps}
          code={"\n\n\n\n\npublic static void test(\n" +
          "\t\tString a = 1;\n" +
          ")"} language={"java"}/>
      </Col>
      <Col style={siderStyle} span={4} pull={20}>
        <Tree
          blockNode
          checkStrictly
          virtual
          focusable
          autoExpandParent
          showLine
          showIcon
          defaultExpandAll
        >
          <TreeNode title="src">
            <TreeNode title="components">
              <TreeNode title="Demo.js"/>
            </TreeNode>
            <TreeNode title="index.js"/>
          </TreeNode>
        </Tree>
      </Col>
    </Row>
  </>;
};

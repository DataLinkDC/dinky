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

import { CodeContent } from '@/pages/RegCenter/GitProject/components/CodeTree/CodeTreeShow/CodeContent';
import { SiderTree } from '@/pages/RegCenter/GitProject/components/CodeTree/CodeTreeShow/SiderTree';
import { handleData } from '@/services/BusinessCrud';
import { API_CONSTANTS } from '@/services/endpoints';
import { GitProject, GitProjectTreeNode } from '@/types/RegCenter/data';
import { SplitPane } from '@andrewray/react-multi-split-pane';
import { Pane } from '@andrewray/react-multi-split-pane/dist/lib/Pane';
import { ProCard } from '@ant-design/pro-components';
import React, { useEffect, useRef, useState } from 'react';

/**
 * CodeTreeShowProps
 */
type CodeTreeShowProps = {
  values: Partial<GitProject>;
};

export const CodeTreeShow: React.FC<CodeTreeShowProps> = (props) => {
  /**
   * some state
   */
  const { values } = props;
  const refObject = useRef<HTMLDivElement>(null);

  const [treeData, setTreeData] = useState<Partial<GitProjectTreeNode>[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [codeValue, setCodeValue] = useState<string>('');
  const [currentGitProjectTreeNode, setCurrentGitProjectTreeNode] = useState<GitProjectTreeNode>({
    name: '',
    path: '',
    content: '',
    size: 0,
    leaf: false,
    children: []
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
    const { node } = info;
    setCurrentGitProjectTreeNode({ ...node });
    setCodeValue(node.content);
  };

  /**
   * render
   */
  return (
    <>
      <ProCard size={'small'} bodyStyle={{ height: parent.innerHeight - 320, overflow: 'auto' }}>
        <SplitPane
          split={'vertical'}
          defaultSizes={[100, 500]}
          minSize={150}
          className={'split-pane'}
        >
          <Pane
            className={'split-pane'}
            forwardRef={refObject}
            minSize={100}
            size={100}
            split={'horizontal'}
          >
            <ProCard
              ghost
              hoverable
              bordered
              size={'small'}
              bodyStyle={{ height: parent.innerHeight, overflow: 'auto' }}
              colSpan={'20%'}
            >
              {/* tree */}
              <SiderTree
                treeData={treeData}
                onNodeClick={(info: any) => handleNodeClick(info)}
                loading={loading}
              />
            </ProCard>
          </Pane>

          <Pane
            className={'split-pane'}
            forwardRef={refObject}
            minSize={100}
            size={100}
            split={'horizontal'}
          >
            <ProCard
              ghost
              hoverable
              bordered
              size={'small'}
              bodyStyle={{ height: parent.innerHeight }}
            >
              <CodeContent code={codeValue} current={currentGitProjectTreeNode} />
            </ProCard>
          </Pane>
        </SplitPane>
      </ProCard>
    </>
  );
};

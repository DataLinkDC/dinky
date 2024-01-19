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

import { buildTreeData } from '@/pages/RegCenter/GitProject/components/CodeTree/function';
import { GitProjectTreeNode } from '@/types/RegCenter/data';
import { PageLoading } from '@ant-design/pro-components';
import { Empty, Tree } from 'antd';
import React from 'react';

/**
 * sider tree props
 */
type SiderTreeProps = {
  treeData: Partial<GitProjectTreeNode>[];
  onNodeClick: (info: any) => void;
  loading: boolean;
};

const { DirectoryTree } = Tree;

export const SiderTree: React.FC<SiderTreeProps> = (props) => {
  /**
   * sider tree props
   */
  const { loading, treeData, onNodeClick } = props;

  /**
   * render , if loading is true , show loading page , else show tree
   */
  return (
    <>
      {loading ? (
        <PageLoading />
      ) : treeData.length > 0 ? (
        <DirectoryTree
          className={'siderTree treeList gitCodeTree'}
          onSelect={(_, info) => onNodeClick(info)}
          treeData={buildTreeData(treeData)}
        />
      ) : (
        <Empty className={'code-content-empty'} />
      )}
    </>
  );
};

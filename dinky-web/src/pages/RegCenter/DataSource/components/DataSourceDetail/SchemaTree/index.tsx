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


import {Empty, Tree} from 'antd';
import React from 'react';
import {buildSchemaTree} from '@/pages/RegCenter/DataSource/components/DataSourceDetail/function';
import { Key } from '@ant-design/pro-components';

const {DirectoryTree} = Tree;

/**
 * props
 */
type SchemaTreeProps = {
  treeData: Partial<any>[];
  onNodeClick: (keys: Key[] ,info: any) => void
  style?: React.CSSProperties
  expandKeys: string[]
  onExpand: (keys: Key[]) => void
  selectKeys: string[]
}


const SchemaTree: React.FC<SchemaTreeProps> = (props) => {
  const {treeData, onNodeClick , style,expandKeys,onExpand ,selectKeys} = props;

  /**
   * render
   */
  return <>
    {
      (treeData.length > 0) ?
        <DirectoryTree
          expandedKeys={expandKeys}
          selectedKeys={selectKeys}
          onExpand={(keys) => onExpand(keys)}
          style={style}
          className={'treeList'}
          onSelect={onNodeClick}
          treeData={buildSchemaTree(treeData)}
        /> : <Empty className={'code-content-empty'}/>
    }
  </>;
};

export default SchemaTree;

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
import {DatabaseTwoTone, TableOutlined, TabletTwoTone} from '@ant-design/icons';
import {folderSeparator} from '@/utils/function';

const {DirectoryTree} = Tree;


type SchemaTreeProps = {
  treeData: Partial<any>[];
  onNodeClick: (info: any) => void
}


const SchemaTree:React.FC<SchemaTreeProps> = (props) => {
  const {treeData, onNodeClick} = props;

  const buildSchemaTree = (data: any): any => data.map((item: any) => {
    return {
      isLeaf: false,
      name: item.name,
      parentId: item.name,
      icon: <DatabaseTwoTone/>,
      content: item.name,
      path: item.name,
      title: item.name,
      fullInfo: item,
      key: item.name,
      children: item.tables.map((table: any) => {
        return {
          isLeaf: true,
          name: table.name,
          parentId: item.name,
          icon: <TableOutlined/>,
          content: table.name,
          path: item.name + folderSeparator() + table.name,
          title: table.name,
          key: item.name + folderSeparator() + table.name,
          fullInfo: table,
        };
      }),
    };
  });

  return<>
    {
      (treeData.length > 0) ?
        <DirectoryTree
          onSelect={(_, info) => onNodeClick(info)}
          treeData={buildSchemaTree(treeData)}
        /> : <Empty className={"code-content-empty"}/>
    }
  </>
}

export default SchemaTree;

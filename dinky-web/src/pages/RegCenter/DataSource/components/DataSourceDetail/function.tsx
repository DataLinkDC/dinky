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

import { folderSeparator, searchTreeNode } from '@/utils/function';
import { DatabaseTwoTone, TableOutlined } from '@ant-design/icons';
import { l } from '@/utils/intl';

/**
 *  build schema tree
 * @param data
 * @param searchValue
 */
export const buildSchemaTree = (data: any, searchValue = ''): any =>
  data.map((item: any) => {
    const title = (
      <p
        style={{
          marginBottom: 0,
          padding: 0,
          textOverflow: 'ellipsis',
          whiteSpace: 'nowrap',
          overflow: 'hidden',
          display: 'flex',
          alignItems: 'center'
        }}
      >
        {item.name}{' '}
        <span style={{ fontSize: 12, color: '#999' }}>
          ({l('rc.ds.total.table', '', { total: item.tables.length })})
        </span>
      </p>
    );

    return {
      isLeaf: false,
      name: item.name,
      parentId: item.name,
      icon: <DatabaseTwoTone />,
      content: item.name,
      path: item.name,
      title: title,
      fullInfo: item,
      key: item.name,
      children: item.tables
        // filter table by search value and map table to tree node
        .filter((table: any) => table.name.indexOf(searchValue) > -1)
        .map((table: any) => {
          return {
            isLeaf: true,
            name: table.name,
            parentId: item.name,
            icon: <TableOutlined />,
            content: table.name,
            path: item.name + folderSeparator() + table.name,
            title: searchTreeNode(table.name, searchValue),
            key: item.name + folderSeparator() + table.name,
            fullInfo: table
          };
        })
    };
  });

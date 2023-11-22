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

import { buildMenuTree, sortTreeData } from '@/pages/AuthCenter/Menu/function';
import { SysMenu } from '@/types/AuthCenter/data';
import { l } from '@/utils/intl';
import { Empty, Input, Spin, Tree } from 'antd';
import React, { useCallback, useState } from 'react';

const { DirectoryTree } = Tree;

type MenuTreeProps = {
  treeData: SysMenu[];
  onNodeClick: (info: any) => void;
  onRightClick: (info: any) => void;
  selectedKeys: string[] | number[];
  loading: boolean;
};
const MenuTree: React.FC<MenuTreeProps> = (props) => {
  const { treeData = [], selectedKeys, onNodeClick, onRightClick, loading } = props;

  const [searchValue, setSearchValue] = useState('');

  /**
   * search tree node
   * @type {(e: {target: {value: React.SetStateAction<string>}}) => void}
   */
  const onSearchChange = useCallback(
    (e: { target: { value: React.SetStateAction<string> } }) => {
      setSearchValue(e.target.value);
    },
    [searchValue]
  );

  return (
    <>
      {treeData.length > 0 ? (
        <>
          <Input
            placeholder={l('global.search.text')}
            allowClear
            style={{ marginBottom: 8 }}
            value={searchValue}
            onChange={onSearchChange}
          />
          <Spin spinning={loading}>
            <DirectoryTree
              selectedKeys={selectedKeys}
              onSelect={(_, info) => onNodeClick(info)}
              onRightClick={(info) => onRightClick(info)}
              treeData={buildMenuTree(sortTreeData(treeData), searchValue)}
            />
          </Spin>
        </>
      ) : (
        <Empty className={'code-content-empty'} image={Empty.PRESENTED_IMAGE_DEFAULT} />
      )}
    </>
  );
};

export default MenuTree;

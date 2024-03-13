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

import { getCurrentTab } from '@/pages/DataStudio/function';
import {
  buildProjectTree,
  generateList,
  getLeafKeyList,
  getParentKey
} from '@/pages/DataStudio/LeftContainer/Project/function';
import { StateType, STUDIO_MODEL, TabsItemType } from '@/pages/DataStudio/model';
import { l } from '@/utils/intl';
import { connect } from '@@/exports';
import { Key } from '@ant-design/pro-components';
import { Empty, Tree } from 'antd';
import Search from 'antd/es/input/Search';
import React, { useEffect, useState } from 'react';
import { BtnRoute, useTasksDispatch } from '../../BtnContext';

const { DirectoryTree } = Tree;

/**
 * props
 */
type TreeProps = {
  onNodeClick: (info: any) => void;
  onRightClick: (info: any) => void;
  style?: React.CSSProperties;
  selectKeyChange: (keys: Key[]) => void;
  onExpand: (expandedKeys: Key[]) => void;
};

const JobTree: React.FC<TreeProps & connect> = (props) => {
  const {
    project: { data: projectData, expandKeys, selectKey },
    onNodeClick,
    style,
    height,
    onRightClick,
    selectKeyChange,
    onExpand,
    dispatch
  } = props;

  const [searchValue, setSearchValueValue] = useState('');
  const [data, setData] = useState<any[]>(buildProjectTree(projectData, searchValue));
  const btnDispatch = useTasksDispatch();

  useEffect(() => {
    setData(buildProjectTree(projectData, searchValue));
  }, [searchValue, projectData]);

  const [autoExpandParent, setAutoExpandParent] = useState(true);
  const onChangeSearch = (e: any) => {
    let { value } = e.target;
    if (!value) {
      dispatch({
        type: STUDIO_MODEL.updateProjectExpandKey,
        payload: []
      });
      setSearchValueValue(value);
      return;
    }
    value = String(value).trim();
    const expandList: any[] = generateList(data, []);
    let expandedKeys: any = expandList
      .map((item: any) => {
        if (item?.name.indexOf(value) > -1) {
          return getParentKey(item.key, data);
        }
        return null;
      })
      .filter((item: any, i: number, self: any) => item && self.indexOf(item) === i);
    dispatch({
      type: STUDIO_MODEL.updateProjectExpandKey,
      payload: expandedKeys
    });
    setSearchValueValue(value);
    setAutoExpandParent(true);
  };

  const expandAll = () => {
    dispatch({
      type: STUDIO_MODEL.updateProjectExpandKey,
      payload: getLeafKeyList(projectData)
    });
  };

  const currentTabName = 'menu.datastudio.project';
  const btnEvent = [...BtnRoute[currentTabName]];
  const positionKey = (panes: TabsItemType[], activeKey: string) => {
    const treeKey = getCurrentTab(panes, activeKey)?.treeKey;
    if (treeKey) {
      const expandList: any[] = generateList(data, []);
      let expandedKeys: any = expandList
        .map((item: any) => {
          if (item?.key === treeKey) {
            return getParentKey(item.key, data);
          }
          return null;
        })
        .filter((item: any, i: number, self: any) => item && self.indexOf(item) === i);
      dispatch({
        type: STUDIO_MODEL.updateProjectExpandKey,
        payload: expandedKeys
      });
      setAutoExpandParent(true);
      selectKeyChange([treeKey]);
    }
  };

  btnEvent[1].onClick = expandAll;

  btnEvent[2].onClick = () =>
    dispatch({
      type: STUDIO_MODEL.updateProjectExpandKey,
      payload: []
    });
  btnEvent[3].onClick = positionKey;
  btnDispatch({
    type: 'change',
    selectKey: currentTabName,
    payload: btnEvent
  });

  return (
    <>
      <Search
        style={{ margin: '8px 0px' }}
        placeholder={l('global.search.text')}
        onChange={onChangeSearch}
        allowClear={true}
      />

      {data.length ? (
        <DirectoryTree
          style={{ ...style, height: height - 40 - 16, overflowY: 'auto' }}
          className={'treeList'}
          onSelect={(_, info) => onNodeClick(info)}
          onRightClick={onRightClick}
          expandedKeys={expandKeys}
          expandAction={'doubleClick'}
          selectedKeys={selectKey}
          onExpand={onExpand}
          treeData={data}
          // autoExpandParent={autoExpandParent}
        />
      ) : (
        <Empty
          className={'code-content-empty'}
          description={l('datastudio.project.create.folder.tip')}
        />
      )}
    </>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  height: Studio.toolContentHeight,
  project: Studio.project
}))(JobTree);

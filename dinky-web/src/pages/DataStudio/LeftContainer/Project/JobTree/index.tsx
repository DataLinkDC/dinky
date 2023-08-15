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

import React, {useState} from "react";
import {Empty, Tree} from "antd";
import {
  buildProjectTree, generateList, getParentKey,
} from "@/pages/DataStudio/LeftContainer/Project/function";
import {connect} from "@@/exports";
import {StateType} from "@/pages/DataStudio/model";
import {Key} from "@ant-design/pro-components";
import {BtnRoute} from "@/pages/DataStudio/route";
import Search from "antd/es/input/Search";
import {l} from "@/utils/intl";
import {Catalogue} from "@/types/Studio/data";


const {DirectoryTree} = Tree;


/**
 * props
 */
type TreeProps = {
  treeData: Catalogue[];
  onNodeClick: (info: any) => void
  onRightClick: (info: any) => void
  style?: React.CSSProperties
  selectedKeys: Key[]
}


const JobTree: React.FC<TreeProps & connect> = (props) => {
  const {treeData, onNodeClick, style, height,onRightClick,selectedKeys} = props;
  const [searchValue,setSearchValueValue] = useState("");
  const data = buildProjectTree(treeData, searchValue);

  const [expandedKeys, setExpandedKeys] = useState<Key[]>();
  const [autoExpandParent, setAutoExpandParent] = useState(true);

  const onChangeSearch = (e: any) => {
    let {value} = e.target;
    if (!value) {
      setExpandedKeys([]);
      setSearchValueValue(value);
      return
    }
    value = String(value).trim();
    const expandList: any[] = generateList(data, []);
    let expandedKeys: any = expandList.map((item: any) => {
      if (item && item.name.indexOf(value) > -1) {
        return getParentKey(item.key, data);
      }
      return null;
    })
      .filter((item: any, i: number, self: any) => item && self.indexOf(item) === i)
    setExpandedKeys(expandedKeys)
    setSearchValueValue(value)
    setAutoExpandParent(true)
  }

  const onExpand = (expandedKeys: Key[]) => {
    setExpandedKeys(expandedKeys);
  };
  const expandAll = () => {
    const map = (data).filter((x: { isLeaf: any; })=>!x.isLeaf).map((x: { key: any; }) => x.key);
    setExpandedKeys(map);
  }
  const btn = BtnRoute['menu.datastudio.project'];

  btn[1].onClick = expandAll

  btn[2].onClick = () => setExpandedKeys([]);


  return <>
    <Search style={{margin:"8px 0px"}}  placeholder={l('global.search.text')} onChange={onChangeSearch} allowClear={true}/>

    {
      (treeData.length > 0) ?
        <DirectoryTree
          style={{...style, height: height-40-16,overflowY:'auto'}}
          className={'treeList'}
          onSelect={(_, info) => onNodeClick(info)}
          onRightClick={onRightClick}
          expandedKeys={expandedKeys}
          selectedKeys={selectedKeys}
          onExpand={onExpand}
          treeData={data}
          autoExpandParent={autoExpandParent}
        /> : <Empty className={'code-content-empty'} description={l('datastudio.project.create.folder.tip')}/>
    }
  </>;
};

export default connect(({Studio}: { Studio: StateType }) => ({
  height: Studio.toolContentHeight,
}))(JobTree);

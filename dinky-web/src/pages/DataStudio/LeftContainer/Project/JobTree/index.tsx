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
import {convertToTreeData, generateList, getParentKey, loop, TreeDataNode} from "@/pages/DataStudio/LeftContainer/Project/function";
import {connect} from "@@/exports";
import {StateType} from "@/pages/DataStudio/model";
import {Key} from "@ant-design/pro-components";
import {BtnRoute} from "@/pages/DataStudio/route";
import Search from "antd/es/input/Search";
import {never} from "@umijs/utils/compiled/zod";


const {DirectoryTree} = Tree;


/**
 * props
 */
type TreeProps = {
  treeData: TreeDataNode[];
  onNodeClick: (info: any) => void
  style?: React.CSSProperties
}


const JobTree: React.FC<TreeProps & connect> = (props) => {
  const {treeData, onNodeClick, style, height} = props;
  const [searchValue,setSearchValueValue] = useState("");
  const data = loop(convertToTreeData(JSON.parse(JSON.stringify(treeData)), 0), searchValue);

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
    const map = (data as TreeDataNode[]).filter(x=>!x.isLeaf).map(x => x.key);
    setExpandedKeys(map);
  }
  const btn = BtnRoute['menu.datastudio.project'];
  btn[0].onClick = expandAll

  btn[1].onClick = () => setExpandedKeys([]);


  return <>
    <Search style={{marginBottom: 8}}  placeholder="Search" onChange={onChangeSearch} allowClear={true}/>

    {
      (treeData.length > 0) ?
        <DirectoryTree
          style={{...style, height: height-40,overflowY:'auto'}}
          // className={'treeList'}
          onSelect={(_, info) => onNodeClick(info)}
          expandedKeys={expandedKeys}
          onExpand={onExpand}
          treeData={data}
          autoExpandParent={autoExpandParent}
        /> : <Empty className={'code-content-empty'} description={"暂无作业,请点击左上角新建目录"}/>
    }
  </>;
};

export default connect(({Studio}: { Studio: StateType }) => ({
  height: Studio.toolContentHeight,
}))(JobTree);

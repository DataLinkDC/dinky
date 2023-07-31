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


import React, {useEffect, useState} from "react";
import JobTree from "@/pages/DataStudio/LeftContainer/Project/JobTree";
import {connect} from "umi";
import {StateType} from "@/pages/DataStudio/model";
import Search from "antd/es/input/Search";
import {TreeDataNode} from "@/pages/DataStudio/LeftContainer/Project/function";


const Project: React.FC = (props: connect) => {
  const {dispatch}=props
  const [search,setSearch] = useState("");

  return (
    <div style={{paddingInline: 5}}>
      <JobTree search={search} onNodeClick={(info:any) => {
        // 选中的key
        const {node: {isLeaf, name, fullInfo,type,parentId,path,key}} = info;
        console.log(info);
        if (isLeaf) {
          // const queryParams =  {id: selectDatabaseId , schemaName, tableName};
          dispatch({
            type: "Studio/addTab",
            payload: {
              icon: type,
              id: parentId+name,
              breadcrumbLabel: path.join("/"),
              label: name ,
              params:key,
              type: "project"
            }
          })
        }
      }} treeData={props.data}/>
    </div>
  )
};

export default connect(({Studio}: { Studio: StateType }) => ({
  data: Studio.project.data,
}))(Project);

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
import JobTree from "@/pages/DataStudio/LeftContainer/Project/JobTree";
import {connect} from "umi";
import {StateType} from "@/pages/DataStudio/model";
import {getTaskDetails} from "@/pages/DataStudio/LeftContainer/Project/service";


const Project: React.FC = (props: connect) => {
  const {dispatch}=props

  return (
    <div style={{paddingInline: 5}}>
      <JobTree onNodeClick={(info:any) => {
        // 选中的key
        const {node: {isLeaf, name, fullInfo,type,parentId,path,key,taskId}} = info;
        if (isLeaf) {
          // const queryParams =  {id: selectDatabaseId , schemaName, tableName};
          getTaskDetails(taskId).then(res=>{
            path.pop()
            dispatch({
              type: "Studio/addTab",
              payload: {
                icon: type,
                id: parentId+name,
                breadcrumbLabel: path.join("/"),
                label: name ,
                params:{taskId:taskId,taskData:res},
                type: "project",
                subType: type.toLowerCase()
              }
            })
          })
        }
      }} treeData={props.data}/>
    </div>
  )
};

export default connect(({Studio}: { Studio: StateType }) => ({
  data: Studio.project.data,
}))(Project);

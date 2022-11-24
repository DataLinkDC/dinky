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


import {DataNode} from "antd/lib/tree";

export type DataType = {
  id:number;
  parentId:number;
  isLeaf:boolean;
  children:DataType[];
};
export interface TreeDataNode extends DataNode {
  name:string;
  id:number;
  taskId:number;
  parentId:number;
  path:string[];
  schema:string;
  table:string;
}

export function convertToTreeData(data:TreeDataNode[], pid:number,path?:string[]) {
  !path&&(path=[]);
  const result:TreeDataNode[] = [];
  let temp:TreeDataNode[] = [];
  for (let i = 0; i < data?.length; i++) {
    if (data[i].parentId === pid) {
      let obj = data[i];
      obj.title = obj.name;
      obj.key = obj.id;
      obj.path = path.slice();
      obj.path.push(obj.name);
      temp = convertToTreeData(data, data[i].id,obj.path);
      if (temp.length > 0) {
        obj.children = temp
      }
      result.push(obj)
    }
  }
  return result
}

export function getTreeNodeByKey(node:any[], key:number) {
  for(let i=0;i<node.length;i++) {
    if (node[i].key == key) {
      return node[i];
    } else if (node[i].children) {
      let result = getTreeNodeByKey(node[i].children, key);
      if(result){
        return result;
      }
    }
  }
  return null;
}

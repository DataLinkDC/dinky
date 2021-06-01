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
  parentId:number;
  path:string[];
}

export function convertToTreeData(data:TreeDataNode[], pid:number,path?:string[]) {
  !path&&(path=[]);
  const result:TreeDataNode[] = [];
  let temp:TreeDataNode[] = [];
  for (let i = 0; i < data.length; i++) {
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

export function getLeafFromTree(data:DataType[], arr:DataType[]) {
  if (typeof arr == 'undefined') {
    arr = [];
  }
  for (let i = 0; i < data.length; i++) {
    let sonList = data[i].children;
    if (sonList) {
      if (sonList.length == 0) {
        arr.push(data[i]);
      } else {
        getLeafFromTree(sonList, arr);
      }
    } else {
      arr.push(data[i]);
    }
  }
  return arr;
}

export function getChildFromTree(data:DataType[], arr:DataType[]) {
  if (typeof arr == 'undefined') {
    arr = [];
  }
  for (let i = 0; i < data.length; i++) {
    if (data[i].isParent) {
      let sonList = data[i].children;
      if (!sonList || sonList == null || sonList.length == 0) {
      } else {
        getChildFromTree(sonList, arr);
      }
    } else {
      arr.push(data[i]);
    }
  }
  return arr;
}

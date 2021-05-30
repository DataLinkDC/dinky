import {DataNode} from "antd/lib/tree";

export type DataType = {
  id:number;
  parentId:number;
  isDir:boolean;
  isLeaf:boolean;
  children:DataType[];
};
export interface TreeDataNode extends DataNode {
  name:String;
  parentId:number;
  isDir:boolean;
}

export function convertToTreeData(data:TreeDataNode[], pid:number) {
  const result:TreeDataNode[] = [];
  let temp:TreeDataNode[] = [];
  for (let i = 0; i < data.length; i++) {
    if (data[i].parentId === pid) {
      let obj = data[i];
      temp = convertToTreeData(data, data[i].id);
      if (temp.length > 0) {
        obj.children = temp
      }
      obj.isLeaf = obj.isDir;
      obj.title = obj.name;
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

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

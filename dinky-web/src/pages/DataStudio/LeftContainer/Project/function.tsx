import { DataNode } from "antd/es/tree";
import {getIcon} from "@/utils/function";
import style from "./index.less";
import {getTabIcon} from "@/pages/DataStudio/MiddleContainer/function";

export interface TreeDataNode extends DataNode {
  name:string;
  type?:string;
  id:number;
  taskId:number;
  parentId:number;
  path:string[];
  schema:string;
  table:string;
}

export const generateList = (data: any, list: any[]) => {
  for (const element of data) {
    const node = element;
    const {name, id, parentId, level} = node;
    list.push({name, id, key: id, title: name, parentId, level});
    if (node.children) {
      generateList(node.children, list);
    }
  }
  return list
};
export const getParentKey = (key: number | string, tree: any): any => {
  let parentKey;
  for (const element of tree) {
    const node = element;
    if (node.children) {
      if (node.children.some((item: any) => item.id === key)) {
        parentKey = node.id;
      } else if (getParentKey(key, node.children)) {
        parentKey = getParentKey(key, node.children);
      }
    }
  }
  return parentKey;
};
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

export const loop:any= (data: TreeDataNode[],searchValue:string) =>
  data?.map((item: any) => {
    const index = item.title.indexOf(searchValue);
    const beforeStr = item.name.substring(0, index);
    const afterStr = item.name.substring(index + searchValue.length);

    item.icon = getTabIcon(item.type,20);
    const title =
      index > -1 ? (
        <span>
            {beforeStr}
          <span className={style['site-tree-search-value']}>{searchValue}</span>
          {afterStr}
            </span>
      ) : (
        <div>
          <span>{item.name}</span>
        </div>
      );
    if (item.children) {
      return {
        isLeaf: item.isLeaf,
        name: item.name,
        id: item.id,
        taskId: item.taskId,
        type: item.type,
        parentId: item.parentId,
        path: item.path,
        icon: item.isLeaf ? item.icon : '',
        title,
        key: item.key,
        children: loop(item.children,searchValue)
      };
    }
    return {
      isLeaf: item.isLeaf,
      name: item.name,
      id: item.id,
      taskId: item.taskId,
      type: item.type,
      parentId: item.parentId,
      path: item.path,
      icon: item.isLeaf ? item.icon : '',
      title: title,
      key: item.key,
    };
  });

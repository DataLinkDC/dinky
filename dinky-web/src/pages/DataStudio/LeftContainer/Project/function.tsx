import {searchTreeNode} from "@/utils/function";
import {getTabIcon} from "@/pages/DataStudio/MiddleContainer/function";
import {SysMenu} from "@/types/RegCenter/data";
import {Catalogue} from "@/types/Studio/data";
import path from "path";
import {DIALECT} from "@/services/constants";


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



/**
 * build menu tree
 * @param {SysMenu[]} data
 * @param {string} searchValue
 * @param path
 * @returns {any}
 */

export const buildProjectTree = (data: Catalogue[], searchValue: string = '', path?: string[]): any => data.filter((ca: Catalogue) => (ca.name.toLowerCase().indexOf(searchValue.toLowerCase()) > -1)).map((item: Catalogue) => {

  // 使用 name 拼接 path todo: 有 bug ，需要修复
    if (path) {
        path.push(...path,item.name)
    }

  return {
    // isLeaf: (item.type && item.children.length === 0) ,
    isLeaf: item.isLeaf ,
    name: item.name,
    parentId: item.parentId,
    label: searchTreeNode(item.name, searchValue),
    icon:  (item.type && item.children.length === 0) && getTabIcon(item.type,20),
    value: item.id,
    path: path || [],
    type: item.type,
    title: <>{searchTreeNode(item.name, searchValue)}</>,
    fullInfo: item,
    key: item.id,
    id: item.id,
    taskId: item.taskId,
    children: buildProjectTree(item.children, searchValue , path),
  }
});


export const isUDF = (jobType: string) => {
  return (jobType === 'Scala' || jobType === 'Python' || jobType === 'Java')
}

export const buildUDFTree = (data: []) => {

}
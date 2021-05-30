import React, {useEffect, useState} from "react";
import {connect} from "umi";
import {StateType} from "@/pages/Demo/FormStepForm/model";
import  {DownOutlined, FrownFilled, FrownOutlined, MehOutlined, SmileOutlined} from "@ant-design/icons";
import {Tree, Input, Menu} from 'antd';
import {getCatalogueTreeData} from "@/pages/FlinkSqlStudio/service";
import {convertToTreeData, DataType, TreeDataNode} from "@/components/Studio/StudioTree/Function";
import style from "./index.less";

const { DirectoryTree } = Tree;

const {Search} = Input;

type StudioTreeProps = {};

type RightClickMenu = {
  pageX: number,
  pageY: number,
  id: number,
  categoryName: string
};

const getParentKey = (key, tree) => {
  let parentKey;
  for (let i = 0; i < tree.length; i++) {
    const node = tree[i];
    if (node.children) {
      if (node.children.some(item => item.key === key)) {
        parentKey = node.key;
      } else if (getParentKey(key, node.children)) {
        parentKey = getParentKey(key, node.children);
      }
    }
  }
  return parentKey;
};

const StudioTree: React.FC<StudioTreeProps> = (props) => {

  const [treeData, setTreeData] = useState<TreeDataNode[]>();
  const [dataList, setDataList] = useState<[]>();
  const [rightClickNodeTreeItem,setRightClickNodeTreeItem] = useState<RightClickMenu>();
  // const [searchValue,setSearchValue] = useState<string>();
  // const [expandedKeys,setExpandedKeys] = useState<any>();
  // const [autoExpandParent,setAutoExpandParent] = useState<boolean>(true);

  /*const loop = data =>
    data.map(item => {
      const index = item.title.indexOf(searchValue);
      const beforeStr = item.title.substr(0, index);
      const afterStr = item.title.substr(index + searchValue.length);
      const title =
        index > -1 ? (
          <span>
              {beforeStr}
            <span className="site-tree-search-value">{searchValue}</span>
            {afterStr}
            </span>
        ) : (
          <span>{item.title}</span>
        );
      if (item.children) {
        return { title, key: item.key, children: loop(item.children) };
      }

      return {
        title,
        key: item.key,
      };
    });*/

  const getTreeData = async () => {
    const result = await getCatalogueTreeData();
    let data = result.datas;
    let list = data;
    for(let i=0;i<list.length;i++){
      list[i].title=list[i].name;
      list[i].key=list[i].id;
      list[i].isLeaf=!list[i].isDir;
    }
    setDataList(list);
    data = convertToTreeData(data, 0);
    setTreeData(data);
  };

  useEffect(() => {
    getTreeData();
  }, []);

  /*const onExpand = () => {
    setExpandedKeys(expandedKeys);
    console.log(autoExpandParent);
    setAutoExpandParent(!autoExpandParent);
  };*/

  const onChange = (e:any) => {
    /*const { value } = e.target;
    const expandedKeys = dataList
      .map(item => {
        if (item.title.indexOf(value) > -1) {
          return getParentKey(item.key, treeData);
        }
        return null;
      })
      .filter((item, i, self) => item && self.indexOf(item) === i);
    setSearchValue(value);
    setExpandedKeys(expandedKeys);
    setAutoExpandParent(true);*/
  };

  const handleMenuClick=()=>{
    setRightClickNodeTreeItem(null);
  };

  const getNodeTreeRightClickMenu = () => {
    const {pageX, pageY} = {...rightClickNodeTreeItem};
    const tmpStyle = {
      position: 'absolute',
      left: `${pageX - 50}px`,
      top: `${pageY - 202}px`
    };
    const menu = (
      <Menu
        onClick={handleMenuClick}
        style={tmpStyle}
        className={style.right_click_menu}
      >
        <Menu.Item key='1'>{'创建目录'}</Menu.Item>
        <Menu.Item key='2'>{'创建作业'}</Menu.Item>
        <Menu.Item key='4'>{'修改'}</Menu.Item>
        <Menu.Item key='3'>{'删除'}</Menu.Item>
      </Menu>
    );
    return (rightClickNodeTreeItem == null) ? '' : menu;
  };

  const onRightClick = (e:any) => {
    setRightClickNodeTreeItem({
      pageX: e.event.pageX,
      pageY: e.event.pageY,
      id: e.node.id,
      categoryName: e.node.name
    });
  };

  const onSelect = (e:any) => {
    setRightClickNodeTreeItem(null);
    // setAutoExpandParent(!autoExpandParent);
  };

  return (
    <div className={style.tree_div}>
      <Search style={{marginBottom: 8}} placeholder="Search" onChange={onChange}/>
        <DirectoryTree
          /*onExpand={onExpand}
          expandedKeys={expandedKeys}
          autoExpandParent={autoExpandParent}*/
          // showIcon
          // showLine
          multiple
          onRightClick={onRightClick}
          onSelect={onSelect}
          // defaultExpandAll
          switcherIcon={<DownOutlined/>}
          treeData={treeData}
        />
      {getNodeTreeRightClickMenu()}
    </div>
  );
};


export default connect(({studio}: { studio: StateType }) => ({}))(StudioTree);

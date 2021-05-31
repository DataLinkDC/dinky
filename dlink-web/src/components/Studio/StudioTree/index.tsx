import React, {useEffect, useState} from "react";
import {connect} from "umi";
import  {DownOutlined, FrownFilled, FrownOutlined, MehOutlined, SmileOutlined} from "@ant-design/icons";
import {Tree, Input, Menu, Empty,Button} from 'antd';
import {getCatalogueTreeData} from "@/pages/FlinkSqlStudio/service";
import {convertToTreeData, DataType, TreeDataNode} from "@/components/Studio/StudioTree/Function";
import style from "./index.less";
import {StateType} from "@/pages/FlinkSqlStudio/model";

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
  const {currentPath,dispatch} = props;

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

  const onChange = (e:any) => {

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

  const getEmpty = () =>{
    const empty = (<Empty image={Empty.PRESENTED_IMAGE_SIMPLE} ><Button type="primary">创建目录</Button></Empty>);
    return (treeData&&treeData.length==0)?empty:'';
  };

  const onRightClick = (e:any) => {
    setRightClickNodeTreeItem({
      pageX: e.event.pageX,
      pageY: e.event.pageY,
      id: e.node.id,
      categoryName: e.node.name
    });
  };

  const onSelect = (selectedKeys:[], e:any) => {
    console.log(e.node.path);
    dispatch({
      type: "Studio/saveCurrentPath",
      payload: e.node.path,
    });
    setRightClickNodeTreeItem(null);
  };

  return (
    <div className={style.tree_div}>
      <Search style={{marginBottom: 8}} placeholder="Search" onChange={onChange}/>
        <DirectoryTree
          multiple
          onRightClick={onRightClick}
          onSelect={onSelect}
          switcherIcon={<DownOutlined/>}
          treeData={treeData}
        />
      {getNodeTreeRightClickMenu()}
      {getEmpty()}
    </div>
  );
};


export default connect(({Studio}: { Studio: StateType }) => ({
  currentPath:Studio.currentPath
}))(StudioTree);

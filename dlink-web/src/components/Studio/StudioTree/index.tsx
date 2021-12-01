import React, {useEffect, useRef, useState} from "react";
import {connect} from "umi";
import  {DownOutlined, SwitcherOutlined, FrownOutlined, MehOutlined, SmileOutlined,FolderAddOutlined} from "@ant-design/icons";
import {Tree, Input, Menu, Empty, Button, message, Modal,Tooltip,Row,Col} from 'antd';
import {getCatalogueTreeData} from "@/pages/FlinkSqlStudio/service";
import {convertToTreeData, DataType, getTreeNodeByKey, TreeDataNode} from "@/components/Studio/StudioTree/Function";
import style from "./index.less";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {
  getInfoById, handleAddOrUpdate, handleAddOrUpdateWithResult, handleInfo, handleRemove, handleRemoveById,
  handleSubmit
} from "@/components/Common/crud";
import UpdateCatalogueForm from './components/UpdateCatalogueForm';
import {ActionType} from "@ant-design/pro-table";
import UpdateTaskForm from "@/components/Studio/StudioTree/components/UpdateTaskForm";
import { Scrollbars } from 'react-custom-scrollbars';
const { DirectoryTree } = Tree;
const {Search} = Input;


type StudioTreeProps = {
  rightClickMenu:StateType['rightClickMenu'];
  dispatch:any;
  tabs:StateType['tabs'];
  current:StateType['current'];
  toolHeight:number;
  refs:any;
};

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
  const {rightClickMenu,dispatch,tabs,refs,toolHeight} = props;
  const [treeData, setTreeData] = useState<TreeDataNode[]>();
  const [dataList, setDataList] = useState<[]>();
  const [expandedKeys, setExpandedKeys] = useState<[]>();
  const [rightClickNodeTreeItem,setRightClickNodeTreeItem] = useState<RightClickMenu>();
  const [updateCatalogueModalVisible, handleUpdateCatalogueModalVisible] = useState<boolean>(false);
  const [updateTaskModalVisible, handleUpdateTaskModalVisible] = useState<boolean>(false);
  const [isCreate, setIsCreate] = useState<boolean>(true);
  const [catalogueFormValues, setCatalogueFormValues] = useState({});
  const [taskFormValues, setTaskFormValues] = useState({});
  const [rightClickNode, setRightClickNode] = useState<TreeDataNode>();
  const [available, setAvailable] = useState<boolean>(true);
  let sref = React.createRef<Scrollbars>();

  const getTreeData = async () => {
    const result = await getCatalogueTreeData();
    let data = result.datas;
    let list = data;
    for(let i=0;i<list.length;i++){
      list[i].title=list[i].name;
      list[i].key=list[i].id;
    }
    setDataList(list);
    data = convertToTreeData(data, 0);
    setTreeData(data);
  };

  const openByKey = async (key)=>{
    const result = await getCatalogueTreeData();
    let data = result.datas;
    let list = data;
    for(let i=0;i<list.length;i++){
      list[i].title=list[i].name;
      list[i].key=list[i].id;
    }
    setDataList(list);
    data = convertToTreeData(data, 0);
    setTreeData(data);
    let node = getTreeNodeByKey(data,key);
    onSelect([],{node:node});
  };

  useEffect(() => {
    getTreeData();
  }, []);


  const handleMenuClick=(key:string)=>{
    if(key=='Open'){
      toOpen(rightClickNode);
    }else if(key=='Submit'){
      toSubmit(rightClickNode);
    }else if(key=='CreateCatalogue'){
      createCatalogue(rightClickNode);
    }else if(key=='CreateRootCatalogue'){
      createRootCatalogue(rightClickNode);
    }else if(key=='CreateTask'){
      createTask(rightClickNode);
    }else if(key=='Rename'){
      toRename(rightClickNode);
    }else if(key=='Delete'){
      toDelete(rightClickNode);
    }
  };

  const toOpen=(node:TreeDataNode)=>{
    if(!available){return}
    setAvailable(false);
    setTimeout(()=>{
      setAvailable(true);
    },200);
    if(node.isLeaf&&node.taskId) {
      for(let item of tabs.panes){
        if(item.key==node.taskId){
          dispatch&&dispatch({
            type: "Studio/changeActiveKey",
            payload: node.taskId,
          });
          return;
        }
      }
      const result = getInfoById('/api/task',node.taskId);
      result.then(result=>{
        let newTabs = tabs;
        let newPane = {
          title: node.name,
          key: node.taskId,
          value:(result.datas.statement?result.datas.statement:''),
          closable: true,
          path: node.path,
          task:{
            session:'',
            maxRowNum: 100,
            jobName:node.name,
            useResult:false,
            useSession:false,
            useRemote:true,
            ...result.datas,
          },
          console:{
            result:[],
          },
          monaco: React.createRef(),
        };
        newTabs.activeKey = node.taskId;
        newTabs.panes.push(newPane);
        dispatch&&dispatch({
          type: "Studio/saveTabs",
          payload: newTabs,
        });
      })
    }
  };

  const createCatalogue=(node:TreeDataNode)=>{
    if(!node.isLeaf) {
      handleUpdateCatalogueModalVisible(true);
      setIsCreate(true);
      setCatalogueFormValues({
        isLeaf: false,
        parentId: node.id,
      });
      getTreeData();
    }else{
      message.error('只能在目录上创建目录');
    }
  };

  const createRootCatalogue=()=>{
    handleUpdateCatalogueModalVisible(true);
    setIsCreate(true);
    setCatalogueFormValues({
      isLeaf: false,
      parentId: 0,
    });
    getTreeData();
  };

  const toSubmit=(node:TreeDataNode)=>{
    Modal.confirm({
      title: '提交作业',
      content: '确定提交该作业到其配置的集群吗？',
      okText: '确认',
      cancelText: '取消',
      onOk:async () => {
        let task = {
          id:node.taskId,
        };
        setTimeout(()=>{
          refs?.history?.current?.reload();
        },2000);
        handleSubmit('/api/task/submit','作业',[task]);
      }
    });
  };

  const toRename=(node:TreeDataNode)=>{
    handleUpdateCatalogueModalVisible(true);
    setIsCreate(false);
    setCatalogueFormValues({
      id: node.id,
      name: node.name,
    });
    getTreeData();
  };

  const createTask=(node:TreeDataNode)=>{
    if(!node.isLeaf) {
      handleUpdateTaskModalVisible(true);
      setIsCreate(true);
      setTaskFormValues({
        parentId: node.id,
      });
      //getTreeData();
    }else{
      message.error('只能在目录上创建作业');
    }
  };

  const toDelete= (node:TreeDataNode)=>{
    let label = (node.taskId==null)?'目录':'作业';
    Modal.confirm({
      title: `删除${label}`,
      content: `确定删除该${label}吗？`,
      okText: '确认',
      cancelText: '取消',
      onOk:async () => {
        await handleRemoveById('/api/catalogue',node.id);
        if(node.taskId) {
          dispatch({
            type: "Studio/deleteTabByKey",
            payload: node.taskId,
          });
        }
        getTreeData();
      }
    });
  };

  const getNodeTreeRightClickMenu = () => {
    const {pageX, pageY} = {...rightClickNodeTreeItem};
    const tmpStyle = {
      position: 'absolute',
      // left: `${pageX - 50}px`,
      // top: `${pageY - 202}px`,
      left: `${pageX}px`,
      top: `${pageY - 120}px`,
    };
    let menuItems;
    if(rightClickNode&&rightClickNode.isLeaf){
      menuItems=(<>
        <Menu.Item key='Open'>{'打开'}</Menu.Item>
        <Menu.Item key='Submit'>{'异步提交'}</Menu.Item>
        <Menu.Item key='Rename'>{'重命名'}</Menu.Item>
        <Menu.Item key='Delete'>{'删除'}</Menu.Item>
      </>)
    }else if(rightClickNode&&rightClickNode.children&&rightClickNode.children.length>0){
      menuItems=(<>
        <Menu.Item key='CreateCatalogue'>{'创建目录'}</Menu.Item>
        <Menu.Item key='CreateRootCatalogue'>{'创建根目录'}</Menu.Item>
        <Menu.Item key='CreateTask'>{'创建作业'}</Menu.Item>
        <Menu.Item key='Rename'>{'重命名'}</Menu.Item>
        <Menu.Item disabled>{'删除'}</Menu.Item>
      </>)
    }else{
      menuItems=(<>
        <Menu.Item key='CreateCatalogue'>{'创建目录'}</Menu.Item>
        <Menu.Item key='CreateTask'>{'创建作业'}</Menu.Item>
        <Menu.Item key='Rename'>{'重命名'}</Menu.Item>
        <Menu.Item key='Delete'>{'删除'}</Menu.Item>
      </>)
    }
    const menu = (
      <Menu
        onClick={({key}) => handleMenuClick(key)}
        style={tmpStyle}
        className={style.right_click_menu}
      >
        {menuItems}
      </Menu>
    );
    return rightClickMenu? menu: '';
  };

  const getEmpty = () =>{
    const empty = (<Empty image={Empty.PRESENTED_IMAGE_SIMPLE} ><Button type="primary" onClick={() => {
      handleUpdateCatalogueModalVisible(true);
      setIsCreate(true);
      setCatalogueFormValues({
        isLeaf:false,
        parentId:0,
      });
    }}>创建目录</Button></Empty>);
    return (treeData&&treeData.length==0)?empty:'';
  };

  const handleContextMenu = (e: React.MouseEvent, node: TreeDataNode) => {
    console.log('top:',sref.current.getScrollTop());
    console.log('node:',node);
    console.log('e:',e.pageY);
    let position = e.currentTarget.getBoundingClientRect();
    console.log('p:',position);
    setRightClickNode(node);
    setRightClickNodeTreeItem({
      pageX: e.pageX,
      pageY: position.top+sref.current.getScrollTop(),
      id: node.id,
      categoryName: node.name
    });
    dispatch&&dispatch({
      type: "Studio/showRightClickMenu",
      payload: true,
    });
  };

  const onSelect = (selectedKeys:[], e:any) => {
    if(e.node&&e.node.isLeaf) {
      dispatch({
        type: "Studio/saveCurrentPath",
        payload: e.node.path,
      });
      toOpen(e.node);
    }
  };

  const offExpandAll = ()=>{
    setExpandedKeys([]);
  };

  const onExpand=(expandedKeys:[])=>{
    setExpandedKeys(expandedKeys);
  };

  return (
    <div className={style.tree_div} >
      <Row>
        <Col span={24}>
        <Tooltip title="创建根目录">
          <Button
          type="text"
          icon={<FolderAddOutlined />}
          onClick={createRootCatalogue}
          />
        </Tooltip>
        <Tooltip title="折叠目录">
          <Button
          type="text"
          icon={<SwitcherOutlined />}
          onClick={offExpandAll}
          />
        </Tooltip>
        </Col>
      </Row>
      <Scrollbars  style={{height:(toolHeight-32)}} ref={sref}>
      {/*<Search style={{marginBottom: 8}} placeholder="Search" onChange={onChange}/>*/}
        <DirectoryTree
          multiple
          onRightClick={({event, node}: any) => {
            handleContextMenu(event, node)
          }}
          onSelect={onSelect}
          switcherIcon={<DownOutlined/>}
          treeData={treeData}
          onExpand ={onExpand }
          expandedKeys={expandedKeys}
        />
      {getNodeTreeRightClickMenu()}
      {getEmpty()}
      {updateCatalogueModalVisible? (
        <UpdateCatalogueForm
          onSubmit={async (value) => {
            const success = await handleAddOrUpdate(
              isCreate?'/api/catalogue':'/api/catalogue/toRename',value);
            if (success) {
              handleUpdateCatalogueModalVisible(false);
              setCatalogueFormValues({});
              getTreeData()
            }
          }}
          onCancel={() => {
            handleUpdateCatalogueModalVisible(false);
            setCatalogueFormValues({});
          }}
          updateModalVisible={updateCatalogueModalVisible}
          values={catalogueFormValues}
          isCreate={isCreate}
        />
      ) : null}
      {updateTaskModalVisible? (
        <UpdateTaskForm
          onSubmit={async (value) => {
            const datas = await handleAddOrUpdateWithResult('/api/catalogue/createTask',value);
            if (datas) {
              handleUpdateTaskModalVisible(false);
              setTaskFormValues({});
              openByKey(datas.id);
              // getTreeData();
              // onSelect([],openByKey(datas.id));
            }
          }}
          onCancel={() => {
            handleUpdateTaskModalVisible(false);
            setTaskFormValues({});
          }}
          updateModalVisible={updateTaskModalVisible}
          values={taskFormValues}
          isCreate={isCreate}
        />
      ) : null}
      </Scrollbars>
    </div>
  );
};


export default connect(({Studio}: { Studio: StateType }) => ({
  currentPath:Studio.currentPath,
  tabs: Studio.tabs,
  rightClickMenu: Studio.rightClickMenu,
  refs: Studio.refs,
  toolHeight: Studio.toolHeight,
}))(StudioTree);

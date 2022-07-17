import React, {useEffect, useState, Key} from "react";
import {connect} from "umi";
import {DownOutlined, SwitcherOutlined, FolderAddOutlined} from "@ant-design/icons";
import {Tree, Menu, Empty, Button, message, Modal, Tooltip, Row, Col, Input} from 'antd';
import {getCatalogueTreeData} from "@/pages/DataStudio/service";
import {convertToTreeData, getTreeNodeByKey, TreeDataNode} from "@/components/Studio/StudioTree/Function";
import style from "./index.less";
import {StateType} from "@/pages/DataStudio/model";
import {
  getInfoById, handleAddOrUpdate, handleAddOrUpdateWithResult, handleOption, handleRemoveById, handleSubmit
} from "@/components/Common/crud";
import UpdateCatalogueForm from './components/UpdateCatalogueForm';
import SimpleTaskForm from "@/components/Studio/StudioTree/components/SimpleTaskForm";
import {Scrollbars} from "react-custom-scrollbars";
import {getIcon} from "@/components/Studio/icon";
import {showEnv, showMetaStoreCatalogs} from "@/components/Studio/StudioEvent/DDL";
import UploadModal from "@/components/Studio/StudioTree/components/UploadModal";

type StudioTreeProps = {
  rightClickMenu: StateType['rightClickMenu'];
  dispatch: any;
  tabs: StateType['tabs'];
  current: StateType['current'];
  toolHeight: number;
  refs: any;
};

type RightClickMenu = {
  pageX: number,
  pageY: number,
  id: number,
  categoryName: string
};

//将树形节点改为一维数组
const generateList = (data: any, list: any[]) => {
  for (let i = 0; i < data.length; i++) {
    const node = data[i];
    const {name, id, parentId, level} = node;
    list.push({name, id, key: id, title: name, parentId, level});
    if (node.children) {
      generateList(node.children, list);
    }
  }
  return list
};

// tree树 匹配方法
const getParentKey = (key: number | string, tree: any): any => {
  let parentKey;
  for (let i = 0; i < tree.length; i++) {
    const node = tree[i];
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

const {DirectoryTree} = Tree;
const {Search} = Input;

const StudioTree: React.FC<StudioTreeProps> = (props) => {
  const {rightClickMenu, dispatch, tabs, refs, toolHeight} = props;
  const [treeData, setTreeData] = useState<TreeDataNode[]>();
  const [expandedKeys, setExpandedKeys] = useState<Key[]>();
  const [defaultExpandedKeys, setDefaultExpandedKeys] = useState<any[]>([]);
  const [rightClickNodeTreeItem, setRightClickNodeTreeItem] = useState<RightClickMenu>();
  const [updateCatalogueModalVisible, handleUpdateCatalogueModalVisible] = useState<boolean>(false);
  const [updateTaskModalVisible, handleUpdateTaskModalVisible] = useState<boolean>(false);
  const [isCreate, setIsCreate] = useState<boolean>(true);
  const [catalogueFormValues, setCatalogueFormValues] = useState({});
  const [taskFormValues, setTaskFormValues] = useState({});
  const [activeNode, setActiveNode] = useState({});
  const [rightClickNode, setRightClickNode] = useState<TreeDataNode>();
  const [available, setAvailable] = useState<boolean>(true);
  const [isUploadModalVisible, setIsUploadModalVisible] = useState(false);
  const [uploadNodeId, setUploadNodeId] = useState(0);
  const sref: any = React.createRef<Scrollbars>();
  const [searchValue, setSearchValue] = useState('');
  const [autoExpandParent, setAutoExpandParent] = useState(true);
  const [cutId, setCutId] = useState<number | undefined>(undefined);

  const getTreeData = async () => {
    const result = await getCatalogueTreeData();
    let data = result.datas;
    let list = data;
    let expendList: any[] = [];
    list?.map((v: any) => {
      expendList.push(v.id);
      if (v.children) {
        v?.children?.map((item: any) => {
          expendList.push(item.id);
        })
      }
    });
    data = convertToTreeData(list, 0);
    setTreeData(data);
    //默认展开所有
    setExpandedKeys(expendList || []);
    setDefaultExpandedKeys(expendList || []);
  };

  const onChange = (e: any) => {
    let {value} = e.target;
    if (!value) {
      setExpandedKeys(defaultExpandedKeys);
      setSearchValue(value);
      return
    }
    value = String(value).trim();
    const expandList: any[] = generateList(treeData, []);
    let expandedKeys: any = expandList.map((item: any) => {
      if (item && item.name.indexOf(value) > -1) {
        let key = getParentKey(item.key, treeData);
        return key;
      }
      return null;
    })
      .filter((item: any, i: number, self: any) => item && self.indexOf(item) === i)
    setExpandedKeys(expandedKeys)
    setSearchValue(value)
    setAutoExpandParent(true)
  }

  const openByKey = async (key: any) => {
    const result = await getCatalogueTreeData();
    let data = result.datas;
    let list = data;
    for (let i = 0; i < list.length; i++) {
      list[i].title = list[i].name;
      list[i].key = list[i].id;
      if (list[i].isLeaf) {
        list[i].icon = getIcon(list[i].type);
      }
    }
    data = convertToTreeData(list, 0);
    setTreeData(data);
    let node = getTreeNodeByKey(data, key);
    onSelect([], {node: node});
  };

  useEffect(() => {
    getTreeData();
  }, []);


  const handleMenuClick = (key: string) => {
    if (key == 'Open') {
      toOpen(rightClickNode);
    } else if (key == 'Submit') {
      toSubmit(rightClickNode);
    } else if (key == 'CreateCatalogue') {
      createCatalogue(rightClickNode);
    } else if (key == 'CreateRootCatalogue') {
      createRootCatalogue();
    } else if (key == 'ShowUploadModal') {
      showUploadModal(rightClickNode);
    } else if (key == 'CreateTask') {
      createTask(rightClickNode);
    } else if (key == 'Rename') {
      toRename(rightClickNode);
    } else if (key == 'Delete') {
      toDelete(rightClickNode);
    } else if (key == 'Cut') {
      toCut(rightClickNode);
    } else if (key == 'Paste') {
      toPaste(rightClickNode);
    } else if (key == 'Copy') {
      toCopy(rightClickNode);
    }
  };

  const showUploadModal = (node: TreeDataNode | undefined) => {
    if (node == undefined) return;
    setUploadNodeId(node.id);
    setIsUploadModalVisible(true);
  };

  const toOpen = (node: TreeDataNode | undefined) => {
    if (!available) {
      return
    }
    setAvailable(false);
    setTimeout(() => {
      setAvailable(true);
    }, 200);

    if (node?.isLeaf && node.taskId) {
      for (let item of tabs.panes) {
        if (item.key == node.taskId) {
          dispatch && dispatch({
            type: "Studio/saveToolHeight",
            payload: toolHeight - 0.0001,
          });
          dispatch && dispatch({
            type: "Studio/changeActiveKey",
            payload: node.taskId,
          });
          return;
        }
      }
      const result = getInfoById('/api/task', node.taskId);
      result.then(result => {
        let newTabs = tabs;
        let newPane: any = {
          title: <>{node!.icon} {node!.name}</>,
          key: node!.taskId,
          value: (result.datas.statement ? result.datas.statement : ''),
          closable: true,
          path: node!.path,
          task: {
            session: '',
            maxRowNum: 100,
            jobName: node!.name,
            useResult: true,
            useChangeLog: false,
            useAutoCancel: false,
            useSession: false,
            useRemote: true,
            ...result.datas,
          },
          console: {
            result: {},
            chart: {},
          },
          monaco: React.createRef(),
          metaStore: []
        };
        newTabs!.activeKey = node!.taskId;
        newTabs!.panes!.push(newPane);
        dispatch && dispatch({
          type: "Studio/saveTabs",
          payload: newTabs,
        });
        showMetaStoreCatalogs(result.datas, dispatch);
      })
    }
  };

  const createCatalogue = (node: TreeDataNode | undefined) => {
    if (!node?.isLeaf) {
      handleUpdateCatalogueModalVisible(true);
      setIsCreate(true);
      setCatalogueFormValues({
        isLeaf: false,
        parentId: node?.id,
      });
    } else {
      message.error('只能在目录上创建目录');
    }
  };

  const createRootCatalogue = () => {
    handleUpdateCatalogueModalVisible(true);
    setIsCreate(true);
    setCatalogueFormValues({
      isLeaf: false,
      parentId: 0,
    });
  };

  const toSubmit = (node: TreeDataNode | undefined) => {
    Modal.confirm({
      title: '提交作业',
      content: '确定提交该作业到其配置的集群吗？',
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        let task = {
          id: node?.taskId,
        };
        setTimeout(() => {
          refs?.history?.current?.reload();
        }, 2000);
        handleSubmit('/api/task/submit', '作业', [task]);
      }
    });
  };

  const toRename = (node: TreeDataNode) => {
    handleUpdateCatalogueModalVisible(true);
    setIsCreate(false);
    setActiveNode(node);
    setCatalogueFormValues({
      id: node?.id,
      name: node?.name,
    });
  };

  const toCut = (node: TreeDataNode | undefined) => {
    setCutId(node?.id);
    message.success('剪切成功');
  };

  const toPaste = async (node: TreeDataNode | undefined) => {
    if (cutId == 0) {
      return;
    }
    const datas = await handleAddOrUpdateWithResult('/api/catalogue/moveCatalogue', {id: cutId, parentId: node?.id});
    if (datas) {
      setCutId(undefined);
      getTreeData();
    }
  };

  const toCopy = async (node: TreeDataNode | undefined) => {
    let catalogues = {
      taskId: node?.taskId,
      parentId: node?.id
    };
    const datas = await handleOption('/api/catalogue/copyTask', "复制作业", catalogues);

    if (datas) {
      getTreeData();
    }
  };

  const createTask = (node: TreeDataNode | undefined) => {
    if (!node?.isLeaf) {
      handleUpdateTaskModalVisible(true);
      setIsCreate(true);
      setTaskFormValues({
        parentId: node?.id,
      });
    } else {
      message.error('只能在目录上创建作业');
    }
  };

  const toDelete = (node: TreeDataNode | undefined) => {
    let label = (node?.taskId == null) ? '目录' : '作业';
    Modal.confirm({
      title: `删除${label}`,
      content: `确定删除该${label}【${node?.name}】吗？`,
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        await handleRemoveById('/api/catalogue', node!.id);
        if (node?.taskId) {
          dispatch({
            type: "Studio/deleteTabByKey",
            payload: node?.taskId,
          });
        }
        getTreeData();
      }
    });
  };

  const getNodeTreeRightClickMenu = () => {
    const {pageX, pageY} = {...rightClickNodeTreeItem};
    const tmpStyle: any = {
      position: 'absolute',
      left: pageX,
      top: pageY,
    };
    let menuItems;
    if (rightClickNode && rightClickNode.isLeaf) {
      menuItems = (<>
        <Menu.Item key='Open'>{'打开'}</Menu.Item>
        <Menu.Item key='Submit'>{'异步提交'}</Menu.Item>
        <Menu.Item key='Rename'>{'重命名'}</Menu.Item>
        <Menu.Item key='Copy'>{'复制'}</Menu.Item>
        <Menu.Item key='Cut'>{'剪切'}</Menu.Item>
        {cutId && <Menu.Item key='Paste'>{'粘贴'}</Menu.Item>}
        <Menu.Item key='Delete'>{'删除'}</Menu.Item>
      </>)
    } else if (rightClickNode && rightClickNode.children && rightClickNode.children.length > 0) {
      menuItems = (<>
        <Menu.Item key='CreateCatalogue'>{'创建目录'}</Menu.Item>
        <Menu.Item key='CreateRootCatalogue'>{'创建根目录'}</Menu.Item>
        <Menu.Item key='ShowUploadModal'>{'上传zip包创建工程'}</Menu.Item>
        <Menu.Item key='CreateTask'>{'创建作业'}</Menu.Item>
        <Menu.Item key='Rename'>{'重命名'}</Menu.Item>
        <Menu.Item key='Copy'>{'复制'}</Menu.Item>
        <Menu.Item key='Cut'>{'剪切'}</Menu.Item>
        {cutId && <Menu.Item key='Paste'>{'粘贴'}</Menu.Item>}
        <Menu.Item disabled>{'删除'}</Menu.Item>
      </>)
    } else {
      menuItems = (<>
        <Menu.Item key='CreateCatalogue'>{'创建目录'}</Menu.Item>
        <Menu.Item key='CreateTask'>{'创建作业'}</Menu.Item>
        <Menu.Item key='Rename'>{'重命名'}</Menu.Item>
        <Menu.Item key='Copy'>{'复制'}</Menu.Item>
        <Menu.Item key='Cut'>{'剪切'}</Menu.Item>
        {cutId && <Menu.Item key='Paste'>{'粘贴'}</Menu.Item>}
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
    return rightClickMenu ? menu : '';
  };

  const getEmpty = () => {
    const empty = (<Empty image={Empty.PRESENTED_IMAGE_SIMPLE}><Button type="primary" onClick={() => {
      handleUpdateCatalogueModalVisible(true);
      setIsCreate(true);
      setCatalogueFormValues({
        isLeaf: false,
        parentId: 0,
      });
    }}>创建目录</Button></Empty>);
    return (treeData && treeData.length == 0) ? empty : '';
  };

  const handleContextMenu = (e: any) => {
    let position = e.event.currentTarget.getBoundingClientRect();
    let scrollTop = document.documentElement.scrollTop;
    setRightClickNode(e.node);
    setRightClickNodeTreeItem({
      pageX: e.event.pageX - 40,
      pageY: position.y + sref.current.getScrollTop() + scrollTop - 145 - position.height,
      id: e.node.id,
      categoryName: e.node.name
    });
    dispatch && dispatch({
      type: "Studio/showRightClickMenu",
      payload: true,
    });
  };

  //选中节点时触发
  const onSelect = (selectedKeys: Key[], e: any) => {
    if (e.node && e.node.isLeaf) {
      dispatch({
        type: "Studio/saveCurrentPath",
        payload: e.node.path,
      });
      toOpen(e.node);
    }
  };

  const offExpandAll = () => {
    setExpandedKeys([]);
  };

  // 树节点展开/收缩
  const onExpand = (expandedKeys: Key[]) => {
    setExpandedKeys(expandedKeys);
    setAutoExpandParent(false)
  };

  const loop = (data: any) =>
    data?.map((item: any) => {
      const index = item.title.indexOf(searchValue);
      const beforeStr = item.title.substr(0, index);
      const afterStr = item.title.substr(index + searchValue.length);
      item.icon = getIcon(item.type);
      const title =
        index > -1 ? (
          <span>
            {beforeStr}
            <span className={style['site-tree-search-value']}>{searchValue}</span>
            {afterStr}
            </span>
        ) : (
          <span>{item.title}</span>
        );
      if (item.children) {
        return {
          isLeaf: item.isLeaf,
          name: item.name,
          id: item.id,
          taskId: item.taskId,
          parentId: item.parentId,
          path: item.path,
          icon: item.isLeaf ? item.icon : '',
          title,
          key: item.key,
          children: loop(item.children)
        };
      }
      return {
        isLeaf: item.isLeaf,
        name: item.name,
        id: item.id,
        taskId: item.taskId,
        parentId: item.parentId,
        path: item.path,
        icon: item.isLeaf ? item.icon : '',
        title,
        key: item.key,
      };
    });

  return (
    <div className={style.tree_div}>
      <Row>
        <Col span={24}>
          <Tooltip title="创建根目录">
            <Button
              type="text"
              icon={<FolderAddOutlined/>}
              onClick={createRootCatalogue}
            />
          </Tooltip>
          <Tooltip title="折叠目录">
            <Button
              type="text"
              icon={<SwitcherOutlined/>}
              onClick={offExpandAll}
            />
          </Tooltip>
        </Col>
      </Row>
      <Search style={{marginBottom: 8}} placeholder="Search" onChange={onChange} allowClear={true}/>
      <Scrollbars style={{height: (toolHeight - 72)}} ref={sref}>
        <DirectoryTree
          multiple
          onRightClick={handleContextMenu}
          onSelect={onSelect}
          switcherIcon={<DownOutlined/>}
          treeData={loop(treeData)}
          onExpand={onExpand}
          autoExpandParent={autoExpandParent}
          defaultExpandAll
          expandedKeys={expandedKeys}
        />
        {getNodeTreeRightClickMenu()}
        {getEmpty()}
        {updateCatalogueModalVisible ? (
          <UpdateCatalogueForm
            onSubmit={async (value) => {
              const success = await handleAddOrUpdate(
                isCreate ? '/api/catalogue' : '/api/catalogue/toRename', value);
              if (success) {
                handleUpdateCatalogueModalVisible(false);
                setCatalogueFormValues({});
                getTreeData();
                dispatch({
                  type: "Studio/renameTab",
                  payload: {
                    key: value.id,
                    name: <>{activeNode.icon} {value.name}</>
                  },
                });
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
        {updateTaskModalVisible ? (
          <SimpleTaskForm
            onSubmit={async (value) => {
              const datas = await handleAddOrUpdateWithResult('/api/catalogue/createTask', value);
              if (datas) {
                handleUpdateTaskModalVisible(false);
                setTaskFormValues({});
                openByKey(datas.id);
                showEnv(dispatch);
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
      <UploadModal visible={isUploadModalVisible} action={`/api/catalogue/upload/${uploadNodeId}`} handleOk={() => {
        setIsUploadModalVisible(false);
        setExpandedKeys(defaultExpandedKeys);
        getTreeData();
      }} onCancel={() => {
        setIsUploadModalVisible(false)
      }} buttonTitle="上传zip包并创建工程"/>
    </div>
  );
};


export default connect(({Studio}: { Studio: StateType }) => ({
  currentPath: Studio.currentPath,
  tabs: Studio.tabs,
  rightClickMenu: Studio.rightClickMenu,
  refs: Studio.refs,
  toolHeight: Studio.toolHeight,
}))(StudioTree);

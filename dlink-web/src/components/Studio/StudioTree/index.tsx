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


import React, {Key, useEffect, useState} from "react";
import {connect} from "umi";
import {DownloadOutlined, DownOutlined, FolderAddOutlined, SwitcherOutlined, UploadOutlined} from "@ant-design/icons";
import type {UploadProps} from 'antd';
import {Button, Col, Empty, Input, Menu, message, Modal, Row, Tooltip, Tree, Upload} from 'antd';
import {getCatalogueTreeData} from "@/pages/DataStudio/service";
import {convertToTreeData, getTreeNodeByKey, TreeDataNode} from "@/components/Studio/StudioTree/Function";
import style from "./index.less";
import {StateType} from "@/pages/DataStudio/model";
import {
  CODE,
  getInfoById,
  handleAddOrUpdate,
  handleAddOrUpdateWithResult,
  handleData,
  handleOption,
  handleRemoveById,
  handleSubmit,
  postAll,
} from "@/components/Common/crud";
import UpdateCatalogueForm from './components/UpdateCatalogueForm';
import SimpleTaskForm from "@/components/Studio/StudioTree/components/SimpleTaskForm";
import {Scrollbars} from "react-custom-scrollbars";
import {getIcon} from "@/components/Studio/icon";
import {showEnv, showMetaStoreCatalogs} from "@/components/Studio/StudioEvent/DDL";
import UploadModal from "@/components/Studio/StudioTree/components/UploadModal";
import {l} from "@/utils/intl";

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

// tree树 匹配方法
const getParentKey = (key: number | string, tree: any): any => {
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
  const [exportTaskIds, setExportTaskIds] = useState<any[]>([]);

  const getTreeData = async () => {
    const result = await getCatalogueTreeData();
    let data = result.datas;
    setTreeData(convertToTreeData(data, 0));
    //默认展开所有
    setExpandedKeys([]);
    setDefaultExpandedKeys([]);
    setExportTaskIds([]);
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
        return getParentKey(item.key, treeData);
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
    for (const element of list) {
      element.title = element.name;
      element.key = element.id;
      if (element.isLeaf) {
        element.icon = getIcon(element.type);
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
    } else if (key == 'ExportJson') {
      toExportJson(rightClickNode);
    }
  };

  const showUploadModal = (node: TreeDataNode | undefined) => {
    if (node == undefined) return;
    setUploadNodeId(node.id);
    setIsUploadModalVisible(true);
  };

  const activeTabCall = (node: TreeDataNode) => {
    dispatch && dispatch({
      type: "Studio/saveToolHeight",
      payload: toolHeight - 0.0001,
    });
    dispatch && dispatch({
      type: "Studio/changeActiveKey",
      payload: node.taskId,
    });
  };

  const checkInPans = (node: TreeDataNode) => {
    for (let item of tabs?.panes!) {
      if (item.key == node.taskId) {
        return true;
      }
    }
    return false;
  }

  const toOpen = (node: TreeDataNode | undefined) => {
    if (!available) {
      return
    }

    setAvailable(false);
    setTimeout(() => {
      setAvailable(true);
    }, 200);

    if (node?.isLeaf && node.taskId) {
      if(checkInPans(node)) {
        activeTabCall(node);
        return;
      }

      const result = getInfoById('/api/task', node.taskId);
      result.then(result => {
        let newTabs = tabs;
        let newPane: any = {
          title: node.name,
          key: node.taskId,
          value: (result.datas.statement ? result.datas.statement : ''),
          icon: node.icon,
          closable: true,
          path: node.path,
          task: {
            session: '',
            maxRowNum: 100,
            jobName: node.name,
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
        newTabs!.activeKey = node.taskId;
        if (checkInPans(node)) {
          return;
        }

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
      okText: l('button.confirm'),
      cancelText: l('button.cancel'),
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
      taskId: node?.taskId,
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

  const toExportJson = async (node: TreeDataNode | undefined) => {
    let taskId = node?.taskId;
    const datas = await handleData('/api/task/exportJsonByTaskId', {id: taskId});
    if (datas) {
      let data = JSON.parse(datas);
      saveJSON(data, data.alias);
      message.success('导出json成功');
    }
  };

  const toExportSelectedTaskJson = async () => {
    if (exportTaskIds.length <= 0) {
      message.warn("请先选择要导出的作业");
    } else {
      try {
        const {code, datas, msg} = await postAll('/api/task/exportJsonByTaskIds', {taskIds: exportTaskIds});
        if (code == CODE.SUCCESS) {
          saveJSON(datas);
          message.success('导出json成功');
        } else {
          message.warn(msg);
        }
      } catch (error) {
        message.error('获取失败，请重试');
      }
    }
  }

  const saveJSON = (data: any, filename?: any) => {
    if (!data) {
      message.error("保存的json数据为空");
      return;
    }
    if (!filename)
      filename = new Date().toLocaleDateString().replaceAll("/", "-");
    if (typeof data === 'object') {
      data = JSON.stringify(data, undefined, 4)
    }
    let blob = new Blob([data], {type: 'text/json'}),
      e = document.createEvent('MouseEvents'),
      a = document.createElement('a')
    a.download = filename + '.json'
    a.href = window.URL.createObjectURL(blob)
    a.dataset.downloadurl = ['text/json', a.download, a.href].join(':')
    e.initMouseEvent('click', true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null)
    a.dispatchEvent(e)
  }

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
      okText: l('button.confirm'),
      cancelText: l('button.cancel'),
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
      position: 'fixed',
      left: pageX,
      top: pageY,
    };
    let menuItems;
    if (rightClickNode && rightClickNode.isLeaf) {
      menuItems = (<>
        <Menu.Item key='Open'>{l('right.menu.open')}</Menu.Item>
        <Menu.Item key='Submit'>{l('right.menu.submit')}</Menu.Item>
        <Menu.Item key='ExportJson'>{l('right.menu.exportJson')}</Menu.Item>
        <Menu.Item key='Rename'>{l('right.menu.rename')}</Menu.Item>
        <Menu.Item key='Copy'>{l('right.menu.copy')}</Menu.Item>
        <Menu.Item key='Cut'>{l('right.menu.cut')}</Menu.Item>
        {cutId && <Menu.Item key='Paste'>{l('right.menu.paste')}</Menu.Item>}
        <Menu.Item key='Delete'>{l('right.menu.delete')}</Menu.Item>
      </>)
    } else if (rightClickNode && rightClickNode.children && rightClickNode.children.length > 0) {
      menuItems = (<>
        <Menu.Item key='CreateCatalogue'>{l('right.menu.createCatalogue')}</Menu.Item>
        <Menu.Item key='CreateRootCatalogue'>{l('right.menu.createRootCatalogue')}</Menu.Item>
        <Menu.Item key='ShowUploadModal'>{l('right.menu.uploadZipToCreate')}</Menu.Item>
        <Menu.Item key='CreateTask'>{l('right.menu.createTask')}</Menu.Item>
        <Menu.Item key='Rename'>{l('right.menu.rename')}</Menu.Item>
        <Menu.Item key='Copy'>{l('right.menu.copy')}</Menu.Item>
        <Menu.Item key='Cut'>{l('right.menu.cut')}</Menu.Item>
        {cutId && <Menu.Item key='Paste'>{l('right.menu.paste')}</Menu.Item>}
        <Menu.Item key='Delete'>{l('right.menu.delete')}</Menu.Item>
      </>)
    } else {
      menuItems = (<>
        <Menu.Item key='CreateCatalogue'>{l('right.menu.createCatalogue')}</Menu.Item>
        <Menu.Item key='CreateTask'>{l('right.menu.createTask')}</Menu.Item>
        <Menu.Item key='Rename'>{l('right.menu.rename')}</Menu.Item>
        <Menu.Item key='Copy'>{l('right.menu.copy')}</Menu.Item>
        <Menu.Item key='Cut'>{l('right.menu.cut')}</Menu.Item>
        {cutId && <Menu.Item key='Paste'>{l('right.menu.paste')}</Menu.Item>}
        <Menu.Item key='Delete'>{l('right.menu.delete')}</Menu.Item>
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
    }}>{l('button.createDir')}</Button></Empty>);
    return (treeData && treeData.length == 0) ? empty : '';
  };

  const handleContextMenu = (e: any) => {
    setRightClickNode(e.node);
    setRightClickNodeTreeItem({
      pageX: e.event.pageX,
      pageY: e.event.pageY,
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
    let taskIds = [];
    for (let i = 0; i < e.selectedNodes?.length; i++) {
      if (e.selectedNodes[i].isLeaf) {
        taskIds.push(e.selectedNodes[i].taskId);
      }
    }
    setExportTaskIds(taskIds);
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

  const uProps: UploadProps = {
    name: 'file',
    action: '/api/task/uploadTaskJson',
    accept: 'application/json',
    headers: {
      authorization: 'authorization-text',
    },
    showUploadList: false,
    onChange(info) {
      if (info.file.status === 'done') {
        if (info.file.response.code == CODE.SUCCESS) {
          message.success(info.file.response.msg);
        } else {
          message.warn(info.file.response.msg);
        }
        getTreeData();
      } else if (info.file.status === 'error') {
        message.error(`${info.file.name}`+ l('app.request.upload.failed'));
      }
    },
  };

  return (
    <div className={style.tree_div}>
      <Row>
        <Col span={24}>
          <Tooltip title={l('right.menu.createRootCatalogue')}>
            <Button
              type="text"
              icon={<FolderAddOutlined/>}
              onClick={createRootCatalogue}
            />
          </Tooltip>
          <Tooltip title={l('button.collapseDir')}>
            <Button
              type="text"
              icon={<SwitcherOutlined/>}
              onClick={offExpandAll}
            />
          </Tooltip>
          <Tooltip title={l('right.menu.exportJson')}>
            <Button
              type="text"
              icon={<DownloadOutlined/>}
              onClick={toExportSelectedTaskJson}
            />
          </Tooltip>
          <Upload {...uProps}>
            <Tooltip title={l('right.menu.importJson')}>
              <Button
                type="text"
                icon={<UploadOutlined/>}
              />
            </Tooltip>
          </Upload>
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
          // defaultExpandAll
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
                if (value.taskId) {
                  dispatch({
                    type: "Studio/renameTab",
                    payload: {
                      key: value.taskId,
                      title: value.name,
                      icon: activeNode.icon
                    },
                  });
                }
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
      }} buttonTitle={l('right.menu.uploadZipToCreate')}/>
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

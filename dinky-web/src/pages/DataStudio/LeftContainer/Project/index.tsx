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

import RightContextMenu from '@/components/RightContextMenu';
import { getTabByTaskId } from '@/pages/DataStudio/function';
import { useTasksDispatch } from '@/pages/DataStudio/LeftContainer/BtnContext';
import {
  FOLDER_RIGHT_MENU,
  JOB_RIGHT_MENU
} from '@/pages/DataStudio/LeftContainer/Project/constants';
import FolderModal from '@/pages/DataStudio/LeftContainer/Project/FolderModal';
import { getRightSelectKeyFromNodeClickJobType } from '@/pages/DataStudio/LeftContainer/Project/function';
import JobModal from '@/pages/DataStudio/LeftContainer/Project/JobModal';
import JobTree from '@/pages/DataStudio/LeftContainer/Project/JobTree';
import {
  DataStudioParams,
  DataStudioTabsItemType,
  StateType,
  STUDIO_MODEL,
  STUDIO_MODEL_ASYNC
} from '@/pages/DataStudio/model';
import {
  handleAddOrUpdate,
  handleOption,
  handlePutDataByParams,
  handleRemoveById
} from '@/services/BusinessCrud';
import { DIALECT } from '@/services/constants';
import { API_CONSTANTS } from '@/services/endpoints';
import { Catalogue } from '@/types/Studio/data.d';
import { InitProjectState } from '@/types/Studio/init.d';
import { ProjectState } from '@/types/Studio/state.d';
import { l } from '@/utils/intl';
import { Modal, Typography } from 'antd';
import { MenuInfo } from 'rc-menu/es/interface';
import React, { Key, useEffect, useState } from 'react';
import { connect } from 'umi';

const { Text } = Typography;

const Project: React.FC = (props: connect) => {
  const {
    dispatch,
    project: { expandKeys, selectKey },
    tabs: { panes, activeKey },
    tabs
  } = props;

  const [projectState, setProjectState] = useState<ProjectState>(InitProjectState);
  const btnDispatch = useTasksDispatch();

  useEffect(() => {
    setProjectState((prevState) => ({
      ...prevState,
      menuItems: prevState.isLeaf
        ? JOB_RIGHT_MENU(prevState.isCut && prevState.cutId !== undefined)
        : FOLDER_RIGHT_MENU(prevState.isCut && prevState.cutId !== undefined)
    }));
  }, [projectState.isCut, projectState.cutId]);

  /**
   * the right click event
   * @param info
   */
  const handleRightClick = (info: any) => {
    const {
      node: { isLeaf, key, fullInfo },
      node,
      event
    } = info;

    // 判断右键的位置是否超出屏幕 , 如果超出屏幕则设置为屏幕的最大值 往上偏移 200px (需要根据具体的右键菜单数量合理设置)
    if (event.clientY + 150 > window.innerHeight) {
      event.clientY = window.innerHeight - 200;
    }

    // 设置右键菜单
    setProjectState((prevState) => ({
      ...prevState,
      isLeaf: isLeaf,
      menuItems: isLeaf
        ? JOB_RIGHT_MENU(prevState.isCut && prevState.cutId !== undefined)
        : FOLDER_RIGHT_MENU(prevState.isCut && prevState.cutId !== undefined),
      contextMenuOpen: true,
      contextMenuPosition: {
        ...prevState.contextMenuPosition,
        top: event.clientY + 5,
        left: event.clientX + 10,
        screenX: event.screenX,
        screenY: event.screenY
      },
      selectedKeys: [key],
      rightClickedNode: { ...node, ...fullInfo },
      value: fullInfo
    }));
    // 设置选中的值
    dispatch({ type: STUDIO_MODEL.updateProjectSelectKey, payload: [key] });
  };

  const onExpand = (expandedKeys: Key[]) => {
    dispatch({
      type: STUDIO_MODEL.updateProjectExpandKey,
      payload: expandedKeys
    });
  };

  /**
   * click the node event
   * @param info
   */
  const onNodeClick = (info: any) => {
    // 选中的key
    const {
      node: { isLeaf, name, type, parentId, path, key, taskId }
    } = info;
    setProjectState((prevState) => ({
      ...prevState,
      selectedKeys: [key],
      contextMenuOpen: false
    }));
    dispatch({ type: STUDIO_MODEL.updateProjectSelectKey, payload: [key] });

    if (!isLeaf) {
      dispatch({ type: STUDIO_MODEL.updateProjectExpandKey, payload: [...expandKeys, key] });
      return;
    } else {
      dispatch({
        type: STUDIO_MODEL.updateSelectRightKey,
        payload: getRightSelectKeyFromNodeClickJobType(type)
      });
    }

    dispatch({
      type: STUDIO_MODEL.addTab,
      payload: {
        icon: type,
        id: parentId + name,
        treeKey: key,
        breadcrumbLabel: path.slice(0, path.length - 1).join('/'),
        label: name,
        params: {
          taskId: taskId
        },
        type: 'project',
        console: {},
        subType: type
      }
    });
  };

  const handleContextCancel = () => {
    setProjectState((prevState) => ({
      ...prevState,
      contextMenuOpen: false
    }));
  };

  /**
   * create or update sub folder
   */
  const handleCreateSubFolder = () => {
    setProjectState((prevState) => ({ ...prevState, isCreateSub: true }));
    handleContextCancel();
  };

  /**
   * 创建目录, 并刷新目录树
   */
  const handleSubmit = async (values: Catalogue) => {
    const options = {
      url: '',
      isLeaf: projectState.isCreateSub ? false : projectState.value.isLeaf,
      parentId: projectState.isCreateSub ? selectKey[0] : projectState.value.parentId
    };

    // 如果是编辑任务 或者 创建任务 , 则需要传入父级id
    if (projectState.rightActiveKey === 'createTask' || projectState.rightActiveKey === 'edit') {
      options.url = API_CONSTANTS.SAVE_OR_UPDATE_TASK_URL;
      options.parentId = projectState.isCreateTask
        ? projectState.value.id
        : projectState.isEdit
        ? projectState.value.parentId
        : options.parentId;
    } else {
      options.url = API_CONSTANTS.SAVE_OR_UPDATE_CATALOGUE_URL;
    }

    await handleAddOrUpdate(
      options.url,
      {
        ...values,
        isLeaf: options.isLeaf,
        parentId: options.parentId
      },
      () => {},
      () => {
        dispatch({ type: STUDIO_MODEL_ASYNC.queryProject });
        if (values.type && values.type.toLowerCase() === DIALECT.FLINKSQLENV) {
          dispatch({ type: STUDIO_MODEL_ASYNC.queryEnv });
        }
        if (projectState.isEdit) {
          const { id } = values;
          const currentTabs = getTabByTaskId(panes, id);
          if (currentTabs) {
            currentTabs.label = values.name;
            // currentTabs.params.taskData.name = values.name;
            const { params } = currentTabs;
            const { taskData } = params as DataStudioParams;
            if (taskData) {
              taskData.name = values.name;
            }
          }
          dispatch({ type: STUDIO_MODEL.saveTabs, payload: { ...props.tabs } });
          // update active breadcrumb title
          if (activeKey === currentTabs?.key) {
            dispatch({ type: STUDIO_MODEL.updateTabsActiveKey, payload: activeKey });
          }
        }
        // close job modal by project state
        setProjectState((prevState) => ({
          ...prevState,
          isCreateSub: false,
          isRename: false,
          isEdit: false,
          isCreateTask: false,
          isCut: false
        }));
      }
    );
  };

  /**
   * 删除目录, 并刷新目录树
   */
  const handleDeleteSubmit = async () => {
    const { key, isLeaf, name, type } = projectState.rightClickedNode;
    const { taskId, task } = projectState.value;

    handleContextCancel();
    if (!isLeaf) {
      await handleRemoveById(API_CONSTANTS.DELETE_CATALOGUE_BY_ID_URL, key, () => {
        dispatch({ type: STUDIO_MODEL_ASYNC.queryProject });
      });
      return;
    }

    Modal.confirm({
      title: l('datastudio.project.delete.job', '', { type, name }),
      width: '30%',
      content: (
        <Text className={'needWrap'} type='danger'>
          {l('datastudio.project.delete.job.confirm')}
        </Text>
      ),
      okText: l('button.confirm'),
      cancelText: l('button.cancel'),
      onOk: async () => {
        await handleRemoveById(API_CONSTANTS.DELETE_CATALOGUE_BY_ID_URL, key, () => {
          const currentTabs = getTabByTaskId(panes, key) as DataStudioTabsItemType;
          dispatch({ type: STUDIO_MODEL.removeTag, payload: taskId });
          const previousTabs = panes[panes.length > 1 ? panes.length - 1 : 0];
          const { key: currentKey } = currentTabs;
          if (currentKey === activeKey && panes.length >= 1) {
            dispatch({ type: STUDIO_MODEL.updateTabsActiveKey, payload: previousTabs?.key });
          }
          if (panes.length === 0) {
            dispatch({ type: STUDIO_MODEL.updateTabsActiveKey, payload: '' });
            dispatch({ type: STUDIO_MODEL.updateActiveBreadcrumbTitle, payload: '' });
          }
        });
        dispatch({ type: STUDIO_MODEL_ASYNC.queryProject });
      }
    });
  };

  /**
   * rename handle
   */
  const handleRename = async () => {
    setProjectState((prevState) => ({ ...prevState, isRename: true }));
    handleContextCancel();
  };

  /**
   * create task handle
   */
  const handleCreateTask = () => {
    setProjectState((prevState) => ({ ...prevState, isCreateTask: true }));
    handleContextCancel();
  };

  /**
   * edit task handle
   */
  const handleEdit = () => {
    setProjectState((prevState) => ({ ...prevState, isEdit: true }));
    handleContextCancel();
  };

  /**
   * copy task handle and submit to server and refresh the tree
   */
  const handleCopy = async () => {
    await handleOption(
      API_CONSTANTS.COPY_TASK_URL,
      l('right.menu.copy'),
      { ...projectState.value },
      () => dispatch({ type: STUDIO_MODEL_ASYNC.queryProject })
    );
    handleContextCancel();
  };

  /**
   * cut task handle
   */
  const handleCut = async () => {
    setProjectState((prevState) => ({
      ...prevState,
      cutId: prevState.rightClickedNode.key,
      isCut: true
    }));
    handleContextCancel();
  };

  /**
   * paste task handle and submit to server and refresh the tree
   */
  const handlePaste = async () => {
    await handlePutDataByParams(
      API_CONSTANTS.MOVE_CATALOGUE_URL,
      l('right.menu.paste'),
      {
        originCatalogueId: projectState.cutId,
        targetParentId: selectKey[0]
      },
      () => {
        // 重置 cutId
        setProjectState((prevState) => ({
          ...prevState,
          cutId: undefined,
          isCut: false
        }));
      }
    );
    dispatch({ type: STUDIO_MODEL_ASYNC.queryProject });
    handleContextCancel();
  };

  /**
   *  all context menu click handle
   */
  const handleMenuClick = async (node: MenuInfo) => {
    setProjectState((prevState) => ({ ...prevState, rightActiveKey: node.key }));
    switch (node.key) {
      case 'addSubFolder':
        handleCreateSubFolder();
        break;
      case 'createTask':
        handleCreateTask();
        break;
      case 'delete':
        await handleDeleteSubmit();
        break;
      case 'renameFolder':
        await handleRename();
        break;
      case 'edit':
        handleEdit();
        break;
      case 'exportJson':
        // todo: 导出 json
        // await handleCancel();
        break;
      case 'copy':
        await handleCopy();
        break;
      case 'cut':
        await handleCut();
        break;
      case 'paste':
        await handlePaste();
        break;
      default:
        handleContextCancel();
        break;
    }
  };

  return (
    <div style={{ paddingInline: 5 }}>
      <JobTree
        selectKeyChange={(keys: Key[]) =>
          dispatch({ type: STUDIO_MODEL.updateProjectSelectKey, payload: keys })
        }
        onRightClick={handleRightClick}
        onExpand={onExpand}
        onNodeClick={(info: any) => onNodeClick(info)}
      />
      <RightContextMenu
        contextMenuPosition={projectState.contextMenuPosition}
        open={projectState.contextMenuOpen}
        openChange={() =>
          setProjectState((prevState) => ({ ...prevState, contextMenuOpen: false }))
        }
        items={projectState.menuItems}
        onClick={handleMenuClick}
      />
      {/*  added  sub folder  */}
      <FolderModal
        title={l('right.menu.createSubFolder')}
        values={{}}
        modalVisible={projectState.isCreateSub}
        onCancel={() =>
          setProjectState((prevState) => ({
            ...prevState,
            isCreateSub: false,
            value: {}
          }))
        }
        onSubmit={handleSubmit}
      />

      {/*  rename  */}
      <FolderModal
        title={l('right.menu.rename')}
        values={projectState.value}
        modalVisible={projectState.isRename}
        onCancel={() =>
          setProjectState((prevState) => ({
            ...prevState,
            isRename: false,
            value: {}
          }))
        }
        onSubmit={handleSubmit}
      />

      {/*  create task  */}
      <JobModal
        title={l('right.menu.createTask')}
        values={{}}
        modalVisible={projectState.isCreateTask}
        onCancel={() =>
          setProjectState((prevState) => ({
            ...prevState,
            isCreateTask: false,
            value: {}
          }))
        }
        onSubmit={handleSubmit}
      />
      {/*  edit task  */}
      {Object.keys(projectState.value).length > 0 && (
        <JobModal
          title={l('button.edit')}
          values={projectState.value}
          modalVisible={projectState.isEdit}
          onCancel={() =>
            setProjectState((prevState) => ({
              ...prevState,
              isEdit: false,
              value: {}
            }))
          }
          onSubmit={handleSubmit}
        />
      )}
    </div>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  tabs: Studio.tabs,
  project: Studio.project
}))(Project);

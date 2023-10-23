/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

import RightContextMenu from '@/components/RightContextMenu';
import {
  FOLDER_RIGHT_MENU,
  JOB_RIGHT_MENU
} from '@/pages/DataStudio/LeftContainer/Project/constants';
import FolderModal from '@/pages/DataStudio/LeftContainer/Project/FolderModal';
import JobModal from '@/pages/DataStudio/LeftContainer/Project/JobModal';
import JobTree from '@/pages/DataStudio/LeftContainer/Project/JobTree';
import { StateType, STUDIO_MODEL, STUDIO_MODEL_ASYNC } from '@/pages/DataStudio/model';
import {
  handleAddOrUpdate,
  handleOption,
  handlePutDataByParams,
  handleRemoveById
} from '@/services/BusinessCrud';
import { API_CONSTANTS } from '@/services/endpoints';
import { Catalogue } from '@/types/Studio/data.d';
import { InitProjectState } from '@/types/Studio/init.d';
import { ProjectState } from '@/types/Studio/state.d';
import { l } from '@/utils/intl';
import { Modal, Typography } from 'antd';
import { MenuInfo } from 'rc-menu/es/interface';
import React, { useEffect, useState } from 'react';
import { connect } from 'umi';

const { Text } = Typography;

const Project: React.FC = (props: connect) => {
  const { dispatch } = props;

  const [projectState, setProjectState] = useState<ProjectState>(InitProjectState);

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
        left: event.clientX + 10
      },
      selectedKeys: [key],
      rightClickedNode: { ...node, ...fullInfo },
      value: fullInfo
    }));
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

    if (!isLeaf) {
      return;
    }

    path.pop();
    dispatch({
      type: STUDIO_MODEL.addTab,
      payload: {
        icon: type,
        id: parentId + name,
        treeKey: key,
        breadcrumbLabel: path.join('/'),
        label: name,
        params: {
          taskId: taskId
        },
        type: 'project',
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
   * @param {Catalogue} values
   * @returns {Promise<void>}
   */
  const handleSubmit = async (values: Catalogue) => {
    const options = {
      url: '',
      isLeaf: projectState.isCreateSub ? false : projectState.value.isLeaf,
      parentId: projectState.isCreateSub
        ? projectState.selectedKeys[0]
        : projectState.value.parentId
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

    const result = await handleAddOrUpdate(
      options.url,
      {
        ...values,
        isLeaf: options.isLeaf,
        parentId: options.parentId
      },
      () => {},
      () => {
        setProjectState((prevState) => ({
          ...prevState,
          isCreateSub: false,
          isRename: false,
          isEdit: false,
          isCreateTask: false,
          isCut: false
        }));
        dispatch({ type: STUDIO_MODEL_ASYNC.queryProject });
        if (projectState.isRename) {
          // todo: 如果是重命名/修改(修改了名字), 则需要 更新 tab 的 label
        }
      }
    );
  };

  /**
   * 删除目录, 并刷新目录树
   * @param {MenuInfo} node
   * @returns {Promise<void>}
   */
  const handleDeleteSubmit = async () => {
    const { key, isLeaf, name, type, taskId } = projectState.rightClickedNode;
    handleContextCancel();
    if (!isLeaf) {
      await handleRemoveById(API_CONSTANTS.DELETE_CATALOGUE_BY_ID_URL, key, () => {
        dispatch({ type: STUDIO_MODEL_ASYNC.queryProject });
        // TODO: 如果打开的 tag 中包含了这个 key 则更新 dav 的 tag 数据 删除此项 && 有一个 bug Dinky/src/pages/DataStudio/RightContainer/JobInfo/index.tsx:55 -> Cannot read properties of undefined (reading 'id')
      });
      return;
    }

    const renderContent = () => {
      return (
        <Text className={'needWrap'} type='danger'>
          {l('datastudio.project.delete.job.confirm')}
        </Text>
      );
    };

    Modal.confirm({
      title: l('datastudio.project.delete.job', '', { type, name }),
      width: '30%',
      content: renderContent(),
      okText: l('button.confirm'),
      cancelText: l('button.cancel'),
      onOk: async () => {
        await handleRemoveById(API_CONSTANTS.DELETE_CATALOGUE_BY_ID_URL, key, () => {
          // TODO: 如果打开的 tag 中包含了这个 key 则更新 dav 的 tag 数据 删除此项
          // dispatch({ type: STUDIO_MODEL.removeTag, payload: taskId });
          dispatch({ type: STUDIO_MODEL_ASYNC.queryProject });
        });
      }
    });
  };

  /**
   * rename handle
   * @returns {Promise<void>}
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
   * @returns {Promise<void>}
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
   * @returns {Promise<void>}
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
   * @returns {Promise<void>}
   */
  const handlePaste = async () => {
    await handlePutDataByParams(
      API_CONSTANTS.MOVE_CATALOGUE_URL,
      l('right.menu.paste'),
      {
        originCatalogueId: projectState.cutId,
        targetParentId: projectState.selectedKeys[0]
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
   * @param {MenuInfo} node
   * @returns {Promise<void>}
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
        selectedKeys={projectState.selectedKeys}
        onRightClick={handleRightClick}
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
  tabs: Studio.tabs
}))(Project);

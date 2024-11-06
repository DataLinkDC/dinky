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

import { MenuInfo } from 'rc-menu/es/interface';
import React, { Key, useEffect, useState } from 'react';
import { InitProjectState } from '@/types/Studio/init.d';
import { ProjectState } from '@/types/Studio/state';
import {
  FOLDER_RIGHT_MENU,
  JOB_RIGHT_MENU
} from '@/pages/DataStudio/LeftContainer/Project/constants';
import { Modal, Typography } from 'antd';
import { DataStudioActionType, RightContextMenuState } from '@/pages/DataStudioNew/data.d';
import { InitContextMenuPosition } from '@/pages/DataStudioNew/function';
import RightContextMenu from '@/pages/DataStudioNew/RightContextMenu';
import FolderModal from '@/pages/DataStudio/LeftContainer/Project/FolderModal';
import { l } from '@/utils/intl';
import JobModal from '@/pages/DataStudio/LeftContainer/Project/JobModal';
import { Catalogue } from '@/types/Studio/data';
import { API_CONSTANTS } from '@/services/endpoints';
import {
  handleAddOrUpdate,
  handleDownloadOption,
  handleOption,
  handlePutDataByParams,
  handleRemoveById
} from '@/services/BusinessCrud';
import { assert } from '@/pages/DataStudio/function';
import { DIALECT } from '@/services/constants';
import { DataStudioState } from '@/pages/DataStudioNew/model';
import { STUDIO_MODEL_ASYNC } from '@/pages/DataStudio/model';
import JobImportModal from '@/pages/DataStudioNew/Toolbar/Project/JobTree/components/JobImportModal';
import { UserBaseInfo } from '@/types/AuthCenter/data.d';

const { Text } = Typography;
export type RightContextProps = {
  selectKeys: Key[];
  refresh: () => Promise<any>;
  centerContent: DataStudioState['centerContent'];
  queryFlinkEnv: any;
  updateCenterTab: any;
  updateAction: any;
  users: UserBaseInfo.User[];
};
export const useRightContext = (props: RightContextProps) => {
  const {
    selectKeys,
    refresh,
    centerContent,
    queryFlinkEnv,
    updateCenterTab,
    updateAction,
    users
  } = props;
  // 右键弹出框状态
  const [rightContextMenuState, setRightContextMenuState] = useState<RightContextMenuState>({
    show: false,
    position: InitContextMenuPosition
  });
  const [uploadValue] = useState({
    url: API_CONSTANTS.IMPORT_CATALOGUE_URL,
    pid: ''
  });
  const [importVisible, setImportVisible] = useState<boolean>(false);

  const [projectState, setProjectState] = useState<ProjectState>(InitProjectState);
  useEffect(() => {
    setProjectState((prevState) => ({
      ...prevState,
      menuItems: prevState.isLeaf
        ? JOB_RIGHT_MENU(prevState.isCut && prevState.cutId !== undefined)
        : FOLDER_RIGHT_MENU(prevState.isCut && prevState.cutId !== undefined)
    }));
  }, [projectState.isCut, projectState.cutId]);

  const handleUploadCancel = async () => {
    setImportVisible(false);
    handleContextCancel();
    await refresh();
  };

  /**
   * the right click event
   * @param info
   */
  const handleProjectRightClick = (info: any) => {
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
      rightClickedNode: { ...node, ...fullInfo },
      value: fullInfo
    }));
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
   * create task handle
   */
  const handleCreateTask = () => {
    setProjectState((prevState) => ({ ...prevState, isCreateTask: true }));
    handleContextCancel();
  };
  /**
   * 删除目录, 并刷新目录树
   */
  const handleDeleteSubmit = async () => {
    const { key, isLeaf, name, type } = projectState.rightClickedNode;

    handleContextCancel();
    if (!isLeaf) {
      await handleRemoveById(API_CONSTANTS.DELETE_CATALOGUE_BY_ID_URL, key, async () => {
        await refresh();
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
          updateAction({
            actionType: DataStudioActionType.TASK_DELETE,
            params: {
              id: `project_${selectKeys[0]}`
            }
          });
        });
        await refresh();
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
   * edit task handle
   */
  const handleEdit = () => {
    setProjectState((prevState) => ({ ...prevState, isEdit: true }));
    handleContextCancel();
  };

  const handleExportJson = async () => {
    const catalogue_id = projectState.value.id;
    await handleDownloadOption(API_CONSTANTS.EXPORT_CATALOGUE_URL, l('right.menu.exportJson'), {
      id: catalogue_id
    });
    handleContextCancel();
  };

  const handleImportJson = () => {
    uploadValue.pid = projectState.value.id;
    setImportVisible(true);
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
      async () => {
        handleContextCancel();
        await refresh();
      }
    );
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
        targetParentId: selectKeys[0]
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
    handleContextCancel();
    await refresh();
  };

  /**
   * 创建目录, 并刷新目录树
   */
  const handleSubmit = async (values: Catalogue) => {
    const options = {
      url: '',
      isLeaf: projectState.isCreateSub ? false : projectState.value.isLeaf,
      parentId: projectState.isCreateSub ? selectKeys[0] : projectState.value.parentId
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
      async () => {
        if (assert(values.type, [DIALECT.FLINKSQLENV], true, 'includes')) {
          queryFlinkEnv();
        }
        if (projectState.isEdit) {
          const tab = centerContent.tabs.find((tab) => tab.params?.taskId === values.taskId);
          if (tab && tab.tabType === 'task') {
            const params = tab.params;
            const taskParams = {
              ...params,
              name: values.name,
              firstLevelOwner: values.firstLevelOwner,
              secondLevelOwners: values.secondLevelOwners
            };
            updateCenterTab({ ...tab, params: taskParams, title: values.name });
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
        await refresh();
      }
    );
  };

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
        await handleExportJson();
        break;
      case 'importJson':
        handleImportJson();
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

  return {
    RightContent: (
      <>
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
          // todo 责任人
          users={users}
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
            users={users}
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

        {/*  import task json  */}
        <JobImportModal
          onUpload={uploadValue}
          visible={importVisible}
          onOk={handleUploadCancel}
          onClose={handleUploadCancel}
        />
        <RightContextMenu
          contextMenuPosition={rightContextMenuState.position}
          open={rightContextMenuState.show}
          openChange={() =>
            setRightContextMenuState((prevState) => ({ ...prevState, show: false }))
          }
          items={projectState.menuItems}
          onClick={handleMenuClick}
        />
      </>
    ),
    setRightContextMenuState,
    handleProjectRightClick
  };
};

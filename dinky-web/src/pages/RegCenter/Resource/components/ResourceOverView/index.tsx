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
import { AuthorizedObject, useAccess } from '@/hooks/useAccess';
import {
  ResourceRightMenuKey,
  RIGHT_CONTEXT_FILE_MENU,
  RIGHT_CONTEXT_FOLDER_MENU
} from '@/pages/RegCenter/Resource/components/constants';
import FileShow from '@/pages/RegCenter/Resource/components/FileShow';
import FileTree from '@/pages/RegCenter/Resource/components/FileTree';
import ResourceModal from '@/pages/RegCenter/Resource/components/ResourceModal';
import ResourcesUploadModal from '@/pages/RegCenter/Resource/components/ResourcesUploadModal';
import { CONFIG_MODEL_ASYNC, SysConfigStateType } from '@/pages/SettingCenter/GlobalSetting/model';
import { SettingConfigKeyEnum } from '@/pages/SettingCenter/GlobalSetting/SettingOverView/constants';
import {
  handleGetOption,
  handleOption,
  handleRemoveById,
  queryDataByParams
} from '@/services/BusinessCrud';
import { API_CONSTANTS } from '@/services/endpoints';
import { ResourceInfo } from '@/types/RegCenter/data';
import { InitResourceState } from '@/types/RegCenter/init.d';
import { ResourceState } from '@/types/RegCenter/state.d';
import { handleCopyToClipboard, unSupportView } from '@/utils/function';
import { l } from '@/utils/intl';
import { SplitPane } from '@andrewray/react-multi-split-pane';
import { Pane } from '@andrewray/react-multi-split-pane/dist/lib/Pane';
import { WarningOutlined } from '@ant-design/icons';
import { ProCard } from '@ant-design/pro-components';
import { history } from '@umijs/max';
import { useAsyncEffect } from 'ahooks';
import { Button, Modal, Result } from 'antd';
import { MenuInfo } from 'rc-menu/es/interface';
import React, { useCallback, useEffect, useRef, useState } from 'react';
import { connect } from 'umi';

const ResourceOverView: React.FC<connect> = (props) => {
  const { dispatch, enableResource, resourcePhysicalDelete } = props;

  const [resourceState, setResourceState] = useState<ResourceState>(InitResourceState);

  const [editModal, setEditModal] = useState<string>('');
  const refObject = useRef<HTMLDivElement>(null);

  const [uploadValue] = useState({
    url: API_CONSTANTS.BASE_URL + API_CONSTANTS.RESOURCE_UPLOAD,
    pid: '',
    description: ''
  });

  const refreshTree = async () => {
    await queryDataByParams<ResourceInfo[]>(API_CONSTANTS.RESOURCE_SHOW_TREE).then((res) =>
      setResourceState((prevState) => ({ ...prevState, treeData: res ?? [] }))
    );
  };

  useEffect(() => {
    dispatch({
      type: CONFIG_MODEL_ASYNC.queryResourceConfig,
      payload: SettingConfigKeyEnum.RESOURCE.toLowerCase()
    });
  }, []);

  useEffect(() => {
    // if enableResource is true, then refresh the tree, otherwise do nothing
    if (enableResource) {
      refreshTree();
    }
  }, [enableResource]);

  /**
   * query content by id
   * @type {(id: number) => Promise<void>}
   */
  const queryContent: (id: number) => Promise<void> = useCallback(async (id: number) => {
    await queryDataByParams<string>(API_CONSTANTS.RESOURCE_GET_CONTENT_BY_ID, {
      id
    }).then((res) => setResourceState((prevState) => ({ ...prevState, content: res ?? '' })));
  }, []);

  /**
   * the node click event
   * @param info
   * @returns {Promise<void>}
   */
  const handleNodeClick = async (info: any): Promise<void> => {
    const {
      node: { id, isLeaf, key, name },
      node
    } = info;
    setResourceState((prevState) => ({ ...prevState, selectedKeys: [key], clickedNode: node }));
    if (isLeaf && !unSupportView(name)) {
      await queryContent(id);
    } else {
      setResourceState((prevState) => ({ ...prevState, content: '' }));
    }
  };

  /**
   * the node right click event OF upload,
   */
  const handleCreateFolder = () => {
    if (resourceState.rightClickedNode) {
      setEditModal(ResourceRightMenuKey.CREATE_FOLDER);
      const { id } = resourceState.rightClickedNode;
      setResourceState((prevState) => ({
        ...prevState,
        editOpen: true,
        value: { id, fileName: '', description: '' },
        contextMenuOpen: false
      }));
    }
  };
  const handleUpload = () => {
    if (resourceState.rightClickedNode) {
      uploadValue.pid = resourceState.rightClickedNode.id;
      // todo: upload
      setResourceState((prevState) => ({ ...prevState, uploadOpen: true, contextMenuOpen: false }));
    }
  };

  const realDelete = async () => {
    await handleRemoveById(API_CONSTANTS.RESOURCE_REMOVE, resourceState.rightClickedNode.id);
    await refreshTree();
  };

  /**
   * the node right click event OF delete,
   */
  const handleDelete = async () => {
    if (resourceState.rightClickedNode) {
      setResourceState((prevState) => ({ ...prevState, contextMenuOpen: false }));
      if (resourcePhysicalDelete) {
        Modal.confirm({
          title: l('rc.resource.delete'),
          content: l('rc.resource.deleteConfirm'),
          onOk: async () => realDelete()
        });
      } else {
        await realDelete();
      }
    }
  };

  /**
   * the node right click event OF rename,
   */
  const handleRename = () => {
    if (resourceState.rightClickedNode) {
      setEditModal(ResourceRightMenuKey.RENAME);
      const { id, name, desc } = resourceState.rightClickedNode;
      setResourceState((prevState) => ({
        ...prevState,
        editOpen: true,
        value: { id, fileName: name, description: desc },
        contextMenuOpen: false
      }));
    }
  };

  const handleMenuClick = async (node: MenuInfo) => {
    const { fullInfo } = resourceState.rightClickedNode;
    switch (node.key) {
      case ResourceRightMenuKey.CREATE_FOLDER:
        handleCreateFolder();
        break;
      case ResourceRightMenuKey.UPLOAD:
        handleUpload();
        break;
      case ResourceRightMenuKey.DELETE:
        await handleDelete();
        break;
      case ResourceRightMenuKey.RENAME:
        handleRename();
        break;
      case ResourceRightMenuKey.COPY_TO_ADD_CUSTOM_JAR:
        if (fullInfo) {
          const fillValue = `ADD CUSTOMJAR 'rs:${fullInfo.fullName}';`;
          await handleCopyToClipboard(fillValue);
        }
        break;
      case ResourceRightMenuKey.COPY_TO_ADD_JAR:
        if (fullInfo) {
          const fillValue = `ADD JAR 'rs:${fullInfo.fullName}';`;
          await handleCopyToClipboard(fillValue);
        }
        break;
      case ResourceRightMenuKey.COPY_TO_ADD_FILE:
        if (fullInfo) {
          const fillValue = `ADD FILE 'rs:${fullInfo.fullName}';`;
          await handleCopyToClipboard(fillValue);
        }
        break;
      case ResourceRightMenuKey.COPY_TO_ADD_RS_PATH:
        if (fullInfo) {
          const fillValue = `rs:${fullInfo.fullName}`;
          await handleCopyToClipboard(fillValue);
        }
        break;
      default:
        break;
    }
  };

  /**
   * the right click event
   * @param info
   */
  const handleRightClick = (info: any) => {
    // Obtain the node information for right-click
    const { node, event } = info;

    // Determine if the position of the right button exceeds the screen. If it exceeds the screen, set it to the maximum value of the screen offset upwards by 75 (it needs to be reasonably set according to the specific number of right button menus)
    if (event.clientY + 150 > window.innerHeight) {
      event.clientY = window.innerHeight - 75;
    }

    setResourceState((prevState) => ({
      ...prevState,
      selectedKeys: [node.key],
      rightClickedNode: node,
      contextMenuOpen: true,
      contextMenuPosition: {
        ...prevState.contextMenuPosition,
        top: event.clientY + 5,
        left: event.clientX + 10,
        screenX: event.screenX,
        screenY: event.screenY
      }
    }));
  };

  const handleSync = async () => {
    Modal.confirm({
      title: l('rc.resource.sync'),
      content: l('rc.resource.sync.confirm'),
      onOk: async () => {
        await handleGetOption(API_CONSTANTS.RESOURCE_SYNC_DATA, l('rc.resource.sync'), {});
        await refreshTree();
      }
    });
  };

  /**
   * the rename cancel
   */
  const handleModalCancel = async () => {
    setResourceState((prevState) => ({ ...prevState, editOpen: false }));
    await refreshTree();
  };

  /**
   * the rename ok
   */
  const handleModalSubmit = async (value: Partial<ResourceInfo>) => {
    const { id: pid } = resourceState.rightClickedNode;
    if (editModal === ResourceRightMenuKey.CREATE_FOLDER) {
      await handleOption(
        API_CONSTANTS.RESOURCE_CREATE_FOLDER,
        l('right.menu.createFolder'),
        {
          ...value,
          pid
        },
        () => handleModalCancel()
      );
    } else if (editModal === ResourceRightMenuKey.RENAME) {
      await handleOption(
        API_CONSTANTS.RESOURCE_RENAME,
        l('right.menu.rename'),
        { ...value, pid },
        () => handleModalCancel()
      );
    }
  };
  const handleUploadCancel = async () => {
    setResourceState((prevState) => ({ ...prevState, uploadOpen: false }));
    await refreshTree();
  };

  /**
   * the content change
   * @param value
   */
  const handleContentChange = (value: any) => {
    setResourceState((prevState) => ({ ...prevState, content: value }));
    // todo: save content
  };

  const access = useAccess();

  const renderRightMenu = () => {
    if (!resourceState.rightClickedNode.isLeaf) {
      return RIGHT_CONTEXT_FOLDER_MENU.filter(
        (menu) => !menu.path || !!AuthorizedObject({ path: menu.path, children: menu, access })
      );
    }
    return RIGHT_CONTEXT_FILE_MENU.filter(
      (menu) => !menu.path || !!AuthorizedObject({ path: menu.path, children: menu, access })
    );
  };

  /**
   * render
   */
  return (
    <>
      {!enableResource ? (
        <ProCard ghost size={'small'} bodyStyle={{ height: parent.innerHeight - 80 }}>
          <Result
            status='warning'
            style={{ alignItems: 'center', justifyContent: 'center' }}
            icon={<WarningOutlined />}
            title={l('rc.resource.enable')}
            subTitle={l('rc.resource.enable.tips')}
            extra={
              <Button
                onClick={() => {
                  history.push('/settings/globalsetting');
                }}
                type='primary'
                key='globalsetting-to-jump'
              >
                {l('menu.settings')}
              </Button>
            }
          />
        </ProCard>
      ) : (
        <>
          <ProCard ghost size={'small'} bodyStyle={{ height: parent.innerHeight - 80 }}>
            <SplitPane
              split={'vertical'}
              defaultSizes={[200, 500]}
              minSize={200}
              className={'split-pane'}
            >
              <Pane
                className={'split-pane'}
                forwardRef={refObject}
                minSize={200}
                size={200}
                split={'horizontal'}
              >
                <ProCard
                  hoverable
                  boxShadow
                  bodyStyle={{ height: parent.innerHeight - 80 }}
                  colSpan={'18%'}
                >
                  <FileTree
                    selectedKeys={resourceState.selectedKeys}
                    treeData={resourceState.treeData}
                    onRightClick={handleRightClick}
                    onNodeClick={(info: any) => handleNodeClick(info)}
                    onSync={handleSync}
                  />
                  <RightContextMenu
                    contextMenuPosition={resourceState.contextMenuPosition}
                    open={resourceState.contextMenuOpen}
                    openChange={() =>
                      setResourceState((prevState) => ({ ...prevState, contextMenuOpen: false }))
                    }
                    items={renderRightMenu()}
                    onClick={handleMenuClick}
                  />
                </ProCard>
              </Pane>

              <Pane
                className={'split-pane'}
                forwardRef={refObject}
                minSize={100}
                size={100}
                split={'horizontal'}
              >
                <ProCard hoverable bodyStyle={{ height: parent.innerHeight }}>
                  <FileShow
                    onChange={handleContentChange}
                    code={resourceState.content}
                    item={resourceState.clickedNode}
                  />
                </ProCard>
              </Pane>
            </SplitPane>
          </ProCard>
          {resourceState.editOpen && (
            <ResourceModal
              title={
                editModal === 'createFolder'
                  ? l('right.menu.createFolder')
                  : editModal === 'rename'
                    ? l('right.menu.rename')
                    : ''
              }
              formValues={resourceState.value}
              onOk={handleModalSubmit}
              onClose={handleModalCancel}
              visible={resourceState.editOpen}
            />
          )}
          {resourceState.uploadOpen && (
            <ResourcesUploadModal
              onUpload={uploadValue}
              visible={resourceState.uploadOpen}
              onOk={handleUploadCancel}
              onClose={handleUploadCancel}
            />
          )}
        </>
      )}
    </>
  );
};

export default connect(({ SysConfig }: { SysConfig: SysConfigStateType }) => ({
  enableResource: SysConfig.enableResource,
  resourcePhysicalDelete: SysConfig.resourcePhysicalDelete
}))(ResourceOverView);

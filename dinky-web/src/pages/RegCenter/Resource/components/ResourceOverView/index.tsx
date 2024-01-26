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
  RIGHT_CONTEXT_FILE_MENU,
  RIGHT_CONTEXT_FOLDER_MENU
} from '@/pages/RegCenter/Resource/components/constants';
import FileShow from '@/pages/RegCenter/Resource/components/FileShow';
import FileTree from '@/pages/RegCenter/Resource/components/FileTree';
import ResourceModal from '@/pages/RegCenter/Resource/components/ResourceModal';
import ResourcesUploadModal from '@/pages/RegCenter/Resource/components/ResourcesUploadModal';
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
import { unSupportView } from '@/utils/function';
import { l } from '@/utils/intl';
import { SplitPane } from '@andrewray/react-multi-split-pane';
import { Pane } from '@andrewray/react-multi-split-pane/dist/lib/Pane';
import { ProCard } from '@ant-design/pro-components';
import { useAsyncEffect } from 'ahooks';
import { MenuInfo } from 'rc-menu/es/interface';
import React, { useCallback, useRef, useState } from 'react';

const ResourceOverView: React.FC = () => {
  const [resourceState, setResourceState] = useState<ResourceState>(InitResourceState);

  const [editModal, setEditModal] = useState<string>('');
  const refObject = useRef<HTMLDivElement>(null);

  const [uploadValue] = useState({
    url: API_CONSTANTS.RESOURCE_UPLOAD,
    pid: '',
    description: ''
  });

  const refreshTree = async () => {
    await queryDataByParams<ResourceInfo[]>(API_CONSTANTS.RESOURCE_SHOW_TREE).then((res) =>
      setResourceState((prevState) => ({ ...prevState, treeData: res ?? [] }))
    );
  };

  useAsyncEffect(async () => {
    await refreshTree();
  }, []);

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
      setEditModal('createFolder');
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

  /**
   * the node right click event OF delete,
   */
  const handleDelete = async () => {
    if (resourceState.rightClickedNode) {
      setResourceState((prevState) => ({ ...prevState, contextMenuOpen: false }));
      await handleRemoveById(API_CONSTANTS.RESOURCE_REMOVE, resourceState.rightClickedNode.id);
      await refreshTree();
    }
  };

  /**
   * the node right click event OF rename,
   */
  const handleRename = () => {
    if (resourceState.rightClickedNode) {
      setEditModal('rename');
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
    switch (node.key) {
      case 'createFolder':
        handleCreateFolder();
        break;
      case 'upload':
        handleUpload();
        break;
      case 'delete':
        await handleDelete();
        break;
      case 'rename':
        handleRename();
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
    // 获取右键点击的节点信息
    const { node, event } = info;
    setResourceState((prevState) => ({
      ...prevState,
      selectedKeys: [node.key],
      rightClickedNode: node,
      contextMenuOpen: true,
      contextMenuPosition: {
        ...prevState.contextMenuPosition,
        left: event.clientX + 20,
        top: event.clientY + 20
      }
    }));
  };

  const handleSync = async () => {
    await handleGetOption(API_CONSTANTS.RESOURCE_SYNC_DATA, l('rc.resource.sync'), {});
    await refreshTree();
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
    if (editModal === 'createFolder') {
      await handleOption(API_CONSTANTS.RESOURCE_CREATE_FOLDER, l('right.menu.createFolder'), {
        ...value,
        pid
      });
      setResourceState((prevState) => ({ ...prevState, editOpen: false }));
    } else if (editModal === 'rename') {
      await handleOption(API_CONSTANTS.RESOURCE_RENAME, l('right.menu.rename'), { ...value, pid });
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
      <ProCard ghost size={'small'} bodyStyle={{ height: parent.innerHeight - 80 }}>
        <SplitPane
          split={'vertical'}
          defaultSizes={[150, 500]}
          minSize={150}
          className={'split-pane'}
        >
          <Pane
            className={'split-pane'}
            forwardRef={refObject}
            minSize={100}
            size={100}
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
  );
};

export default ResourceOverView;

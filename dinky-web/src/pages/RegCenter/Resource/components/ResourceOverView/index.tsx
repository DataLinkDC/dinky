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
import { RIGHT_CONTEXT_MENU } from '@/pages/RegCenter/Resource/components/constants';
import FileShow from '@/pages/RegCenter/Resource/components/FileShow';
import FileTree from '@/pages/RegCenter/Resource/components/FileTree';
import ResourceModal from '@/pages/RegCenter/Resource/components/ResourceModal';
import ResourcesUploadModal from '@/pages/RegCenter/Resource/components/ResourcesUploadModal';
import { handleOption, handleRemoveById, queryDataByParams } from '@/services/BusinessCrud';
import { API_CONSTANTS } from '@/services/endpoints';
import { InitResourceState } from '@/types/RegCenter/init.d';
import { ResourceState } from '@/types/RegCenter/state.d';
import { ProCard } from '@ant-design/pro-components';
import { MenuInfo } from 'rc-menu/es/interface';
import { Resizable } from 're-resizable';
import React, { useCallback, useEffect, useState } from 'react';

export type Resource = {
  id: number;
  fileName: string;
  description: string;
  type?: string;
};

const ResourceOverView: React.FC = () => {
  const [resourceState, setResourceState] = useState<ResourceState>(InitResourceState);

  const [editModal, setEditModal] = useState<string>('');

  const [uploadValue] = useState({
    url: API_CONSTANTS.RESOURCE_UPLOAD,
    pid: '',
    description: ''
  });

  const updateTreeData = (list: any[], key: React.Key, children: any[]): any[] =>
    list.map((node) => {
      if (node.path === key) {
        return { ...node, children };
      }
      if (node.children) {
        return {
          ...node,
          children: updateTreeData(node.children, key, children)
        };
      }
      return node;
    });

  const refreshTreeData = async (pid: number, path: string) => {
    const data = await queryDataByParams(API_CONSTANTS.RESOURCE_SHOW_TREE, {
      pid: pid
    });
    setResourceState((prevState) => ({
      ...prevState,
      treeData: updateTreeData(prevState.treeData, path, data)
    }));
  };

  const refreshTree = async () => {
    await queryDataByParams(API_CONSTANTS.RESOURCE_SHOW_TREE, { pid: -1 }).then((res) =>
      setResourceState((prevState) => ({ ...prevState, treeData: res }))
    );
  };

  useEffect(() => {
    refreshTree();
  }, []);

  /**
   * query content by id
   * @type {(id: number) => Promise<void>}
   */
  const queryContent = useCallback(
    async (id: number) => {
      await queryDataByParams(API_CONSTANTS.RESOURCE_GET_CONTENT_BY_ID, {
        id
      }).then((res) => setResourceState((prevState) => ({ ...prevState, content: res })));
    },
    [resourceState.clickedNode]
  );

  /**
   * the node click event
   * @param info
   * @returns {Promise<void>}
   */
  const handleNodeClick = async (info: any) => {
    const {
      node: { id, isLeaf, key },
      node
    } = info;
    setResourceState((prevState) => ({ ...prevState, selectedKeys: [key], clickedNode: node }));
    if (isLeaf) {
      await queryContent(id);
    } else {
      setResourceState((prevState) => ({ ...prevState, content: '' }));
    }
  };
  const getSelectedNode = () => {
    const indexes = (resourceState.rightClickedNode.pos.split('-') as string[]).map((x) =>
      parseInt(x)
    );
    if (indexes.length === 1) {
      return resourceState.treeData[indexes[0]];
    } else {
      let temp = resourceState.treeData[indexes[0]];
      for (let i = 1; i < indexes.length - 1; i++) {
        temp = temp.children[indexes[i]];
      }
      return temp;
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
      setResourceState((prevState) => ({ ...prevState, uploadOpen: true }));
    }
  };

  const getSelectedParentNode = () => {
    const indexes = (resourceState.rightClickedNode.pos.split('-') as string[]).map((x) =>
      parseInt(x)
    );
    let temp = resourceState.treeData[indexes[0]];
    for (let i = 1; i < indexes.length - 2; i++) {
      temp = temp.children[indexes[i]];
    }
    return { node: temp, index: indexes[indexes.length - 2] };
  };

  /**
   * the node right click event OF delete,
   */
  const handleDelete = async () => {
    if (resourceState.rightClickedNode) {
      await handleRemoveById(API_CONSTANTS.RESOURCE_REMOVE, resourceState.rightClickedNode.id);
      // await refreshTree()
      const { node, index } = getSelectedParentNode();
      node.children.splice(index, 1);
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
  const handleRefresh = async () => {
    if (resourceState.rightClickedNode) {
      // const {id, name, desc, path} = rightClickedNode;
      //todo refresh
    }
  };

  const handleMenuClick = (node: MenuInfo) => {
    switch (node.key) {
      case 'createFolder':
        handleCreateFolder();
        break;
      case 'upload':
        handleUpload();
        break;
      case 'delete':
        handleDelete();
        break;
      case 'rename':
        handleRename();
        break;
      case 'refresh':
        handleRefresh();
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

  /**
   * the rename cancel
   */
  const handleModalCancel = () => {
    setResourceState((prevState) => ({ ...prevState, editOpen: false }));
  };

  /**
   * the rename ok
   */
  const handleModalSubmit = async (value: Partial<Resource>) => {
    if (editModal === 'createFolder') {
      const d = (
        await handleOption(API_CONSTANTS.RESOURCE_CREATE_FOLDER, '创建文件夹', {
          ...value
        })
      ).datas;
      if (getSelectedNode().children) {
        getSelectedNode().children.push(d);
      } else {
        getSelectedNode().children = [d];
      }
      setResourceState((prevState) => ({ ...prevState, editOpen: false }));
    } else if (editModal === 'rename') {
      await handleOption(API_CONSTANTS.RESOURCE_RENAME, '重命名', { ...value });
      getSelectedNode().fileName = value.fileName;
      getSelectedNode().name = value.fileName;
    }
  };
  const handleUploadCancel = () => {
    setResourceState((prevState) => ({ ...prevState, uploadOpen: false }));
  };

  /**
   * the content change
   * @param value
   */
  const handleContentChange = (value: any) => {
    setResourceState((prevState) => ({ ...prevState, content: value }));
    // todo: save content
  };

  const asyncLoadData = async ({ children, path, id }: any) => {
    if (children.length > 0) {
      return;
    }
    await refreshTreeData(id, path);
  };

  /**
   * render
   */
  return (
    <>
      <ProCard size={'small'}>
        <Resizable
          defaultSize={{
            width: 500,
            height: '100%'
          }}
          minWidth={200}
          maxWidth={1200}
        >
          <ProCard ghost hoverable colSpan={'18%'} className={'siderTree schemaTree'}>
            <FileTree
              loadData={asyncLoadData}
              selectedKeys={resourceState.selectedKeys}
              treeData={resourceState.treeData}
              onRightClick={handleRightClick}
              onNodeClick={(info: any) => handleNodeClick(info)}
            />
            <RightContextMenu
              contextMenuPosition={resourceState.contextMenuPosition}
              open={resourceState.contextMenuOpen}
              openChange={() =>
                setResourceState((prevState) => ({ ...prevState, contextMenuOpen: false }))
              }
              items={RIGHT_CONTEXT_MENU()}
              onClick={handleMenuClick}
            />
          </ProCard>
        </Resizable>
        <ProCard.Divider type={'vertical'} />
        <ProCard ghost hoverable className={'schemaTree'}>
          <FileShow
            onChange={handleContentChange}
            code={resourceState.content}
            item={resourceState.clickedNode}
          />
        </ProCard>
      </ProCard>
      {resourceState.editOpen && (
        <ResourceModal
          title={editModal}
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

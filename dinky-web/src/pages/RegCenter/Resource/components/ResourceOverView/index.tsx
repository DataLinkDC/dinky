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


import React, {useCallback, useEffect, useState} from "react";
import {ProCard} from "@ant-design/pro-components";
import FileTree from "@/pages/RegCenter/Resource/components/FileTree";
import FileShow from "@/pages/RegCenter/Resource/components/FileShow";
import {Dropdown, Menu} from "antd";
import {MenuInfo} from "rc-menu/es/interface";
import {API_CONSTANTS} from "@/services/constants";
import {handleOption, handleRemoveById, queryDataByParams} from "@/services/BusinessCrud";
import {RIGHT_CONTEXT_MENU} from "@/pages/RegCenter/Resource/components/constants";
import ResourceModal from "@/pages/RegCenter/Resource/components/ResourceModal";
import {Resizable} from "re-resizable";
import ResourcesUploadModal from "@/pages/RegCenter/Resource/components/ResourcesUploadModal";

export type Resource = {
  id: number,
  fileName: string,
  description: string,
  type?: string,
};

const ResourceOverView: React.FC = () => {
  const [treeData, setTreeData] = useState<Partial<any[]>>([]);
  const [content, setContent] = useState<string>('');
  const [clickedNode, setClickedNode] = useState({});
  const [rightClickedNode, setRightClickedNode] = useState<any>();
  const [contextMenuVisible, setContextMenuVisible] = useState(false);
  const [contextMenuPosition, setContextMenuPosition] = useState({});
  const [selectedKeys, setSelectedKeys] = useState([]);
  const [showEditModal, setShowEditModal] = useState(false);
  const [editModal, setEditModal] = useState<string>("");
  const [showUpload, setShowUpload] = useState(false);
  const [formValue, setFormValue] = useState<Resource>({id: 0, fileName: '', description: ''});
  const [uploadValue] = useState({url: API_CONSTANTS.RESOURCE_UPLOAD, pid: '', description: ''});

  const updateTreeData = (list: any[], key: React.Key, children: any[]): any[] =>
      list.map((node) => {
        if (node.path === key) {
          return {
            ...node,
            children,
          };
        }
        if (node.children) {
          return {
            ...node,
            children: updateTreeData(node.children, key, children),
          };
        }
        return node;
      });

  const refreshTreeData = async (pid: number, path: string) => {
    const data = await queryDataByParams(API_CONSTANTS.RESOURCE_SHOW_TREE, {pid: pid});
    setTreeData((origin) =>
        updateTreeData(origin, path, data)
    );
  }


  const refreshTree = async () => {
    await queryDataByParams(API_CONSTANTS.RESOURCE_SHOW_TREE, {pid: -1}).then(res => setTreeData(res))
  }

  useEffect(() => {
    refreshTree()
  }, [])


  /**
   * query content by id
   * @type {(id: number) => Promise<void>}
   */
  const queryContent = useCallback(async (id: number) => {
    await queryDataByParams(API_CONSTANTS.RESOURCE_GET_CONTENT_BY_ID, {id}).then(res => setContent(res))
  }, [clickedNode])

  /**
   * the node click event
   * @param info
   * @returns {Promise<void>}
   */
  const handleNodeClick = async (info: any) => {
    const {node: {id, isLeaf, key,}, node} = info;
    setSelectedKeys([key] as any);
    setClickedNode(node);
    if (isLeaf) {
      await queryContent(id);
    } else {
      setContent('');
    }
  };
  const getSelectedNode = () => {
    const indexes = (rightClickedNode.pos.split("-") as string[]).map(x => parseInt(x));
    if (indexes.length === 1) {
      return treeData[indexes[0]]
    } else {
      let temp = treeData[indexes[0]]
      for (let i = 1; i < indexes.length - 1; i++) {
        temp = temp.children[indexes[i]]
      }
      return temp
    }
  }

  /**
   * the node right click event OF upload,
   */
  const handleCreateFolder = () => {
    if (rightClickedNode) {
      setEditModal("createFolder")
      const {id} = rightClickedNode;
      setShowEditModal(true)
      setFormValue({id, fileName: "", description: ""})
      setContextMenuVisible(false)
    }
  };
  const handleUpload = () => {
    if (rightClickedNode) {
      uploadValue.pid = rightClickedNode.id
      // todo: upload
      setShowUpload(true)
    }
  };

  const getSelectedParentNode = () => {
    const indexes = (rightClickedNode.pos.split("-") as string[]).map(x => parseInt(x));
    let temp = treeData[indexes[0]]
    for (let i = 1; i < indexes.length - 2; i++) {
      temp = temp.children[indexes[i]]
    }
    return {node: temp, index: indexes[indexes.length - 2]}
  }

  /**
   * the node right click event OF delete,
   */
  const handleDelete = async () => {
    //todo delete
    if (rightClickedNode) {
      await handleRemoveById(API_CONSTANTS.RESOURCE_REMOVE, rightClickedNode.id)
      // await refreshTree()
      const {node, index} = getSelectedParentNode();
      node.children.splice(index, 1)
    }
  };

  /**
   * the node right click event OF rename,
   */
  const handleRename = () => {
    if (rightClickedNode) {
      setEditModal("rename")
      const {id, name, desc} = rightClickedNode;
      setShowEditModal(true)
      setFormValue({id, fileName: name, description: desc})
      setContextMenuVisible(false)
    }
  }
  const handleRefresh = async () => {
    if (rightClickedNode) {
      // const {id, name, desc, path} = rightClickedNode;
     //todo refresh

    }
  }

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
    const {node, event} = info;
    setSelectedKeys([node.key] as any);
    setRightClickedNode(node);
    setContextMenuVisible(true);
    setContextMenuPosition({
      position: 'fixed',
      cursor: 'context-menu',
      width: '10vw',
      left: event.clientX + 20, // + 20 是为了让鼠标不至于在选中的节点上 && 不遮住当前鼠标位置
      top: event.clientY + 20, // + 20 是为了让鼠标不至于在选中的节点上
      zIndex: 888,
    });
  };

  /**
   * the rename cancel
   */
  const handleModalCancel = () => {
    setShowEditModal(false)
  }

  /**
   * the rename ok
   */
  const handleModalSubmit = async (value: Partial<Resource>) => {
    if (editModal === "createFolder") {
      const d = (await handleOption(API_CONSTANTS.RESOURCE_CREATE_FOLDER, "创建文件夹", {...value})).datas
      if (getSelectedNode().children) {
        getSelectedNode().children.push(d)
      } else {
        getSelectedNode().children = [d]
      }
      setShowEditModal(false)
    } else if (editModal === "rename") {
      await handleOption(API_CONSTANTS.RESOURCE_RENAME, "重命名", {...value})
      getSelectedNode().fileName = value.fileName
      getSelectedNode().name = value.fileName
    }
  }
  const handleUploadCancel = () => {
    setShowUpload(false)
  }




  /**
   * render the right click menu
   * @returns {JSX.Element}
   */
  const renderRightClickMenu = () => {
    const menu = <Menu onClick={handleMenuClick} items={RIGHT_CONTEXT_MENU()}/>
    return <>
      <Dropdown
        arrow
        trigger={['contextMenu']}
        overlayStyle={{...contextMenuPosition}}
        overlay={menu}
        open={contextMenuVisible}
        onVisibleChange={setContextMenuVisible}
      >
        {/*占位*/}
        <div style={{...contextMenuPosition}}/>
      </Dropdown>
    </>
  }


  /**
   * the content change
   * @param value
   */
  const handleContentChange = (value: any) => {
    setContent(value);
    // todo: save content
  }

  const asyncLoadData = async ({ children, path, id}: any) => {
    if (children.length > 0) {
      return;
    }
    await refreshTreeData(id, path)
  }



  /**
   * render
   */
  return <>
    <ProCard size={'small'}>
      <Resizable
        defaultSize={{
          width: 500,
          height: '100%'
        }}
        minWidth={200}
        maxWidth={1200}
      >
        <ProCard ghost hoverable colSpan={'18%'} className={"siderTree schemaTree"}>
          <FileTree
            loadData={asyncLoadData}
            selectedKeys={selectedKeys}
            treeData={treeData}
            onRightClick={handleRightClick}
            onNodeClick={(info: any) => handleNodeClick(info)}
          />
          {contextMenuVisible && renderRightClickMenu()}
        </ProCard>
      </Resizable>
      <ProCard.Divider type={"vertical"}/>
      <ProCard ghost hoverable className={"schemaTree"}>
        <FileShow
          onChange={handleContentChange}
          code={content}
          item={clickedNode}
        />
      </ProCard>
    </ProCard>
    {showEditModal &&
      <ResourceModal title={editModal} formValues={formValue} onOk={handleModalSubmit} onClose={handleModalCancel}
                     visible={showEditModal}/>}
    {showUpload &&
      <ResourcesUploadModal onUpload={uploadValue} visible={showUpload} onOk={handleUploadCancel}
                            onClose={handleUploadCancel}/>}
  </>
}

export default ResourceOverView;

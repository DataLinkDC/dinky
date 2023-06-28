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
import {ModalForm, ProCard, ProFormText, ProFormTextArea} from "@ant-design/pro-components";
import FileTree from "@/pages/RegCenter/Resource/components/FileTree";
import FileShow from "@/pages/RegCenter/Resource/components/FileShow";
import {Dropdown, Form, Menu, Modal} from "antd";
import {MenuInfo} from "rc-menu/es/interface";
import {API_CONSTANTS} from "@/services/constants";
import {handleRemoveById, queryDataByParams} from "@/services/BusinessCrud";
import {RIGHT_CONTEXT_MENU} from "@/pages/RegCenter/Resource/components/constants";
import { ExclamationCircleOutlined } from "@ant-design/icons";


const ResourceOverView: React.FC = () => {
  const [modal, contextHolder] = Modal.useModal();
  const [treeData, setTreeData] = useState<Partial<any[]>>([]);
  const [content, setContent] = useState<string>('');
  const [clickedNode, setClickedNode] = useState({});
  const [rightClickedNode, setRightClickedNode] = useState<any>();
  const [contextMenuVisible, setContextMenuVisible] = useState(false);
  const [contextMenuPosition, setContextMenuPosition] = useState({});
  const [selectedKeys, setSelectedKeys] = useState([]);
  const [showRename, setShowRename] = useState(false);
  const [form] = Form.useForm<{ fileName: string; desc: string }>();

  useEffect(() => {
    queryDataByParams(API_CONSTANTS.RESOURCE_SHOW_TREE, {pid: -1}).then(res => setTreeData(res))
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
    const {node: {id, isLeaf, key}, node} = info;
    setSelectedKeys([key] as any);
    setClickedNode(node);
    if (isLeaf) {
      await queryContent(id);
    } else {
      setContent('');
    }
  };


  /**
   * the node right click event OF upload,
   */
  const handleUpload = () => {
    if (rightClickedNode) {
      // todo: upload
    }
  };

  /**
   * the node right click event OF delete,
   */
  const handleDelete = async () => {
    //todo delete
    if (rightClickedNode) {
      await handleRemoveById(API_CONSTANTS.RESOURCE_REMOVE, rightClickedNode.id)
    }
  };

  /**
   * the node right click event OF rename,
   */
  const handleRename = () => {
    if (rightClickedNode) {
      // todo: rename
      setShowRename(true)
      console.log(rightClickedNode.name)
      form.setFieldsValue({fileName:rightClickedNode.name,desc:rightClickedNode.desc});
    }
  }

  const handleMenuClick = (node: MenuInfo) => {
    switch (node.key) {
      case 'upload':
        handleUpload();
        break;
      case 'delete':
        handleDelete();
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
    const {node, event} = info;
    setSelectedKeys([node.key] as any);
    setRightClickedNode(node);
    setContextMenuVisible(true);
    setContextMenuPosition({
      position: 'fixed',
      cursor: 'context-menu',
      width: '5vw',
      left: event.clientX + 20, // + 20 是为了让鼠标不至于在选中的节点上 && 不遮住当前鼠标位置
      top: event.clientY + 20, // + 20 是为了让鼠标不至于在选中的节点上
      zIndex: 888,
    });
  };


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


  /**
   * render
   */
  return <>
    <ModalForm<{
      name: string;
      company: string;
    }>
      title="新建表单"
      // form={form}
      autoFocusFirstInput
      modalProps={{
        destroyOnClose: true,
        onCancel: () => setShowRename(false),
      }}
      submitTimeout={2000}
      onFinish={async (values) => {
        // await waitTime(2000);
        // console.log(values.name);
        // message.success('提交成功');
        return true;
      }}
      open={showRename}
    >
      <ProFormText required width="lg" name="fileName" label="名称" />
      <ProFormTextArea
        colProps={{ span: 24 }}
        name="desc"
        label="描述"
      />
    </ModalForm>
    <ProCard size={'small'}>
      <ProCard ghost hoverable colSpan={'18%'} className={"schemaTree"}>
        <FileTree
          selectedKeys={selectedKeys}
          treeData={treeData}
          onRightClick={handleRightClick}
          onNodeClick={(info: any) => handleNodeClick(info)}
        />
        {contextMenuVisible && renderRightClickMenu()}
      </ProCard>
      <ProCard.Divider type={"vertical"}/>
      <ProCard ghost hoverable className={"schemaTree"}>
        <FileShow
          onChange={handleContentChange}
          code={content}
          item={clickedNode}
        />
      </ProCard>
    </ProCard>
  </>
}

export default ResourceOverView;

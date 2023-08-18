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
import MenuForm from '@/pages/AuthCenter/Menu/components/MenuForm';
import { RIGHT_CONTEXT_MENU } from '@/pages/AuthCenter/Menu/components/MenuList/constants';
import OpHelper from '@/pages/AuthCenter/Menu/components/MenuList/OpHelper';
import MenuTree from '@/pages/AuthCenter/Menu/components/MenuTree';
import {
  handleAddOrUpdate,
  handleRemoveById,
  queryDataByParams,
} from '@/services/BusinessCrud';
import { API_CONSTANTS } from '@/services/constants';
import { SysMenu } from '@/types/RegCenter/data';
import { l } from '@/utils/intl';
import { PlusSquareTwoTone, ReloadOutlined } from '@ant-design/icons';
import { ProCard } from '@ant-design/pro-components';
import { Button, Space } from 'antd';
import { MenuInfo } from 'rc-menu/es/interface';
import React, { useEffect, useState } from 'react';

const MenuList: React.FC = () => {
  /**
   * status
   */
  const [formValues, setFormValues] = useState<Partial<SysMenu>>({});
  const [contextMenuPosition, setContextMenuPosition] = useState({});
  const [selectedKeys, setSelectedKeys] = useState([]);
  const [clickedNode, setClickedNode] = useState<{
    clickedNode: any;
    rightClickedNode: any;
  }>({
    clickedNode: {},
    rightClickedNode: {},
  });
  const [treeData, setTreeData] = useState<SysMenu[]>([]);

  const [modalVisible, handleModalVisible] = useState<boolean>(false);
  const [updateModalVisible, handleUpdateModalVisible] =
    useState<boolean>(false);
  const [loading, setLoading] = useState<boolean>(false);
  const [contextMenuVisible, setContextMenuVisible] = useState(false);
  const [disabled, setDisabled] = useState<boolean>(false);
  const [isRootMenu, setIsRootMenu] = useState<boolean>(false);

  /**
   * query
   */
  const queryMenuData = async () => {
    setLoading(true);
    await queryDataByParams(API_CONSTANTS.MENU_LIST).then((res) =>
      setTreeData(res),
    );
    setLoading(false);
  };

  useEffect(() => {
    queryMenuData();
  }, []);

  const executeAndCallbackRefresh = async (callback: () => void) => {
    setLoading(true);
    await callback();
    setLoading(false);
    await queryMenuData();
  };

  /**
   * delete role by id
   * @param id role id
   */
  const handleDeleteSubmit = async () => {
    await executeAndCallbackRefresh(async () => {
      await handleRemoveById(
        API_CONSTANTS.MENU_DELETE,
        clickedNode?.rightClickedNode.key as number,
      );
    });
    setContextMenuVisible(false);
  };

  /**
   * add or update role submit callback
   * @param value
   */
  const handleAddOrUpdateSubmit = async (value: Partial<SysMenu>) => {
    await executeAndCallbackRefresh(async () => {
      await handleAddOrUpdate(API_CONSTANTS.MENU_ADD_OR_UPDATE, { ...value });
    });
    handleModalVisible(false);
  };

  /**
   * cancel
   */
  const handleCancel = () => {
    handleModalVisible(false);
    handleUpdateModalVisible(false);
    setContextMenuVisible(false);
  };

  /**
   * create sub menu callback
   */
  const handleCreateSubMenu = () => {
    handleModalVisible(true);
    setIsRootMenu(false);
    handleUpdateModalVisible(false);
    setContextMenuVisible(false);
    setFormValues({});
  };

  const handleMenuClick = async (node: MenuInfo) => {
    switch (node.key) {
      case 'addSub':
        await handleCreateSubMenu();
        break;
      case 'delete':
        await handleDeleteSubmit();
        break;
      case 'cancel':
        await handleCancel();
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
    setSelectedKeys([node.key] as any);
    setClickedNode((prevState) => ({ ...prevState, rightClickedNode: node }));
    setContextMenuVisible(true);
    setContextMenuPosition({
      position: 'fixed',
      cursor: 'context-menu',
      width: '12vw',
      left: event.clientX + 20, // + 20 是为了让鼠标不至于在选中的节点上 && 不遮住当前鼠标位置
      top: event.clientY + 20, // + 20 是为了让鼠标不至于在选中的节点上
      zIndex: 888,
    });
  };

  const handleNodeClick = async (info: any) => {
    const {
      node: { key, fullInfo },
    } = info;
    setSelectedKeys([key] as any);
    setFormValues(fullInfo);
    setClickedNode((prevState) => ({ ...prevState, clickedNode: info }));
    handleUpdateModalVisible(true);
    setDisabled(true);
    setIsRootMenu(fullInfo.parentId === -1);
    handleModalVisible(false);
  };

  const renderRightCardExtra = () => {
    return (
      <>
        {updateModalVisible && formValues && disabled && (
          <Button
            size={'large'}
            type={'primary'}
            onClick={() => setDisabled(false)}
          >
            {l('button.edit')}
          </Button>
        )}
        {updateModalVisible && formValues && !disabled && (
          <Button
            size={'large'}
            type={'dashed'}
            onClick={() => setDisabled(true)}
          >
            {l('button.cancel')}
          </Button>
        )}
      </>
    );
  };

  /**
   * render the right content
   * @returns {JSX.Element}
   */
  const renderRightContent = () => {
    // default
    if (!updateModalVisible && !modalVisible) {
      return (
        <>
          <OpHelper />
        </>
      );
    }
    // update
    if (formValues && updateModalVisible) {
      return (
        <>
          <MenuForm
            selectedKeys={selectedKeys}
            isRootMenu={isRootMenu}
            treeData={treeData}
            disabled={disabled}
            clickNode={clickedNode.clickedNode}
            values={formValues}
            onCancel={handleCancel}
            open={updateModalVisible}
            onSubmit={(value: Partial<SysMenu>) =>
              handleAddOrUpdateSubmit(value)
            }
          />
        </>
      );
    }
    // add
    if (modalVisible) {
      return (
        <>
          <MenuForm
            selectedKeys={selectedKeys}
            clickNode={clickedNode.rightClickedNode}
            isRootMenu={isRootMenu}
            treeData={treeData}
            values={{}}
            open={modalVisible}
            onCancel={handleCancel}
            onSubmit={(value: Partial<SysMenu>) =>
              handleAddOrUpdateSubmit(value)
            }
          />
        </>
      );
    }
  };

  /**
   * create root menu
   */
  const handleCreateRoot = () => {
    handleUpdateModalVisible(false);
    handleModalVisible(true);
    setIsRootMenu(true);
    setFormValues({});
  };

  const renderLeftExtra = () => {
    return (
      <Space>
        <Button
          size={'small'}
          key={'added-menu'}
          icon={<PlusSquareTwoTone />}
          type={'primary'}
          onClick={() => handleCreateRoot()}
        >
          {l('right.menu.createRoot')}
        </Button>
        <Button
          size={'small'}
          key={'refresh-menu'}
          icon={<ReloadOutlined />}
          type={'primary'}
          onClick={() => queryMenuData()}
        >
          {l('button.refresh')}
        </Button>
      </Space>
    );
  };

  const renderAddSubMenuTitle = () => {
    return (
      <>
        {formValues.id && updateModalVisible
          ? l('menu.edit')
          : !formValues.id && modalVisible && !isRootMenu
          ? l('right.menu.addSub')
          : !formValues.id && modalVisible && isRootMenu
          ? l('right.menu.createRoot')
          : ''}
      </>
    );
  };

  /**
   * render
   */
  return (
    <>
      <ProCard size={'small'}>
        <ProCard
          extra={renderLeftExtra()}
          title={l('menu.management')}
          ghost
          hoverable
          colSpan={'30%'}
          className={'siderTree schemaTree'}
        >
          <MenuTree
            loading={loading}
            selectedKeys={selectedKeys}
            treeData={treeData}
            onRightClick={handleRightClick}
            onNodeClick={(info: any) => handleNodeClick(info)}
          />
        </ProCard>
        <ProCard.Divider type={'vertical'} />
        <ProCard
          extra={renderRightCardExtra()}
          title={renderAddSubMenuTitle()}
          ghost
          hoverable
          className={'schemaTree'}
        >
          {renderRightContent()}
        </ProCard>
      </ProCard>

      <RightContextMenu
        contextMenuPosition={contextMenuPosition}
        open={contextMenuVisible}
        openChange={() => setContextMenuVisible(false)}
        items={RIGHT_CONTEXT_MENU(clickedNode.rightClickedNode?.type === 'F')}
        onClick={handleMenuClick}
      />
    </>
  );
};
export default MenuList;

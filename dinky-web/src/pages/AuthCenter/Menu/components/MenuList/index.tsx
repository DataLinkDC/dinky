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
import { Authorized, AuthorizedObject, useAccess } from '@/hooks/useAccess';
import MenuForm from '@/pages/AuthCenter/Menu/components/MenuForm';
import { RIGHT_CONTEXT_MENU } from '@/pages/AuthCenter/Menu/components/MenuList/constants';
import OpHelper from '@/pages/AuthCenter/Menu/components/MenuList/OpHelper';
import MenuTree from '@/pages/AuthCenter/Menu/components/MenuTree';
import { handleAddOrUpdate, handleRemoveById, queryDataByParams } from '@/services/BusinessCrud';
import { API_CONSTANTS } from '@/services/endpoints';
import { SysMenu } from '@/types/AuthCenter/data.d';
import { InitMenuState } from '@/types/AuthCenter/init.d';
import { MenuState } from '@/types/AuthCenter/state.d';
import { PermissionConstants } from '@/types/Public/constants';
import { l } from '@/utils/intl';
import { PlusSquareTwoTone, ReloadOutlined } from '@ant-design/icons';
import { ProCard } from '@ant-design/pro-components';
import { Button, Space } from 'antd';
import { MenuInfo } from 'rc-menu/es/interface';
import React, { useEffect, useState } from 'react';

const MenuList: React.FC = () => {
  const [menuState, setMenuState] = useState<MenuState>(InitMenuState);
  const access = useAccess();

  const executeAndCallbackRefresh = async (callback: () => void) => {
    setMenuState((prevState) => ({ ...prevState, loading: true }));
    await callback();
    setMenuState((prevState) => ({ ...prevState, loading: false }));
  };

  /**
   * query
   */
  const queryMenuData = async () => {
    executeAndCallbackRefresh(async () => {
      await queryDataByParams(API_CONSTANTS.MENU_LIST).then((res) =>
        setMenuState((prevState) => ({ ...prevState, menuTreeData: res as SysMenu[] }))
      );
    });
  };

  useEffect(() => {
    queryMenuData();
  }, []);

  /**
   * delete role by id
   * @param id role id
   */
  const handleDeleteSubmit = async () => {
    await executeAndCallbackRefresh(async () =>
      handleRemoveById(
        API_CONSTANTS.MENU_DELETE,
        menuState.clickNode?.rightClickedNode.key as number
      )
    );
    setMenuState((prevState) => ({ ...prevState, contextMenuOpen: false }));
    queryMenuData();
  };

  /**
   * add or update role submit callback
   * @param value
   */
  const handleAddOrUpdateSubmit = async (value: Partial<SysMenu>) => {
    return await handleAddOrUpdate(
      API_CONSTANTS.MENU_ADD_OR_UPDATE,
      { ...value },
      () => {
        setMenuState((prevState) => ({ ...prevState, loading: true }));
      },
      () => {
        setMenuState((prevState) => ({ ...prevState, addedOpen: false, loading: false }));
        queryMenuData();
      }
    );
  };

  /**
   * cancel
   */
  const handleCancel = () => {
    setMenuState((prevState) => ({
      ...prevState,
      addedOpen: false,
      editOpen: false,
      contextMenuOpen: false
    }));
  };

  /**
   * create sub menu callback
   */
  const handleCreateSubMenu = () => {
    setMenuState((prevState) => ({
      ...prevState,
      addedOpen: true,
      editOpen: false,
      contextMenuOpen: false,
      isRootMenu: false,
      isEditDisabled: false,
      sysMenuValue: {}
    }));
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
    setMenuState((prevState) => ({
      ...prevState,
      contextMenuOpen: true,
      selectedKeys: [node.key],
      clickNode: { ...prevState.clickNode, rightClickedNode: node },
      contextMenuPosition: {
        ...prevState.contextMenuPosition,
        top: event.clientY + 20,
        left: event.clientX + 20
      }
    }));
  };

  const handleNodeClick = async (info: any) => {
    const {
      node: { key, fullInfo }
    } = info;

    setMenuState((prevState) => ({
      ...prevState,
      selectedKeys: [key],
      clickNode: { ...prevState.clickNode, oneClickedNode: info },
      sysMenuValue: fullInfo,
      editOpen: true,
      addedOpen: false,
      isEditDisabled: true,
      isRootMenu: fullInfo.parentId === -1
    }));
  };

  const renderRightCardExtra = () => {
    const { editOpen, sysMenuValue, isEditDisabled } = menuState;
    return (
      <>
        <Authorized key={`edit_auth`} path={PermissionConstants.AUTH_MENU_EDIT}>
          <>
            {editOpen && sysMenuValue && isEditDisabled && (
              <Button
                size={'small'}
                type={'primary'}
                onClick={() =>
                  setMenuState((prevState) => ({ ...prevState, isEditDisabled: false }))
                }
              >
                {l('button.edit')}
              </Button>
            )}
          </>
        </Authorized>
        <>
          {editOpen && sysMenuValue && !isEditDisabled && (
            <Button
              size={'small'}
              type={'dashed'}
              onClick={() => setMenuState((prevState) => ({ ...prevState, isEditDisabled: true }))}
            >
              {l('button.cancel')}
            </Button>
          )}
        </>
      </>
    );
  };

  /**
   * render the right content
   * @returns {JSX.Element}
   */
  const renderRightContent = () => {
    const {
      addedOpen,
      editOpen,
      selectedKeys,
      isRootMenu,
      menuTreeData,
      sysMenuValue,
      isEditDisabled
    } = menuState;

    // default
    if (!editOpen && !addedOpen) {
      return (
        <>
          <OpHelper />
        </>
      );
    }
    // update
    if (sysMenuValue && editOpen) {
      return (
        <>
          <MenuForm
            selectedKeys={selectedKeys}
            isRootMenu={isRootMenu}
            treeData={menuTreeData}
            disabled={isEditDisabled}
            values={sysMenuValue}
            onCancel={handleCancel}
            open={editOpen}
            onSubmit={async (value: Partial<SysMenu>): Promise<boolean> =>
              await handleAddOrUpdateSubmit(value)
            }
          />
        </>
      );
    }
    // add
    if (addedOpen) {
      return (
        <>
          <MenuForm
            selectedKeys={selectedKeys}
            isRootMenu={isRootMenu}
            treeData={menuTreeData}
            disabled={isEditDisabled}
            values={{}}
            open={addedOpen}
            onCancel={handleCancel}
            onSubmit={(value: Partial<SysMenu>) => handleAddOrUpdateSubmit(value)}
          />
        </>
      );
    }
  };

  /**
   * create root menu
   */
  const handleCreateRoot = () => {
    setMenuState((prevState) => ({
      ...prevState,
      addedOpen: true,
      editOpen: false,
      contextMenuOpen: false,
      isRootMenu: true,
      sysMenuValue: {}
    }));
  };

  const renderLeftExtra = () => {
    return (
      <Space>
        <Authorized key={`added_auth`} path={PermissionConstants.AUTH_MENU_ADD_ROOT}>
          <Button
            size={'small'}
            key={'added-menu'}
            icon={<PlusSquareTwoTone />}
            type={'primary'}
            onClick={() => handleCreateRoot()}
          >
            {l('right.menu.createRoot')}
          </Button>
        </Authorized>
        <Authorized key={`refresh_auth`} path={PermissionConstants.AUTH_MENU_REFRESH}>
          <Button
            size={'small'}
            key={'refresh-menu'}
            icon={<ReloadOutlined />}
            type={'primary'}
            onClick={() => queryMenuData()}
          >
            {l('button.refresh')}
          </Button>
        </Authorized>
      </Space>
    );
  };

  const renderAddSubMenuTitle = () => {
    const { sysMenuValue, editOpen, addedOpen, isRootMenu } = menuState;
    return (
      <>
        {sysMenuValue?.id && editOpen
          ? l('menu.edit')
          : !sysMenuValue?.id && addedOpen && !isRootMenu
          ? l('right.menu.addSub')
          : !sysMenuValue?.id && addedOpen && isRootMenu
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
          colSpan={'40%'}
          className={'siderTree schemaTree'}
        >
          <MenuTree
            loading={menuState.loading}
            selectedKeys={menuState.selectedKeys}
            treeData={menuState.menuTreeData}
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
        contextMenuPosition={menuState.contextMenuPosition}
        open={menuState.contextMenuOpen}
        openChange={() => setMenuState((prevState) => ({ ...prevState, contextMenuOpen: false }))}
        items={RIGHT_CONTEXT_MENU(menuState.clickNode.rightClickedNode?.type === 'F').filter(
          (menu) => !!!menu.path || !!AuthorizedObject({ path: menu.path, children: menu, access })
        )}
        onClick={handleMenuClick}
      />
    </>
  );
};
export default MenuList;

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

import { buildMenuTree, sortTreeData } from '@/pages/AuthCenter/Menu/function';
import { queryDataByParams } from '@/services/BusinessCrud';
import { API_CONSTANTS } from '@/services/endpoints';
import { SysMenu, UserBaseInfo } from '@/types/AuthCenter/data.d';
import { InitRoleAssignMenuState } from '@/types/AuthCenter/init.d';
import { RoleAssignMenuState } from '@/types/AuthCenter/state.d';
import { l } from '@/utils/intl';
import { Key } from '@ant-design/pro-components';
import { Button, Drawer, Empty, Input, Space, Spin, Tree } from 'antd';
import { DataNode } from 'antd/es/tree';
import React, { useCallback, useEffect, useState } from 'react';

type AssignMenuProps = {
  values: Partial<UserBaseInfo.Role>;
  open: boolean;
  onClose: () => void;
  onSubmit: (values: Key[]) => void;
};

interface TreeDataNode extends DataNode {
  value: number;
}

const { DirectoryTree } = Tree;

const AssignMenu: React.FC<AssignMenuProps> = (props) => {
  const { open, onClose, onSubmit, values } = props;

  const [roleAssignMenu, setRoleAssignMenu] =
    useState<RoleAssignMenuState>(InitRoleAssignMenuState);

  useEffect(() => {
    if (open) {
      queryDataByParams(
        API_CONSTANTS.ROLE_MENU_LIST,
        { id: values.id },
        () => setRoleAssignMenu((prevState) => ({ ...prevState, loading: true })),
        () => setRoleAssignMenu((prevState) => ({ ...prevState, loading: false }))
      ).then((res) =>
        setRoleAssignMenu((prevState) => ({
          ...prevState,
          menuTreeData: res as {
            menus: SysMenu[];
            selectedMenuIds: number[];
          }
        }))
      );
    }
  }, [values, open]);

  /**
   * close
   */
  const handleClose = useCallback(() => {
    onClose();
    setRoleAssignMenu((prevState) => ({
      ...prevState,
      menuTreeData: InitRoleAssignMenuState.menuTreeData
    }));
  }, [values]);

  /**
   * when submit menu , use loading
   */
  const handleSubmit = async () => {
    setRoleAssignMenu((prevState) => ({ ...prevState, loading: true }));
    await onSubmit(roleAssignMenu.selectValue);
    setRoleAssignMenu((prevState) => ({
      ...prevState,
      loading: false,
      menuTreeData: InitRoleAssignMenuState.menuTreeData
    }));
  };

  /**
   * render extra buttons
   */
  const renderExtraButtons = () => {
    return (
      <>
        <Space>
          <Button onClick={handleClose}>{l('button.cancel')}</Button>
          <Button type='primary' loading={roleAssignMenu.loading} onClick={handleSubmit}>
            {l('button.submit')}
          </Button>
        </Space>
      </>
    );
  };

  /**
   * search tree node
   */
  const onSearchChange = useCallback(
    (e: { target: { value: string } }) => {
      setRoleAssignMenu((prevState) => ({ ...prevState, searchValue: e.target.value }));
    },
    [roleAssignMenu.searchValue]
  );

  const onCheck = (selectKeys: any) => {
    setRoleAssignMenu((prevState) => ({ ...prevState, selectValue: selectKeys }));
  };

  const treeData = buildMenuTree(
    sortTreeData(roleAssignMenu.menuTreeData.menus),
    roleAssignMenu.searchValue
  );

  const treeToArray = (list: TreeDataNode[], newArr: TreeDataNode[] = []) => {
    list.forEach((item) => {
      const { children } = item;
      if (children) {
        if (children.length) {
          newArr.push(item);
          return treeToArray(children as TreeDataNode[], newArr);
        }
      }
      newArr.push(item);
    });
    return newArr;
  };

  const filterHalfKeys: any = useCallback(
    (keys: Key[]) => {
      const treeArray = treeToArray(treeData);
      return keys.filter((key) => treeArray.some((tree) => tree.value == key && tree.isLeaf));
    },
    [treeData]
  );

  return (
    <Drawer
      title={l('role.assignMenu', '', { roleName: values.roleName })}
      open={open}
      width={'55%'}
      maskClosable={false}
      onClose={handleClose}
      extra={renderExtraButtons()}
    >
      {roleAssignMenu.menuTreeData.menus.length > 0 ? (
        <>
          <Input
            placeholder={l('global.search.text')}
            allowClear
            style={{ marginBottom: 16 }}
            value={roleAssignMenu.searchValue}
            onChange={onSearchChange}
          />
          <Spin spinning={roleAssignMenu.loading}>
            <DirectoryTree
              selectable
              defaultCheckedKeys={filterHalfKeys(roleAssignMenu.menuTreeData.selectedMenuIds)}
              checkable
              defaultExpandAll
              // @ts-ignore
              onCheck={(keys: Key[], e: { halfCheckedKeys: Key[] }) => {
                onCheck(keys?.concat(e.halfCheckedKeys));
              }}
              multiple={true}
              className={'treeList'}
              treeData={treeData}
            />
          </Spin>
        </>
      ) : (
        <Empty className={'code-content-empty'} />
      )}
    </Drawer>
  );
};

export default AssignMenu;

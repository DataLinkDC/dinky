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


import React, {useRef, useState} from "react";
import {EditTwoTone, PlusOutlined} from '@ant-design/icons';
import ProTable, {ActionType, ProColumns} from "@ant-design/pro-table";
import {Button, Popconfirm, Tag} from 'antd';
import {PageContainer} from '@ant-design/pro-layout';
import {l} from "@/utils/intl";
import {handleAddOrUpdate, handleRemoveById} from "@/services/BusinessCrud";
import {queryList} from "@/services/api";
import {API_CONSTANTS, PROTABLE_OPTIONS_PUBLIC} from "@/services/constants";
import RoleForm from "./components/RoleForm";
import {DangerDeleteIcon} from "@/components/Icons/CustomIcons";
import {getTenantByLocalStorage} from "@/utils/function";


const RoleList: React.FC = () => {
  /**
   * status
   */
  const [formValues, setFormValues] = useState<any>();
  const [modalVisible, handleModalVisible] = useState<boolean>(false);
  const [updateModalVisible, handleUpdateModalVisible] = useState<boolean>(false);
  const actionRef = useRef<ActionType>();


  /**
   * delete role by id
   * @param id role id
   */
  const handleDeleteSubmit = async (id: number) => {
    // TODO: DELETE role interface is use /api/role  , because of the backend interface 'DeleteMapping' is repeat , in the future, we need to change the interface to /api/role (ROLE)
    await handleRemoveById(API_CONSTANTS.ROLE_DELETE, id);
    actionRef.current?.reload?.();
  }


  const handleAddOrUpdateSubmit = async (value :any ) => {
    // TODO: added or update role interface is use /api/role/addedOrUpdateRole  , because of the backend interface 'saveOrUpdate' is repeat , in the future, we need to change the interface to /api/role (ROLE)
    const success = await handleAddOrUpdate(API_CONSTANTS.ROLE_ADDED_OR_UPDATE, {...value, tenantId: getTenantByLocalStorage()});
    if (success) {
      handleModalVisible(false);
      setFormValues({});
      if (actionRef.current) {
        actionRef.current.reload();
      }
    }
  }


  /**
   * columns
   */
  const columns: ProColumns<UserBaseInfo.Role>[] = [
    {
      title: l('role.roleCode'),
      dataIndex: 'roleCode',
    },
    {
      title: l('role.roleName'),
      dataIndex: 'roleName',
    },
    {
      title: l('role.belongTenant'),
      hideInSearch: true,
      render: (_, record) => {
        return <Tag color="blue">{record.tenant.tenantCode}</Tag>
      }
    },
    {
      title: l('global.table.note'),
      dataIndex: 'note',
      hideInSearch: true,
      ellipsis: true,
    },
    {
      title: l('global.table.createTime'),
      dataIndex: 'createTime',
      sorter: true,
      hideInSearch: true,
      valueType: 'dateTime',
    },
    {
      title: l('global.table.updateTime'),
      dataIndex: 'updateTime',
      sorter: true,
      hideInSearch: true,
      valueType: 'dateTime',
    },
    {
      title: l('global.table.operate'),
      valueType: 'option',
      width: "20vh",
      render: (_, record) => [
        <Button
          className={"options-button"}
          key={"RoleEdit"}
          icon={<EditTwoTone/>}
          title={l("button.edit")}
          onClick={() => {
            setFormValues(record);
            handleUpdateModalVisible(true);
          }}
        />,
        <>
          {record.id !== 1 &&
            <Popconfirm
              placement="topRight"
              title={l("button.delete")}
              description={l("role.deleteConfirm")}
              onConfirm={() => {
                handleDeleteSubmit(record.id);
              }}
              okText={l("button.confirm")}
              cancelText={l("button.cancel")}
            >
              <Button key={'deleteRoleIcon'} icon={<DangerDeleteIcon/>}/>
            </Popconfirm>
          }
        </>,
      ],
    },
  ];


  /**
   * render
   */
  return (
    <PageContainer title={false}>
      <ProTable<UserBaseInfo.Role>
        {...PROTABLE_OPTIONS_PUBLIC}
        headerTitle={l('role.roleManagement')}
        actionRef={actionRef}
        toolBarRender={() => [
          <Button type="primary" onClick={() => handleModalVisible(true)}>
            <PlusOutlined/> {l('button.create')}
          </Button>,
        ]}
        request={(params, sorter, filter: any) => queryList(API_CONSTANTS.ROLE, {...params, sorter, filter})}
        columns={columns}
      />
      <RoleForm
        onSubmit={(value: any) => {handleAddOrUpdateSubmit(value)}}
        onCancel={() => {
          handleModalVisible(false);
        }}
        modalVisible={modalVisible}
        values={{}}
      />
      {formValues && Object.keys(formValues).length ? (
          <RoleForm
            onSubmit={(value: any) => {handleAddOrUpdateSubmit(value)}}
            onCancel={() => {
              handleUpdateModalVisible(false);
              setFormValues({});
            }}
            modalVisible={updateModalVisible}
            values={formValues}
          />
        ) : undefined
      }
    </PageContainer>
  );
};

export default RoleList;

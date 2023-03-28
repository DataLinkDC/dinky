/*
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
import {
  EditTwoTone,
  PlusOutlined,
  UserSwitchOutlined
} from '@ant-design/icons';
import ProTable, {ActionType, ProColumns} from "@ant-design/pro-table";
import {Button, Form, Modal, Popconfirm} from 'antd';
import {PageContainer} from '@ant-design/pro-layout';
import TenantForm from "@/pages/AuthCenter/Tenant/components/TenantForm";
import {l} from "@/utils/intl";
import {handleAddOrUpdate, handleRemoveById} from "@/services/BusinessCrud";
import {queryList} from "@/services/api";
import {API_CONSTANTS, PROTABLE_OPTIONS_PUBLIC} from "@/services/constants";
import TenantTransfer from "@/pages/AuthCenter/Tenant/components/TenantTransfer";
import {DangerDeleteIcon} from "@/components/Icons/CustomIcons";

const url = '/api/tenant';
const TenantFormList: React.FC<{}> = (props: any) => {
  const [handleGrantTenant, setHandleGrantTenant] = useState<boolean>(false);
  const [tenantRelFormValues, setTenantRelFormValues] = useState({});
  const [modalVisible, handleModalVisible] = useState<boolean>(false);
  const [updateModalVisible, handleUpdateModalVisible] = useState<boolean>(false);
  const actionRef = useRef<ActionType>();
  const [formValues, setFormValues] = useState<any>({});
  const [form] = Form.useForm();


  const handleAddSubmit = async (value: UserBaseInfo.Tenant) => {
    const success = await handleAddOrUpdate(url, value);
    if (success) {
      handleModalVisible(false);
      setFormValues({});
      if (actionRef.current) {
        actionRef.current.reload();
        form.resetFields();
      }
    }
  };
  const handleEditSubmit = async (value: UserBaseInfo.Tenant) => {
    const success = await handleAddOrUpdate(url, value);
    if (success) {
      handleUpdateModalVisible(false);
      setFormValues({});
      if (actionRef.current) {
        actionRef.current.reload();
      }
    }
  };
  const handleDeleteSubmit = async (id : number) => {
    await handleRemoveById(url, id);
    actionRef.current?.reloadAndRest?.();
  };
  const handleAssignUserSubmit = async () => {
    // to save
    const success = await handleAddOrUpdate(API_CONSTANTS.ASSIGN_USER_TO_TENANT, {
      tenantId: formValues.id as number,
      users: tenantRelFormValues
    });
    if (success) {
      setHandleGrantTenant(false);
      setFormValues({});
      if (actionRef.current) {
        actionRef.current.reload();
      }
    }

  }
  const handleGrantTenantForm = () => {
    return (
      <Modal
        title={l('tenant.AssignUser')}
        open={handleGrantTenant}
        destroyOnClose={true}
        width={"90%"}
        onCancel={() => {
          setHandleGrantTenant(false);
        }}
        onOk={handleAssignUserSubmit}
        >
        <TenantTransfer tenant={formValues} onChange={(value) => {
          setTenantRelFormValues(value);
        }}/>
      </Modal>
    )
  }

  const columns: ProColumns<UserBaseInfo.Tenant>[] = [
    {
      title: l('tenant.TenantCode'),
      dataIndex: 'tenantCode',
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
      valueType: 'dateTime',
    },
    {
      title: l('global.table.updateTime'),
      dataIndex: 'updateTime',
      valueType: 'dateTime',
    },
    {
      title: l('global.table.operate'),
      valueType: 'option',
      width: "20vh",
      render: (_, record: UserBaseInfo.Tenant) => [
        <Button
          className={"options-button"}
          key={"edit"}
          icon={<EditTwoTone/>}
          title={l("button.edit")}
          onClick={() => {
            handleUpdateModalVisible(true);
            setFormValues(record);
          }}
        />,
        <Button
          className={"options-button"}
          key={"AssignUser"}
          title={l('tenant.AssignUser')}
          icon={<UserSwitchOutlined className={"blue-icon"}/>}
          onClick={() => {
            setHandleGrantTenant(true);
            setFormValues(record);
          }}
        />,
        <>
          {record.tenantCode !== "DefaultTenant" &&
            <Popconfirm
              placement="topRight"
              title={l("button.delete")}
              description={l("tenant.deleteConfirm")}
              onConfirm={() => {handleDeleteSubmit(record.id);}}
              okText={l("button.confirm")}
              cancelText={l("button.cancel")}
            >
              <Button key={'deleteTenantIcon'} icon={<DangerDeleteIcon/>}/>
            </Popconfirm>
          }
        </>,
      ],
    },
  ];

  return (
    <PageContainer title={false}>
      {/*table*/}
      <ProTable<UserBaseInfo.Tenant>
        {...PROTABLE_OPTIONS_PUBLIC}
        headerTitle={l('tenant.TenantManager')}
        actionRef={actionRef}
        toolBarRender={() => [
          <Button type="primary" onClick={() => handleModalVisible(true)}>
            <PlusOutlined/> {l('button.create')}
          </Button>,
        ]}
        request={(params, sorter, filter: any) => queryList(url, {...params, sorter, filter})}
        columns={columns}
      />

      {/*add tenant form*/}
      <TenantForm
        onSubmit={(value) => {handleAddSubmit(value as UserBaseInfo.Tenant)}}
        onCancel={() => {handleModalVisible(false);}}
        modalVisible={modalVisible}
        values={{}}
      />

      {
        formValues && Object.keys(formValues).length ? (
          <TenantForm
            onSubmit={async (value) => {handleEditSubmit(value as UserBaseInfo.Tenant);}}
            onCancel={() => {
              handleUpdateModalVisible(false);
              setFormValues({});
            }}
            modalVisible={updateModalVisible}
            values={formValues}
          />
        ) : undefined
      }
      {handleGrantTenantForm()}
    </PageContainer>
  );
};

export default TenantFormList;

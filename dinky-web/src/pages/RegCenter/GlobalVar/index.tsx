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

import {EditTwoTone, PlusOutlined} from '@ant-design/icons';
import {Button, Drawer, Popconfirm, Space, Switch} from 'antd';
import React, {useRef, useState} from 'react';
import {PageContainer} from '@ant-design/pro-layout';
import type {ActionType, ProColumns} from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import ProDescriptions from '@ant-design/pro-descriptions';
import {l} from "@/utils/intl";
import {GlobalVar} from "@/types/RegCenter/data";
import {
  API_CONSTANTS,
  PROTABLE_OPTIONS_PUBLIC,
  STATUS_ENUM,
  STATUS_MAPPING,
  SWITCH_OPTIONS
} from "@/services/constants";
import {DangerDeleteIcon} from "@/components/Icons/CustomIcons";
import {handleAddOrUpdate, handleRemoveById, updateEnabled} from "@/services/BusinessCrud";
import GlobalVarForm from "@/pages/RegCenter/GlobalVar/components/GlobalVarForm";
import {queryList} from "@/services/api";
import CodeShow from "@/components/CustomMonacoEditor/CodeShow";

const GlobalVarList: React.FC = (props: any) => {
  /**
   * state
   */
  const [modalVisible, handleModalVisible] = useState<boolean>(false);
  const [updateModalVisible, handleUpdateModalVisible] = useState<boolean>(false);
  const [formValues, setFormValues] = useState({});
  const actionRef = useRef<ActionType>();
  const [row, setRow] = useState<GlobalVar>();
  const [loading, setLoading] = useState<boolean>(false);


  /**
   * var enable or disable
   * @param value
   */
  const handleChangeEnable = async (value: GlobalVar) => {
    setLoading(true);
    await updateEnabled(API_CONSTANTS.GLOBAL_VARIABLE_ENABLE, {id: value.id});
    setLoading(false);
    actionRef.current?.reload?.();
  };

  /**
   * delete role by id
   * @param id role id
   */
  const handleDeleteSubmit = async (id: number) => {
    await handleRemoveById(API_CONSTANTS.GLOBAL_VARIABLE_DELETE, id);
    actionRef.current?.reload?.();
  }


  /**
   * added global var
   * @param value
   */
  const handleAddSubmit = async (value: GlobalVar) => {
    const success = await handleAddOrUpdate(API_CONSTANTS.GLOBAL_VARIABLE, value);
    if (success) {
      handleModalVisible(!modalVisible);
      setFormValues({});
      if (actionRef.current) {
        actionRef.current.reload();
      }
    }
  }

  /**
   * update global var
   * @param value
   */
  const handleUpdateSubmit = async (value: GlobalVar) => {
    const success = await handleAddOrUpdate(API_CONSTANTS.GLOBAL_VARIABLE, value);
    if (success) {
      handleUpdateModalVisible(!updateModalVisible);
      setFormValues({});
      if (actionRef.current) {
        actionRef.current.reload();
      }
    }
  }

  /**
   * columns
   */
  const columns: ProColumns<GlobalVar>[] = [
    {
      title: l('rc.gv.name'),
      dataIndex: 'name',
      sorter: true,
      render: (dom, entity) => {
        return <a onClick={() => setRow(entity)}>{dom}</a>;
      },
    },
    {
      title: l('rc.gv.value'),
      dataIndex: 'fragmentValue',
      hideInTable: true,
      render: (_, record) => {
        return <CodeShow code={record.fragmentValue} />
      },
    },
    {
      title: l('global.table.note'),
      dataIndex: 'note',
      valueType: 'textarea',
    },
    {
      title: l("global.table.isEnable"),
      dataIndex: "enabled",
      hideInSearch: true,
      width: '15vh',
      render: (_, record) => {
        return <>
          <Space>
            <Switch
              key={record.id}
              {...SWITCH_OPTIONS()}
              checked={record.enabled}
              onChange={() => handleChangeEnable(record)}/>
          </Space>
        </>;
      },
      filters: STATUS_MAPPING(),
      filterMultiple: false,
      valueEnum: STATUS_ENUM(),
    },
    {
      title: l('global.table.createTime'),
      dataIndex: 'createTime',
      hideInSearch: true,
      width: '20vh',
      sorter: true,
      valueType: 'dateTime',
    },
    {
      title: l('global.table.lastUpdateTime'),
      dataIndex: 'updateTime',
      width: '20vh',
      hideInSearch: true,
      sorter: true,
      valueType: 'dateTime',
    },
    {
      title: l('global.table.operate'),
      width: '20vh',
      valueType: 'option',
      render: (_, record) => [
        <Button
          className={"options-button"}
          key={"GlobalVarEdit"}
          icon={<EditTwoTone/>}
          title={l("button.edit")}
          onClick={() => {
            setFormValues(record);
            handleUpdateModalVisible(true);
          }}
        />,
        <Popconfirm
          key={'GlobalVarDelete'}
          placement="topRight"
          title={l("button.delete")}
          description={l("rc.gv.deleteConfirm")}
          onConfirm={() => {
            handleDeleteSubmit(record.id);
          }}
          okText={l("button.confirm")}
          cancelText={l("button.cancel")}
        >
          <Button key={'DeleteGlobalVarIcon'} icon={<DangerDeleteIcon/>}/>
        </Popconfirm>
      ],
    },
  ];


  /**
   * render
   */
  return (
    <PageContainer title={false}>
      {/*table*/}
      <ProTable<GlobalVar>
        headerTitle={l('rc.gv.Management')}
        actionRef={actionRef}
        loading={loading}
        {...PROTABLE_OPTIONS_PUBLIC}
        toolBarRender={() => [
          <Button type="primary" onClick={() => handleModalVisible(true)}>
            <PlusOutlined/> {l('button.create')}
          </Button>,
        ]}
        request={(params, sorter, filter:any) => queryList(API_CONSTANTS.GLOBAL_VARIABLE, {...params, sorter, filter})}
        columns={columns}
      />

      {/*add*/}
      <GlobalVarForm
        onSubmit={ (value: GlobalVar) => {handleAddSubmit(value)}}
        onCancel={() => {
          handleModalVisible(!modalVisible)
        }}
        modalVisible={modalVisible}
        values={{}}
      />
      {/*update*/}
      {formValues && Object.keys(formValues).length ? (
          <GlobalVarForm
            onSubmit={ (value:GlobalVar) => {handleUpdateSubmit(value)}}
            onCancel={() => {
              handleUpdateModalVisible(!updateModalVisible)
              setFormValues({});
            }}
            modalVisible={updateModalVisible}
            values={formValues}
          />
        ) : null
      }
      {/*drawer render*/}
      <Drawer extra={undefined}
        width={600}
        visible={!!row}
        onClose={() => {
          setRow(undefined);
        }}
        closable={false}
      >
        {row?.name && (
          <ProDescriptions<GlobalVar>
            column={1}
            title={row.name}
            request={async () => ({
              data: row,
            })}
            params={{
              id: row.id,
            }}
            columns={columns as any}
          />
        )}
      </Drawer>
    </PageContainer>
  );
};
export default GlobalVarList;

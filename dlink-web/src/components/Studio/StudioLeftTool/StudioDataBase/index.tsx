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


import {Button, Drawer, Empty, Modal, Table, Tooltip} from "antd";
import {StateType} from "@/pages/DataStudio/model";
import {connect} from "umi";
import React, {useState} from "react";
import {PlusOutlined, ReloadOutlined} from '@ant-design/icons';
import {showDataBase} from "../../StudioEvent/DDL";
import DBForm from "@/pages/RegistrationCenter/DataBase/components/DBForm";
import {Scrollbars} from 'react-custom-scrollbars';
import ProDescriptions from "@ant-design/pro-descriptions";
import {handleRemove} from "@/components/Common/crud";
import {l} from "@/utils/intl";

const StudioDataBase = (props: any) => {

  const {database, toolHeight, dispatch} = props;
  const [chooseDBModalVisible, handleDBFormModalVisible] = useState<boolean>(false);
  const [values, setValues] = useState<any>({});
  const [row, setRow] = useState<{}>();

  const getColumns = () => {
    return [{
      title: "数据源名",
      dataIndex: "alias",
      key: "alias",
      sorter: true,
      render: (dom, entity) => {
        return <a onClick={() => setRow(entity)}>{entity.alias === "" ? entity.name : entity.alias}</a>;
      },
    }];
  };

  const getAllColumns = () => {
    return [{
      title: "ID",
      dataIndex: "id",
      key: "id",
    }, {
      title: "数据源名",
      dataIndex: "alias",
    }, {
      title: '唯一标识',
      dataIndex: 'name',
    },
      {
        title: '分组类型',
        dataIndex: 'groupName',
        filters: [
          {
            text: '来源',
            value: '来源',
          },
          {
            text: '数仓',
            value: '数仓',
          },
          {
            text: '应用',
            value: '应用',
          },
          {
            text: '备份',
            value: '备份',
          }, {
            text: '其他',
            value: '其他',
          },
        ],
        filterMultiple: false,
        valueEnum: {
          'yarn-session': {text: 'Yarn Session'},
          'standalone': {text: 'Standalone'},
          'yarn-per-job': {text: 'Yarn Per-Job'},
          'yarn-application': {text: 'Yarn Application'},
        },
      },
      {
        title: 'URL',
        dataIndex: 'url',
        valueType: 'textarea',
      },
      {
        title: '用户名',
        dataIndex: 'username',
      }, {
        title: '版本',
        sorter: true,
        dataIndex: 'dbVersion',
      },
      {
        title: '状态',
        dataIndex: 'status',
        filters: [
          {
            text: '正常',
            value: 1,
          },
          {
            text: '异常',
            value: 0,
          },
        ],
        filterMultiple: false,
        valueEnum: {
          1: {text: '正常', status: 'Success'},
          0: {text: '异常', status: 'Error'},
        },
      },
      {
        title: l('global.table.note'),
        sorter: true,
        valueType: 'textarea',
        dataIndex: 'note',
      },
      {
        title: l('global.table.isEnable'),
        dataIndex: 'enabled',
        filters: [
          {
            text: l('status.enabled'),
            value: 1,
          },
          {
            text: l('status.disabled'),
            value: 0,
          },
        ],
        filterMultiple: false,
        valueEnum: {
          true: {text: l('status.enabled'), status: 'Success'},
          false: {text: l('status.disabled'), status: 'Error'},
        },
      },
      {
        title: '最近的健康时间',
        dataIndex: 'healthTime',
        valueType: 'dateTime',
      }, {
        title: '最近的心跳检测时间',
        dataIndex: 'heartbeatTime',
        valueType: 'dateTime',
      }, {
        title: l('global.table.createTime'),
        dataIndex: 'createTime',
        valueType: 'dateTime',
      },
      {
        title: l('global.table.lastUpdateTime'),
        dataIndex: 'updateTime',
        valueType: 'dateTime',
      },
      {
        title: l('global.table.operate'),
        dataIndex: 'option',
        valueType: 'option',
        render: (_, record) => [
          <Button type="dashed" onClick={() => onModifyDataBase(record)}>
            {l('button.edit')}
          </Button>, <Button danger onClick={() => onDeleteDataBase(record)}>
            {l('button.delete')}
          </Button>
        ],
      },];
  };

  const onRefreshDataBase = () => {
    showDataBase(dispatch);
  };

  const onCreateDataBase = () => {
    setValues({});
    handleDBFormModalVisible(true);
  };

  const onModifyDataBase = (record) => {
    setValues(record);
    handleDBFormModalVisible(true);
  };

  const onDeleteDataBase = (record) => {
    Modal.confirm({
      title: '删除数据源',
      content: `确定删除该数据源【${record.alias === "" ? record.name : record.alias}】吗？`,
      okText: l('button.confirm'),
      cancelText: l('button.cancel'),
      onOk: async () => {
        await handleRemove('api/database', [record]);
        setRow({});
        onRefreshDataBase();
      }
    });
  };

  return (
    <>
      <Tooltip title="新建数据源">
        <Button
          type="text"
          icon={<PlusOutlined/>}
          onClick={onCreateDataBase}
        />
      </Tooltip>
      <Tooltip title="刷新数据源">
        <Button
          type="text"
          icon={<ReloadOutlined/>}
          onClick={onRefreshDataBase}
        />
      </Tooltip>
      <Scrollbars style={{height: (toolHeight - 32)}}>
        {database.length > 0 ? (
          <Table
            dataSource={database}
            columns={getColumns()}
            pagination={{
              defaultPageSize: 10,
              showSizeChanger: true,
            }}
            size="small"/>) : (
          <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>)}
        <DBForm
          onCancel={() => {
            handleDBFormModalVisible(false);
            setValues({});
          }}
          modalVisible={chooseDBModalVisible}
          onSubmit={() => {
            setRow({});
            onRefreshDataBase();
          }}
          values={values}
        />
        <Drawer
          width={600}
          visible={!!row?.id}
          onClose={() => {
            setRow(undefined);
          }}
          closable={false}
        >
          {row?.name && (
            <ProDescriptions
              column={2}
              title={row?.name}
              request={async () => ({
                data: row || {},
              })}
              params={{
                id: row?.name,
              }}
              columns={getAllColumns()}
            />
          )}
        </Drawer>
      </Scrollbars>
    </>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  database: Studio.database,
  toolHeight: Studio.toolHeight,
}))(StudioDataBase);

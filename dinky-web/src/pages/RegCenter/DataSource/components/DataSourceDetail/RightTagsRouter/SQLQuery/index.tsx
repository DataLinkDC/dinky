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

import { QueryParams } from '@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter/data';
import QueryForm from '@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter/SQLQuery/QueryForm';
import { buildColumnsQueryKeyWord } from '@/pages/RegCenter/DataSource/components/function';
import { handleOption } from '@/services/BusinessCrud';
import { PROTABLE_OPTIONS_PUBLIC } from '@/services/constants';
import { API_CONSTANTS } from '@/services/endpoints';
import { l } from '@/utils/intl';
import { ProTable } from '@ant-design/pro-table';
import { Alert, Empty, Form } from 'antd';
import { DefaultOptionType } from 'rc-select/lib/Select';
import React, { useEffect, useState } from 'react';
// props
type SQLQueryProps = {
  queryParams: QueryParams;
};

const SQLQuery: React.FC<SQLQueryProps> = (props) => {
  const {
    queryParams: { id: dbId, schemaName, tableName }
  } = props;

  // state
  const [form] = Form.useForm();
  const [tableData, setTableData] = useState({ columns: [{}], rowData: [{}] });
  const [autoCompleteColumns, setAutoCompleteColumns] = useState<DefaultOptionType[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [responseMsg, setResponseMsg] = useState<{ isErr: boolean; msg: string }>({
    isErr: false,
    msg: ''
  });

  // query data
  const fetchData = async (values: any) => {
    setLoading(true);
    const result = await handleOption(
      API_CONSTANTS.DATASOURCE_QUERY_DATA,
      l('global.getdata.tips'),
      {
        id: dbId,
        schemaName,
        tableName,
        option: {
          where: values.where,
          order: values.order,
          limitStart: '0',
          limitEnd: '500'
        }
      }
    );
    const {
      code,
      data: { columns, rowData }
    } = result ?? {
      code: -1,
      data: { columns: [], rowData: [] }
    }; // 获取到的数据

    if (code && code === 1) {
      setResponseMsg({ isErr: true, msg: result.data.error });
    } else {
      setResponseMsg({ isErr: false, msg: '' });
    }
    // render columns list
    const tableColumns = columns?.map((item: string | number) => ({
      title: item,
      dataIndex: item,
      key: item,
      ellipsis: true,
      tooltip: item,
      width: '8%'
    }));
    setAutoCompleteColumns(buildColumnsQueryKeyWord(columns));
    setTableData({ columns: tableColumns, rowData: rowData });
    setLoading(false);
  };

  /**
   * clear state
   */
  const clearState = () => {
    setTableData({ columns: [], rowData: [] });
    setResponseMsg({ isErr: false, msg: '' });
    setLoading(false);
    form.resetFields();
  };

  useEffect(() => {
    if (dbId && tableName && schemaName) {
      const values = form.getFieldsValue();
      fetchData(values);
    } else {
      clearState();
    }
  }, [dbId, tableName, schemaName, form]);

  /**
   * render alert msg
   */
  const renderAlert = () => {
    return (
      <>
        {responseMsg.isErr ? (
          <Alert message='Error' description={responseMsg.msg} type='error' showIcon />
        ) : (
          <></>
        )}
      </>
    );
  };

  /**
   * render toolbar
   */
  const renderToolBar = () => [
    <QueryForm
      key={'queryForm'}
      autoCompleteColumns={autoCompleteColumns}
      form={form}
      onSubmit={(values) => fetchData(values)}
    />
  ];

  /**
   * render
   */
  return (
    <>
      {dbId && tableName && schemaName ? (
        <ProTable
          bordered
          loading={loading}
          {...PROTABLE_OPTIONS_PUBLIC}
          size={'small'}
          search={false}
          pagination={{
            defaultPageSize: 15,
            hideOnSinglePage: true
          }}
          dateFormatter='string'
          columns={tableData.columns}
          dataSource={tableData.rowData}
          toolBarRender={renderToolBar}
          tableAlertRender={renderAlert}
          options={{
            density: false,
            reload: false,
            fullScreen: true
          }}
        />
      ) : (
        <Empty className={'code-content-empty'} description={l('rc.ds.detail.tips')} />
      )}
    </>
  );
};

export default SQLQuery;

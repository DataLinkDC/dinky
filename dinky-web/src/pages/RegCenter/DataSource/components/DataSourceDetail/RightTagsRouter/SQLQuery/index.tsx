/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React, {useCallback, useEffect, useState} from 'react';
import {ProTable} from '@ant-design/pro-table';
import {API_CONSTANTS, PROTABLE_OPTIONS_PUBLIC} from '@/services/constants';
import {handleOption} from '@/services/BusinessCrud';
import {Alert, Empty, Form} from 'antd';
import {QueryParams} from '@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter/data';
import QueryForm from '@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter/SQLQuery/QueryForm';
import {l} from '@/utils/intl';
import {DefaultOptionType} from 'rc-select/lib/Select';

// props
type SQLQueryProps = {
  queryParams: Partial<QueryParams>
}

const SQLQuery: React.FC<SQLQueryProps> = (props) => {

  const {queryParams: {id: dbId, schemaName, tableName}} = props;

  // state
  const [form] = Form.useForm();
  const [tableData, setTableData] = useState({columns: [{}], rowData: [{}]});
  const [autoCompleteColumns, setAutoCompleteColumns] = useState<DefaultOptionType[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [errMsg, setErrMsg] = useState<{ isErr: boolean, msg: string }>({isErr: false, msg: ''});


  // query data
  const fetchData = async (values: { whereInput: string; orderInput: string; }) => {
    setLoading(true);
    let option = {
      where: values.whereInput ? values.whereInput : '',
      order: values.orderInput ? values.orderInput : '', limitStart: '0', limitEnd: '500'
    };

    const result = await handleOption(API_CONSTANTS.DATASOURCE_QUERY_DATA, l('global.getdata.tips'), {
      id: dbId,
      schemaName,
      tableName,
      sql: 'select * from ' + tableName,
      option: option
    });
    const {code, datas: {columns, rowData}} = result; // 获取到的数据
    if (code === 1) {
      setErrMsg({isErr: true, msg: result.datas.error});
    } else {
      setErrMsg({isErr: false, msg: ''});
    }
    // render columns list
    const tableColumns = columns.map((item: string | number) => ({
      title: item,
      dataIndex: item,
      key: item,
      ellipsis: true,
      tooltip: item,
      width: '8%',
    }));

    let autoComplete: DefaultOptionType[] = [];
    // build autoComplete columns list
    autoComplete = columns.map((item: string | number) => ({
      value: item,
      label: item,
    }));
    setAutoCompleteColumns(autoComplete);
    setTableData({columns: tableColumns, rowData: rowData});
    setLoading(false);
  };

  const clearState = () => {
    setTableData({columns: [], rowData: []});
    setErrMsg({isErr: false, msg: ''});
    setLoading(false);
    form.resetFields();
  };


  useEffect(() => {
    if (dbId && tableName && schemaName) {
      fetchData({whereInput: '', orderInput: ''});
    } else {
      clearState();
    }
  }, [dbId, tableName, schemaName]);


  const renderAlert = () => {
    return <>
      {errMsg.isErr ? (
        <Alert
          message="Error"
          description={errMsg.msg}
          type="error"
          showIcon
        />
      ) : <></>}
    </>;
  };

  /**
   * render toolbar
   */
  const renderToolBar = useCallback(() => {
    return [
      <QueryForm key={'queryForm'} autoCompleteColumns={autoCompleteColumns} form={form}
                 onSubmit={(values => fetchData(values))}/>,];
  }, []);


  /**
   * render
   */
  return <>
    {
      (dbId && tableName && schemaName) ? (
        <ProTable
          bordered
          loading={loading}
          {...PROTABLE_OPTIONS_PUBLIC}
          size={'small'}
          search={false}
          pagination={{
            defaultPageSize: 12,
          }}
          dateFormatter="string"
          columns={tableData.columns}
          dataSource={tableData.rowData}
          toolBarRender={renderToolBar}
          tableAlertRender={renderAlert}
          options={{
            density: false,
            reload: false,
            fullScreen: true,
          }}
        />
      ) : <Empty className={'code-content-empty'} description={l('rc.ds.detail.tips')}/>
    }
  </>;
};

export default SQLQuery;

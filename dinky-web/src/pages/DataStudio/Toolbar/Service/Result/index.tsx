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

import { handleGetOption, handleGetOptionWithoutMsg } from '@/services/BusinessCrud';
import { API_CONSTANTS } from '@/services/endpoints';
import { transformTableDataToCsv } from '@/utils/function';
import { l } from '@/utils/intl';
import { DownloadOutlined, SearchOutlined, SyncOutlined } from '@ant-design/icons';
import { Highlight } from '@ant-design/pro-layout/es/components/Help/Search';
import { Button, Drawer, Empty, Input, InputRef, Space, Tabs, TabsProps } from 'antd';
import { ColumnType } from 'antd/es/table';
import { FilterConfirmProps } from 'antd/es/table/interface';
import { DataIndex } from 'rc-table/es/interface';
import React, { useCallback, useEffect, useRef, useState, useTransition } from 'react';
import { useAsyncEffect } from 'ahooks';
import { DataStudioActionType } from '@/pages/DataStudio/data.d';
import { isSql } from '@/pages/DataStudio/utils';
import { ProTable } from '@ant-design/pro-components';
import { getInsights } from '@antv/ava';
import { InsightCard } from '@antv/ava-react';
import type { Datum, InsightsResult } from '@antv/ava/lib/insight/types';
import { ProColumns } from '@ant-design/pro-table/es/typing';

type TableInfo = {
  tableId: {
    boxName: string;
    catalogName: string;
    databaseName: string;
    tableName: string;
  };
  columns: {
    name: string;
    dataType: string;
  }[];
  rowData: object[];
};

type Data = {
  [c: string]: any;
  columns: string[];
  rowData: object[];
};

export default (props: {
  taskId: number;
  historyId?: number | undefined;
  action: any;
  dialect: string;
}) => {
  const {
    taskId,
    historyId,
    action: { actionType, params },
    dialect
  } = props;

  // 从 Sandbox 读取的数据，用于 FlinkSQL 和 DataFlow 查询
  const [tableInfoList, setTableInfoList] = useState<TableInfo[]>([]);
  // 通过 action 传递来的查询数据，用于数据源查询、FlinkSQL 的 show语句等
  const [dataList, setDataList] = useState<Data[]>([]);
  const [activeTabIndex, setActiveTabIndex] = useState<number>(0);
  const [loading, setLoading] = useState<boolean>(true);
  const [openAVA, setOpenAVA] = useState<boolean>(false);
  const [avaResult, setAvaResult] = useState<InsightsResult>();
  const [isPending, startTransition] = useTransition();
  const [searchText, setSearchText] = useState('');
  const [searchedColumn, setSearchedColumn] = useState('');
  const searchInput = useRef<InputRef>(null);

  useEffect(() => {
    if (actionType === DataStudioActionType.TASK_PREVIEW_RESULT && taskId === params.taskId) {
      if (params.async) {
        loadTableInfo();
      } else {
        setDataList([{ columns: params.columns, rowData: params.rowData }]);
      }
    }
  }, [props.action]);

  useAsyncEffect(async () => {
    if (!isSql(dialect)) {
      await loadTableInfo();
    } else {
      setLoading(false);
    }
  }, []);

  const convertTableIdToString = (tableId: {
    boxName: string;
    catalogName: string;
    databaseName: string;
    tableName: string;
  }) => {
    return `${tableId.catalogName ?? 'default_catalog'}.${tableId.databaseName ?? 'default_database'}.${tableId.tableName ?? 'result'}`;
  };

  const loadTableInfo = async () => {
    let historyIdParam = historyId;
    if (!historyIdParam) {
      const res = await handleGetOptionWithoutMsg(API_CONSTANTS.GET_LATEST_HISTORY_BY_ID, {
        id: taskId
      });
      historyIdParam = res?.data?.id;
    }
    if (historyIdParam) {
      const tableInfosRes = await handleGetOption(
        API_CONSTANTS.GET_JOB_DATA_TABLE_INFOS,
        l('global.getdata.tips'),
        {
          jobId: historyIdParam
        }
      );
      const data = tableInfosRes.data;
      if (tableInfosRes.success) {
        if (data.length > 0) {
          await refreshTableData(data, 0);
        }
      }
    }
    setLoading(false);
  };

  const refreshTableData = async (tableInfos: TableInfo[], index: number) => {
    if (index < tableInfos.length) {
      let tableInfo = tableInfos[index];
      const tableData = await handleGetOption(
        API_CONSTANTS.GET_JOB_DATA,
        l('global.getdata.tips'),
        {
          boxName: tableInfo.tableId.boxName,
          tableName: convertTableIdToString(tableInfo.tableId)
        }
      );
      const data = tableData.data;
      if (tableData.success) {
        // 更新 tableInfoList
        const newTableInfoList = tableInfos;
        newTableInfoList[index] = {
          ...tableInfo,
          rowData: data.rowData
        };
        setTableInfoList(newTableInfoList);
      }
    }
  };

  const handleReset = (clearFilters: () => void) => {
    clearFilters();
    setSearchText('');
  };

  const handleSearch = (
    selectedKeys: string[],
    confirm: (param?: FilterConfirmProps) => void,
    dataIndex: DataIndex
  ) => {
    confirm();
    if (selectedKeys.length > 0) {
      setSearchText(selectedKeys[0]);
      setSearchedColumn(dataIndex.toString());
    } else {
      setSearchText('');
      setSearchedColumn('');
    }
  };

  const getColumnSearchProps = (dataIndex: string): ColumnType<Data> => ({
    filterDropdown: ({ setSelectedKeys, selectedKeys, confirm, clearFilters }) => (
      <div style={{ padding: 8 }} onKeyDown={(e) => e.stopPropagation()}>
        <Input
          ref={searchInput}
          placeholder={`Search ${dataIndex}`}
          value={selectedKeys[0]}
          onChange={(e) => setSelectedKeys(e.target.value ? [e.target.value] : [])}
          onPressEnter={() => handleSearch(selectedKeys as string[], confirm, dataIndex)}
          style={{ marginBottom: 8, display: 'block' }}
        />
        <Space>
          <Button
            type='primary'
            onClick={() => handleSearch(selectedKeys as string[], confirm, dataIndex)}
            icon={<SearchOutlined />}
            size='small'
            style={{ width: 90 }}
          >
            {l('button.search')}
          </Button>
          <Button
            onClick={() => clearFilters && handleReset(clearFilters)}
            size='small'
            style={{ width: 90 }}
          >
            {l('button.reset')}
          </Button>
        </Space>
      </div>
    ),
    filterIcon: (filtered: boolean) => (
      <SearchOutlined style={{ color: filtered ? '#1677ff' : undefined }} />
    ),
    onFilter: (value, record) =>
      record[dataIndex]
        .toString()
        .toLowerCase()
        .includes((value as string).toLowerCase()),
    onFilterDropdownOpenChange: (visible) => {
      if (visible) {
        setTimeout(() => searchInput.current?.select(), 100);
      }
    },
    render: (text) =>
      searchedColumn === dataIndex ? (
        <Highlight label={text ? text.toString() : ''} words={[searchText]} />
      ) : (
        text
      )
  });

  const getColumns = (columns: string[] = []) => {
    return columns?.map((item) => {
      return {
        title: item,
        dataIndex: item,
        sorter: (a, b) => a[item] - b[item],
        ...getColumnSearchProps(item)
      };
    }) as ProColumns[];
  };

  const buildColumns = (
    columns: {
      name: string;
      dataType: string;
    }[] = []
  ) => {
    return columns?.map((item) => {
      return {
        title: item.name,
        dataIndex: item.name,
        sorter: (a, b) => a[item.name] - b[item.name],
        ...getColumnSearchProps(item.name)
      };
    }) as ProColumns[];
  };

  const refreshLatestData = async () => {
    setLoading(true);
    await refreshTableData(tableInfoList, activeTabIndex);
    setLoading(false);
  };

  const renderFlinkSQLContent = () => {
    return (
      <>
        {!isSql(dialect) ? (
          <Button
            loading={loading}
            type='primary'
            onClick={refreshLatestData}
            icon={<SyncOutlined />}
          >
            {l('pages.datastudio.label.result.query.latest.data')}
          </Button>
        ) : undefined}
      </>
    );
  };

  const renderDownloadButton = (tableInfo: TableInfo) => {
    if (tableInfo.columns && tableInfo.rowData && tableInfo.rowData.length > 0) {
      const _utf = '\uFEFF';
      const csvDataBlob = new Blob(
        [_utf + transformTableDataToCsv(tableInfo.columns!, tableInfo.rowData!)],
        {
          type: 'text/csv'
        }
      );
      const url = URL.createObjectURL(csvDataBlob);
      return <Button type='link' href={url} icon={<DownloadOutlined />} title={'Export Csv'} />;
    }
    return undefined;
  };

  const renderAVA = (tableInfo: TableInfo) => {
    return (
      <Button
        type='link'
        title={l('button.ava')}
        onClick={() => {
          setOpenAVA(true);
          startTransition(() => {
            setAvaResult(getInsights(tableInfo.rowData as Datum[], { visualization: true }));
          });
        }}
      >
        AVA
      </Button>
    );
  };

  const handleCloseAva = useCallback(() => setOpenAVA(false), []);

  const tabItems: () => TabsProps['items'] = () => {
    if (
      actionType === DataStudioActionType.TASK_PREVIEW_RESULT &&
      taskId === params.taskId &&
      !params.async
    ) {
      return [
        {
          key: '0',
          label: '运行结果',
          children: (
            <ProTable
              className={'datastudio-theme'}
              cardBordered
              columns={getColumns(dataList[0]?.columns)}
              size='small'
              scroll={{ x: 'max-content' }}
              dataSource={dataList[0]?.rowData?.map((item: any, index: number) => {
                return { ...item, key: index };
              })}
              options={{ fullScreen: true, density: false }}
              search={false}
              pagination={{
                showSizeChanger: true
              }}
            />
          )
        }
      ];
    }
    return tableInfoList.map((tableInfo, index) => {
      return {
        key: index,
        label: tableInfo.tableId.tableName,
        children: (
          <ProTable
            className={'datastudio-theme'}
            cardBordered
            columns={buildColumns(tableInfo.columns)}
            size='small'
            scroll={{ x: 'max-content' }}
            dataSource={tableInfo.rowData?.map((item: any, index: number) => {
              return { ...item, key: index };
            })}
            options={{ fullScreen: true, density: false }}
            search={false}
            loading={loading}
            toolBarRender={() => [renderDownloadButton(tableInfo), renderAVA(tableInfo)]}
            pagination={{
              showSizeChanger: true
            }}
          />
        )
      };
    });
  };

  return (
    <div style={{ width: '100%' }}>
      <Tabs
        defaultActiveKey='0'
        tabBarExtraContent={renderFlinkSQLContent()}
        items={tabItems()}
        tabBarStyle={{ marginBottom: '5px' }}
        onChange={(key) => {
          setActiveTabIndex(parseInt(key));
        }}
      />
      {dataList.length == 0 ? <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} /> : <></>}
      <Drawer
        open={openAVA}
        loading={isPending}
        width={'70%'}
        onClose={handleCloseAva}
        destroyOnClose
      >
        <div key='plot' style={{ flex: 5, height: '100%' }}>
          {avaResult?.insights &&
            avaResult.insights.map((insight, index) => {
              return (
                <InsightCard
                  insightInfo={insight}
                  key={index}
                  visualizationOptions={{ lang: 'zh-CN' }}
                />
              );
            })}
        </div>
      </Drawer>
    </div>
  );
};

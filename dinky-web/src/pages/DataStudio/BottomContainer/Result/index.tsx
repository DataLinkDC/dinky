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

import {
  getCurrentData,
  getCurrentTab,
  isDataStudioTabsItemType,
  mapDispatchToProps
} from '@/pages/DataStudio/function';
import { isSql } from '@/pages/DataStudio/HeaderContainer/function';
import { StateType } from '@/pages/DataStudio/model';
import { handleGetOption, handleGetOptionWithoutMsg } from '@/services/BusinessCrud';
import { DIALECT } from '@/services/constants';
import { API_CONSTANTS } from '@/services/endpoints';
import { transformTableDataToCsv } from '@/utils/function';
import { l } from '@/utils/intl';
import { SearchOutlined } from '@ant-design/icons';
import { Highlight } from '@ant-design/pro-layout/es/components/Help/Search';
import { Button, Empty, Input, InputRef, Space, Table, Tabs } from 'antd';
import { ColumnsType, ColumnType } from 'antd/es/table';
import { FilterConfirmProps } from 'antd/es/table/interface';
import { DataIndex } from 'rc-table/es/interface';
import { useEffect, useRef, useState } from 'react';
import { connect } from 'umi';

type Data = {
  [c: string]: any;
  columns?: string[];
  rowData?: object[];
};
type DataList = Data[];
const Result = (props: any) => {
  const {
    tabs: { panes, activeKey }
  } = props;
  const [data, setData] = useState<Data>({});
  const [dataList, setDataList] = useState<DataList>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const currentTabs = getCurrentTab(panes, activeKey);
  const current = getCurrentData(panes, activeKey) ?? {};

  const [searchText, setSearchText] = useState('');
  const [searchedColumn, setSearchedColumn] = useState('');
  const searchInput = useRef<InputRef>(null);
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
    setSearchText(selectedKeys[0]);
    setSearchedColumn(dataIndex.toString());
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

  const loadData = async (isRefresh?: boolean) => {
    if (!isDataStudioTabsItemType(currentTabs)) {
      return;
    }

    const params = currentTabs.params;
    const consoleData = currentTabs.console;
    if (consoleData.result && !isRefresh) {
      setData(consoleData.result);
    } else if (consoleData.results && !isRefresh) {
      setDataList(consoleData.results);
    } else {
      if (current.dialect && current.dialect.toLowerCase() == DIALECT.FLINK_SQL) {
        // flink sql
        // to do: get job data by history id list, not flink jid
        if (current.id) {
          const res = await handleGetOptionWithoutMsg(API_CONSTANTS.GET_LATEST_HISTORY_BY_ID, {
            id: current.id
          });
          const historyData = res.data;
          if (historyData) {
            const historyId = historyData.id;
            const tableData = await handleGetOption(
              'api/studio/getJobData',
              l('global.getdata.tips'),
              {
                jobId: historyId
              }
            );
            const data = tableData.data;
            if (data.success) {
              consoleData.result = data;
              setData(data);
            } else {
              consoleData.result = {};
              setData({});
            }
          }
        }
      }
    }
    setLoading(false);
  };

  useEffect(() => {
    setData({});
    setDataList([]);
    loadData();
  }, [currentTabs?.console?.result, currentTabs?.console?.results]);

  const getColumns = (columns: string[]) => {
    return columns?.map((item) => {
      return {
        title: item,
        dataIndex: item,
        sorter: (a, b) => a[item] - b[item],
        ...getColumnSearchProps(item)
      };
    }) as ColumnsType<any>;
  };

  const showDetail = async () => {
    setLoading(true);
    await loadData(true);
    setLoading(false);
  };

  const renderFlinkSQLContent = () => {
    return (
      <>
        {current.jobInstanceId ? (
          <>
            <Space>
              <Button
                loading={loading}
                type='primary'
                onClick={showDetail}
                icon={<SearchOutlined />}
              >
                {l('pages.datastudio.label.result.query.latest.data')}
              </Button>
            </Space>
          </>
        ) : undefined}
      </>
    );
  };
  const renderDownloadButton = () => {
    if (current && data.columns) {
      const _utf = '\uFEFF';
      const csvDataBlob = new Blob([_utf + transformTableDataToCsv(data.columns!, data.rowData!)], {
        type: 'text/csv'
      });
      const url = URL.createObjectURL(csvDataBlob);
      return (
        <Button type='link' href={url}>
          Export Csv
        </Button>
      );
    }
    return undefined;
  };

  return (
    <div style={{ width: '100%' }}>
      <div style={{ direction: 'rtl' }}>
        {renderDownloadButton()}
        {current ? isSql(current.dialect) ? <></> : renderFlinkSQLContent() : undefined}
      </div>
      {data.columns ? (
        <Table
          columns={getColumns(data.columns)}
          size='small'
          dataSource={data.rowData?.map((item: any, index: number) => {
            return { ...item, key: index };
          })}
          loading={loading}
        />
      ) : dataList.length > 0 ? (
        <Tabs defaultActiveKey='0'>
          {dataList.map((data, index) => {
            return (
              <Tabs.TabPane key={index} tab={`Table ${index + 1}`}>
                <Table
                  columns={getColumns(data.columns)}
                  size='small'
                  dataSource={data.rowData?.map((item: any, index: number) => {
                    return { ...item, key: index };
                  })}
                  loading={loading}
                />
              </Tabs.TabPane>
            );
          })}
        </Tabs>
      ) : (
        <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
      )}
    </div>
  );
};

export default connect(
  ({ Studio }: { Studio: StateType }) => ({
    tabs: Studio.tabs
  }),
  mapDispatchToProps
)(Result);

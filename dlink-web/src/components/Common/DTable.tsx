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


import React, {useEffect, useState} from "react";
import {SearchOutlined} from '@ant-design/icons';
import ProTable from '@ant-design/pro-table';
import {getData} from "@/components/Common/crud";
import {Button, Input, Space} from "antd";
import {l} from "@/utils/intl";

const DTable = (props: any) => {

  const {dataSource, columns, scroll} = props;

  const [data, setData] = useState<[]>([]);

  const refreshData = async () => {
    const msg = await getData(dataSource.url, dataSource.params);
    setData(msg.datas);
  };

  const buildColumn = () => {
    const columnList: any = [];
    columns.map((item) => {
      const openSorter = item.openSorter == null ? true : item.openSorter;
      const isString = item.isString == null ? true : item.isString;
      const openSearch = item.openSearch == null ? 'like' : item.openSearch;

      let column = {
        title: item.title ? item.title : item.field,
        dataIndex: item.dataIndex ? item.dataIndex : item.field,
        key: item.dataIndex ? item.dataIndex : item.field,
      };
      if (openSorter) {
        if (isString) {
          column = {
            sorter: (a, b) => {
              const value1 = a[column.dataIndex] != null ? a[column.dataIndex].toString() : '';
              const value2 = b[column.dataIndex] != null ? b[column.dataIndex].toString() : '';
              return value1.localeCompare(value2);
            },
            ...column,
          }
        } else {
          column = {
            sorter: (a, b) => a[column.dataIndex] - b[column.dataIndex],
            ...column,
          }
        }
      }
      if (openSearch === 'like') {
        column = {...column, ...getColumnSearchProps(column.dataIndex),}
      } else if (openSearch === 'dict') {
        column = {
          onFilter: (value, record) => record[column.dataIndex] === value,
          ...column,
        }
      }
      columnList.push({
        ...column,
        ...item,
      });
    });
    return columnList;
  }

  useEffect(() => {
    if (dataSource && dataSource.url) {
      refreshData();
    }
  }, [dataSource]);

  return (
    <ProTable
      columns={buildColumn()}
      style={{width: '100%'}}
      scroll={scroll}
      dataSource={dataSource ? (dataSource.url ? data : dataSource) : []}
      rowKey="name"
      pagination={{
        defaultPageSize: 10,
        showSizeChanger: true,
      }}
      toolBarRender={false}
      search={false}
      size="small"
    />
  );
};

export default DTable;

export const getColumnSearchProps = (dIndex) => ({
  filterDropdown: ({setSelectedKeys, selectedKeys, confirm, clearFilters}) => (
    <div style={{padding: 8}}>
      <Input
        placeholder={`请输入关键字`}
        value={selectedKeys[0]}
        onChange={e => setSelectedKeys(e.target.value ? [e.target.value] : [])}
        onPressEnter={() => handleSearch(selectedKeys, confirm, dIndex)}
        style={{marginBottom: 8, display: 'block'}}
      />
      <Space>
        <Button
          type="primary"
          onClick={() => handleSearch(selectedKeys, confirm, dIndex)}
          icon={<SearchOutlined/>}
          size="small"
          style={{width: 90}}
        >
          {l('button.search')}
        </Button>
        <Button onClick={() => handleReset(clearFilters)} size="small" style={{width: 90}}>
          {l('button.reset')}
        </Button>
      </Space>
    </div>
  ),
  filterIcon: filtered => <SearchOutlined style={{color: filtered ? '#1890ff' : undefined}}/>,
  onFilter: (value, record) =>
    record[dIndex]
      ? record[dIndex].toString().toLowerCase().includes(value.toLowerCase())
      : '',
});

const handleSearch = (selectedKeys, confirm, dIndex) => {
  confirm();
};

const handleReset = (clearFilters) => {
  clearFilters();
};

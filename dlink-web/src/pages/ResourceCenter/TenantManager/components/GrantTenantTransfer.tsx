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
import {Table, Transfer} from 'antd';
import type {ColumnsType, TableRowSelection} from 'antd/es/table/interface';
import type {TransferProps} from 'antd/es/transfer';
import difference from 'lodash/difference';
import React, {useEffect, useState} from 'react';
import {getData} from "@/components/Common/crud";
import {Scrollbars} from 'react-custom-scrollbars';
import { TenantTableListItem, UserTableListItem} from "@/pages/ResourceCenter/data.d";


interface TableTransferProps extends TransferProps<UserTableListItem> {
  dataSource: UserTableListItem[];
  leftColumns: ColumnsType<UserTableListItem>;
  rightColumns: ColumnsType<UserTableListItem>;
}


// Customize Table Transfer
const GrantTenantTransfer = ({leftColumns, rightColumns, ...restProps}: TableTransferProps) => (
  <Transfer
    titles={['未选', '已选']}
    locale={{
      itemUnit: "项",
      itemsUnit: "项",
      searchPlaceholder: "请输入用户名搜索",

    }}
    showSelectAll={false}
    showSearch={true}
    {...restProps}>
    {({
        direction,
        filteredItems,
        onItemSelectAll,
        onItemSelect,
        selectedKeys: listSelectedKeys,
        disabled: enabled,
      }) => {
      const columns = direction === 'left' ? leftColumns : rightColumns;

      const rowSelection: TableRowSelection<UserTableListItem> = {
        getCheckboxProps: item => ({disabled: enabled || item.enabled || item.isDelete}),
        onSelectAll: function (selected, selectedRows) {
          const treeSelectedKeys = selectedRows
            .filter(item => !item.isDelete)
            .map(({id}) => id);
          const diffKeys = selected
            ? difference(treeSelectedKeys, listSelectedKeys)
            : difference(listSelectedKeys, treeSelectedKeys);
          onItemSelectAll(diffKeys as string[], selected);
        },
        onSelect({id}, selected) {
          onItemSelect(id as unknown as string, selected);
        },
        selectedRowKeys: listSelectedKeys,
      };

      return (<>
          <Scrollbars style={{height: '520px', width: '100%'}}>
            <Table
              rowSelection={rowSelection}
              columns={columns}
              pagination={{
                pageSize: 7,
              }}
              dataSource={filteredItems}
              size="large"
              rowKey='id'
              style={{
                height: '350px',
                pointerEvents: enabled ? 'none' : undefined
              }}
              onRow={({id, isDelete: itemDisabled}) => ({
                onClick: () => {
                  if (itemDisabled || enabled) return;
                  onItemSelect(id, !listSelectedKeys.includes(id));
                },
              })}
            />
          </Scrollbars>
        </>
      );
    }}
  </Transfer>
);

export type TableTransferFromProps = {
  tenant: Partial<TenantTableListItem>;
  onChange: (values: string[]) => void;
};

const GrantTenantToUserTableTransferFrom = (props: TableTransferFromProps) => {

  const {tenant, onChange: handleChange} = props;

  const [targetKeys, setTargetKeys] = useState<string[]>([]);
  const [userTableList, setUserTableList] = useState<UserTableListItem[]>([]);
  const [selectedKeys, setSelectedKeys] = useState<string[]>([]);
  const onSelectChange = (
    sourceSelectedKeys: string[],
    targetSelectedKeys: string[],
  ) => {
    const newSelectedKeys = [...sourceSelectedKeys, ...targetSelectedKeys];
    setSelectedKeys(newSelectedKeys);
  };

  useEffect(() => {
    getData('/api/user/getUserListByTenantId', {id: tenant.id}).then(result => {
      console.log(result.datas,'============')
      setUserTableList(result.datas.users);
      setTargetKeys(result.datas.userIds);
      handleChange(result.datas.userIds);
    });
  }, []);

  const leftTableColumns: ColumnsType<UserTableListItem> = [
    {
      title: '用户名',
      dataIndex: 'username',
    },
    {
      title: '昵称',
      dataIndex: 'nickname',
    },
    {
      title: '工号',
      dataIndex: 'worknum',
    },
  ];

  const rightTableColumns: ColumnsType<UserTableListItem> = [
    {
      title: '用户名',
      dataIndex: 'username',
    },
    {
      title: '昵称',
      dataIndex: 'nickname',
    },
    {
      title: '工号',
      dataIndex: 'worknum',
    },
  ];


  const onChange = (nextTargetKeys: string[]) => {
    setTargetKeys(nextTargetKeys);
    handleChange(nextTargetKeys);
  };


  return (<>
      <GrantTenantTransfer
        dataSource={userTableList}
        targetKeys={targetKeys}
        selectedKeys={selectedKeys}
        rowKey={itme => itme.id}
        onChange={onChange}
        onSelectChange={onSelectChange}
        filterOption={(inputValue, item) =>
          item.username!.indexOf(inputValue) !== -1 || item.username!.indexOf(inputValue) !== -1
        }
        leftColumns={leftTableColumns}
        rightColumns={rightTableColumns}
      />
    </>
  );
};
export default GrantTenantToUserTableTransferFrom;


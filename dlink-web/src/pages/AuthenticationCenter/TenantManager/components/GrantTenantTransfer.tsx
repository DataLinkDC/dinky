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
import {TenantTableListItem, UserTableListItem} from "@/pages/AuthenticationCenter/data.d";
import {l} from "@/utils/intl";


interface TableTransferProps extends TransferProps<UserTableListItem> {
  dataSource: UserTableListItem[];
  leftColumns: ColumnsType<UserTableListItem>;
  rightColumns: ColumnsType<UserTableListItem>;
}


// Customize Table Transfer
const GrantTenantTransfer = ({leftColumns, rightColumns, ...restProps}: TableTransferProps) => (
  <Transfer
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
        getCheckboxProps: item => ({disabled: enabled || !item.enabled}),
        onSelectAll: function (selected, selectedRows) {
          const treeSelectedKeys = selectedRows
            .filter(item => item.enabled)
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
                defaultPageSize: 10,
                showSizeChanger: true,
                hideOnSinglePage: true,
              }}
              dataSource={filteredItems}
              size="large"
              rowKey='id'
              style={{
                height: '350px',
                pointerEvents: enabled ? 'none' : undefined
              }}
              onRow={({id, enabled: itemDisabled}) => ({
                onClick: (e) => {
                  if (itemDisabled || !enabled) {
                    onItemSelect(id, listSelectedKeys.includes(id));
                  } else {
                    return;
                  }
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
      setUserTableList(result.datas.users);
      setTargetKeys(result.datas.userIds);
      handleChange(result.datas.userIds);
    });
  }, []);

  const leftTableColumns: ColumnsType<UserTableListItem> = [
    {
      title: l('pages.user.UserName'),
      dataIndex: 'username',
    },
    {
      title: l('pages.user.UserNickName'),
      dataIndex: 'nickname',
    },
    {
      title: l('pages.user.UserJobNumber'),
      dataIndex: 'worknum',
    },
  ];

  const rightTableColumns: ColumnsType<UserTableListItem> = [
    {
      title: l('pages.user.UserName'),
      dataIndex: 'username',
    },
    {
      title: l('pages.user.UserNickName'),
      dataIndex: 'nickname',
    },
    {
      title: l('pages.user.UserJobNumber'),
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
        rowKey={item => item.id}
        onChange={onChange}
        onSelectChange={onSelectChange}
        filterOption={(inputValue, item) =>
          item.username!.indexOf(inputValue) !== -1 || item.nickname!.indexOf(inputValue) !== -1 || item.worknum!.indexOf(inputValue) !== -1
        }
        leftColumns={leftTableColumns}
        rightColumns={rightTableColumns}
      />
    </>
  );
};
export default GrantTenantToUserTableTransferFrom;


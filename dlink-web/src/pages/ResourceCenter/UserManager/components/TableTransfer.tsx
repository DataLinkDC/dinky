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
import {queryData} from "@/components/Common/crud";
import {RoleTableListItem} from "@/pages/ResourceCenter/data.d";

// TODO:
//  1.表单渲染数据
//  2.给用户分配角色
//  3.给该用户删除角色
// 给用户分配角色接口 /api/user/grantRole 数据结构: [1,2,3]
// 删除角色接口 /api/user/removeGrantRole 数据结构: [1,2,3]
// 获取用户角色接口 /api/user/getRole 数据结构: userId: 1,tenantId: 1

interface TableTransferProps extends TransferProps<RoleTableListItem> {
  dataSource: RoleTableListItem[];
  leftColumns: ColumnsType<RoleTableListItem>;
  rightColumns: ColumnsType<RoleTableListItem>;
}


// Customize Table Transfer
const TableTransfer = ({leftColumns, rightColumns, ...restProps}: TableTransferProps) => (
  <Transfer
    titles={['未选', '已选']}
    locale={{
      itemUnit: "项",
      itemsUnit: "项",
      searchPlaceholder: "请输入角色名称搜索",

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
        disabled: isDelete,
      }) => {
      const columns = direction === 'left' ? leftColumns : rightColumns;

      const rowSelection: TableRowSelection<RoleTableListItem> = {
        getCheckboxProps: item => ({disabled: isDelete || item.isDelete}),
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

      return (
        <Table
          rowSelection={rowSelection}
          columns={columns}
          dataSource={filteredItems}
          pagination={false}
          size="large"
          rowKey='id'
          style={{
            height: '350px',
            pointerEvents: isDelete ? 'none' : undefined}}
          onRow={({id, isDelete: itemDisabled}) => ({
            onClick: () => {
              if (itemDisabled || isDelete) return;
              onItemSelect(id , !listSelectedKeys.includes(id));
            },
          })}
        />
      );
    }}
  </Transfer>
);


const TableTransferFrom: React.FC = () => {

  const [targetKeys, setTargetKeys] = useState<string[]>([]);
  const [roleTableList, setRoleTableList] = useState<RoleTableListItem[]>([]);
  const [selectedKeys, setSelectedKeys] = useState<string[]>([]);
  const onSelectChange = (
    sourceSelectedKeys: string[],
    targetSelectedKeys: string[],
  ) => {
    const newSelectedKeys = [...sourceSelectedKeys, ...targetSelectedKeys];
    setSelectedKeys(newSelectedKeys);
  };

  useEffect(() => {
    queryData('/api/role', {}).then(result => {
      setRoleTableList(result.data);
    });
  }, []);



  const leftTableColumns: ColumnsType<RoleTableListItem> = [
    {
      dataIndex: 'roleCode',
      title: '角色编码',
    },
    {
      dataIndex: 'roleName',
      title: '角色名称',
    },
    {
      dataIndex: 'note',
      title: '描述',
      ellipsis: true,
    },
  ];

  const rightTableColumns: ColumnsType<RoleTableListItem> = [
    {
      dataIndex: 'roleCode',
      title: '角色编码',
    },
    {
      dataIndex: 'roleName',
      title: '角色名称',
    },
    {
      dataIndex: 'note',
      title: '描述',
      ellipsis: true,
    },
  ];


  const onChange = (nextTargetKeys: string[]) => {
    setTargetKeys(nextTargetKeys);
  };



  return (
    <TableTransfer
      dataSource={roleTableList}
      targetKeys={targetKeys}
      selectedKeys={selectedKeys}
      rowKey={itme => itme.id}
      pagination
      onChange={onChange}
      onSelectChange={onSelectChange}
      filterOption={(inputValue, item) =>
        item.roleCode!.indexOf(inputValue) !== -1 || item.roleName!.indexOf(inputValue) !== -1
      }
      leftColumns={leftTableColumns}
      rightColumns={rightTableColumns}
    />
  );
};
export default TableTransferFrom;


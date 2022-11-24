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
import {useEffect, useState} from 'react';
import {getData} from "@/components/Common/crud";
import {Scrollbars} from 'react-custom-scrollbars';
import {RoleTableListItem, UserTableListItem} from "@/pages/AuthenticationCenter/data.d";
import {l} from "@/utils/intl";


interface TableTransferProps extends TransferProps<RoleTableListItem> {
  dataSource: RoleTableListItem[];
  leftColumns: ColumnsType<RoleTableListItem>;
  rightColumns: ColumnsType<RoleTableListItem>;
}


// Customize Table Transfer
const TableTransfer = ({leftColumns, rightColumns, ...restProps}: TableTransferProps) => (
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
                pointerEvents: isDelete ? 'none' : undefined
              }}
              onRow={({id, isDelete: itemDisabled}) => ({
                onClick: () => {
                  if (itemDisabled || isDelete) return;
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
  user: Partial<UserTableListItem>;
  onChange: (values: string[]) => void;
};

const TableTransferFrom = (props: TableTransferFromProps) => {

  const {user, onChange: handleChange} = props;

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
    getData('/api/role/getRolesAndIdsByUserId', {id: user.id}).then(result => {
      setRoleTableList(result.datas.roles);
      setTargetKeys(result.datas.roleIds);
      handleChange(result.datas.roleIds);
    });
  }, []);

  const leftTableColumns: ColumnsType<RoleTableListItem> = [
    {
      dataIndex: 'roleCode',
      title: l('pages.role.roleCode'),
    },
    {
      dataIndex: 'roleName',
      title: l('pages.role.roleName'),
    },
    {
      dataIndex: 'note',
      title: l('global.table.note'),
      ellipsis: true,
    },
  ];

  const rightTableColumns: ColumnsType<RoleTableListItem> = [
    {
      dataIndex: 'roleCode',
      title: l('pages.role.roleCode'),
    },
    {
      dataIndex: 'roleName',
      title: l('pages.role.roleName'),
    },
    {
      dataIndex: 'note',
      title: l('global.table.note'),
      ellipsis: true,
    },
  ];


  const onChange = (nextTargetKeys: string[]) => {
    setTargetKeys(nextTargetKeys);
    handleChange(nextTargetKeys);
  };


  return (<>
      <TableTransfer
        dataSource={roleTableList}
        targetKeys={targetKeys}
        selectedKeys={selectedKeys}
        rowKey={item => item.id}
        onChange={onChange}
        onSelectChange={onSelectChange}
        filterOption={(inputValue, item) =>
          item.roleCode!.indexOf(inputValue) !== -1 || item.roleName!.indexOf(inputValue) !== -1
        }
        leftColumns={leftTableColumns}
        rightColumns={rightTableColumns}
      />
    </>
  );
};
export default TableTransferFrom;


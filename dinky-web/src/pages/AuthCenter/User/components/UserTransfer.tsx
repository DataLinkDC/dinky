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
import type {ColumnsType} from 'antd/es/table/interface';
import {useEffect, useState} from 'react';
import {l} from "@/utils/intl";
import {getData} from "@/services/api";
import TableTransfer from '@/components/TableTransfer/TableTransfer';
import {API_CONSTANTS} from "@/services/constants";


export type TableTransferFromProps = {
  user: Partial<UserBaseInfo.User>;
  onChange: (values: string[]) => void;
};

const TableTransferFrom = (props: TableTransferFromProps) => {

  /**
   * status
   */
  const {user, onChange: handleChange} = props;
  const [targetKeys, setTargetKeys] = useState<string[]>([]);
  const [roleTableList, setRoleTableList] = useState<UserBaseInfo.Role[]>([]);
  const [selectedKeys, setSelectedKeys] = useState<string[]>([]);

  /**
   * select change
   * @param sourceSelectedKeys
   * @param targetSelectedKeys
   */
  const onSelectChange = (
    sourceSelectedKeys: string[],
    targetSelectedKeys: string[],
  ) => {
    const newSelectedKeys = [...sourceSelectedKeys, ...targetSelectedKeys];
    setSelectedKeys(newSelectedKeys);
  };

  /**
   * get data
   */
  useEffect(() => {
    getData(API_CONSTANTS.GET_ROLES_BY_USERID, {id: user.id}).then(result => {
      setRoleTableList(result.datas.roles);
      setTargetKeys(result.datas.roleIds);
      handleChange(result.datas.roleIds);
    });
  }, []);

  /**
   * table columns
   */
  const columns: ColumnsType<UserBaseInfo.Role> = [
    {
      dataIndex: 'roleCode',
      title: l('role.roleCode'),
    },
    {
      dataIndex: 'roleName',
      title: l('role.roleName'),
    },
    {
      dataIndex: 'note',
      title: l('global.table.note'),
      ellipsis: true,
    },
  ];


  /**
   * transfer change
   * @param nextTargetKeys
   */
  const onChange = (nextTargetKeys: string[]) => {
    setTargetKeys(nextTargetKeys);
    handleChange(nextTargetKeys);
  };


  /**
   * render
   */
  return (<>
      <TableTransfer
        dataSource={roleTableList}
        targetKeys={targetKeys}
        selectedKeys={selectedKeys}
        rowKey={item => item.id as any}
        onChange={onChange}
        onSelectChange={onSelectChange}
        filterOption={(inputValue, item) =>
          item.roleCode!.indexOf(inputValue) !== -1 || item.roleName!.indexOf(inputValue) !== -1
        }
        leftColumns={columns}
        rightColumns={columns}
      />
    </>
  );
};
export default TableTransferFrom;


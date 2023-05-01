/*
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
import React, {useEffect, useState} from 'react';
import {l} from "@/utils/intl";
import {API_CONSTANTS} from "@/services/constants";
import {getData} from "@/services/api";
import {UserBaseInfo} from '@/types/User/data.d';
import TableTransfer from "@/components/TableTransfer";


type TenantTransferFromProps = {
    tenant: Partial<UserBaseInfo.Tenant>;
    onChange: (values: string[]) => void;
};


const TenantTransfer: React.FC<TenantTransferFromProps> = (props) => {


    const {tenant, onChange: handleChange} = props;

    const [targetKeys, setTargetKeys] = useState<string[]>([]);
    const [userTableList, setUserTableList] = useState<UserBaseInfo.User[]>([]);
    const [selectedKeys, setSelectedKeys] = useState<string[]>([]);
    const onSelectChange = (
        sourceSelectedKeys: string[],
        targetSelectedKeys: string[],
    ) => {
        const newSelectedKeys = [...sourceSelectedKeys, ...targetSelectedKeys];
        setSelectedKeys(newSelectedKeys);
    };

    useEffect(() => {
        getData(API_CONSTANTS.GET_USER_LIST_BY_TENANTID, {id: tenant.id}).then((result: any) => {
            setUserTableList(result.datas.users);
            setTargetKeys(result.datas.userIds);
            handleChange(result.datas.userIds);
        });
    }, []);

    const columns: ColumnsType<UserBaseInfo.User> = [
        {
            title: l('user.username'),
            dataIndex: 'username',
        },
        {
            title: l('user.nickname'),
            dataIndex: 'nickname',
        },
        {
            title: l('user.jobnumber'),
            dataIndex: 'worknum',
        },
    ];

    const onChange = (nextTargetKeys: string[]) => {
        setTargetKeys(nextTargetKeys);
        handleChange(nextTargetKeys);
    };


    return <>
        <TableTransfer
            dataSource={userTableList}
            targetKeys={targetKeys}
            selectedKeys={selectedKeys}
            rowKey={item => item.id as any}
            onChange={onChange}
            onSelectChange={onSelectChange}
            filterOption={(inputValue, item) =>
                item.username!.indexOf(inputValue) !== -1 || item.nickname!.indexOf(inputValue) !== -1 || item.worknum!.indexOf(inputValue) !== -1
            }
            leftColumns={columns}
            rightColumns={columns}
        />
    </>
};
export default TenantTransfer;


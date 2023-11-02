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

import {EditableProTable, ProColumns} from "@ant-design/pro-components";
import {UDFRegisterInfo} from "@/types/RegCenter/data";
import {EditBtn} from "@/components/CallBackButton/EditBtn";
import {PopconfirmDeleteBtn} from "@/components/CallBackButton/PopconfirmDeleteBtn";
import {Key, useEffect, useRef, useState} from "react";
import {ActionType} from "@ant-design/pro-table";
import {useRequest} from "@@/plugin-request";
import {API_CONSTANTS} from "@/services/endpoints";
import {Button, Modal, Space} from "antd";
import {PlusOutlined} from "@ant-design/icons";
import {TreeTransfer} from "@/components/Transfer/TreeTransfer";
import {buildResourceTreeData} from "@/pages/RegCenter/Resource/components/FileTree/function";
import {add} from "./service";

import {
    BaseResult
} from 'C:/project/ideaProjects/dinky/dinky-web/node_modules/@umijs/plugins/node_modules/@ahooksjs/use-request/es/types';

const UDFRegister = () => {
    const req: BaseResult<UDFRegisterInfo[], any> = useRequest({
        url: API_CONSTANTS.UDF_LIST
    });
    const req_resources = useRequest({
        url: API_CONSTANTS.UDF_RESOURCES_LIST
    });
    const actionRef = useRef<ActionType>();
    const [udfRegisterState, setUDFRegisterState] = useState<{
        editableKeys: Key[];
        isEdit: boolean;
        isAdd: boolean;
        dataSource: UDFRegisterInfo[];
    }>({
        editableKeys: [],
        dataSource: [],
        isEdit: false,
        isAdd: false,
    });
    const [showEdit, setShowEdit] = useState<boolean>(false);
    const [targetKeys, setTargetKeys] = useState<Key[]>([]);
    useEffect(() => {
        setTargetKeys([...new Set(req.data?.map(x => x.resourcesId))])
    }, [req.data])
    const editableKeysChange = (editableKeys: Key[]) => {
        setUDFRegisterState(prevState => ({...prevState, editableKeys}));
    }

    const columns: ProColumns<UDFRegisterInfo>[] = [
        {
            title: '名称',
            dataIndex: 'name',
            width: '10%',
            // readonly: udfRegisterState.isEdit && !udfRegisterState.isAdd,
            formItemProps: {
                rules: [
                    {
                        required: true,
                        message: '请输入名称',
                    },
                ],
            },
        },
        {
            title: '类名',
            dataIndex: 'className',
            // readonly: !udfRegisterState.isEdit && udfRegisterState.isAdd,
            readonly: true,
            width: '15%',
        },
        {
            title: '语言',
            dataIndex: 'dialect',
            readonly: true,
            // readonly: !udfRegisterState.isEdit && udfRegisterState.isAdd,
            width: '8%',
        },
        {
            title: '来源',
            dataIndex: 'source',
            readonly: true,
            // readonly: !udfRegisterState.isEdit && udfRegisterState.isAdd,
            width: '10%',
        },
        {
            title: '文件名',
            dataIndex: 'fileName',
            readonly: true,
            // readonly: !udfRegisterState.isEdit && udfRegisterState.isAdd,
            width: '20%',
        },
        {
            title: '更新时间',
            dataIndex: 'updateTime',
            readonly: true,
            // readonly: !udfRegisterState.isEdit && udfRegisterState.isAdd,
            valueType: 'dateTime',
            width: '15%',
        },
        {
            title: '操作',
            valueType: 'option',
            width: '10%',
            render: (_text, record, _, action) => {
                return [
                    <EditBtn key={`${record.id}_edit`} onClick={() => {
                        action?.startEditable?.(record.id);
                        setUDFRegisterState(prevState => ({...prevState, isEdit: true, isAdd: false}));
                    }}/>
                    , record.source == "develop" ?
                        <PopconfirmDeleteBtn key={`${record.id}_delete`} onClick={() => {
                        }} description={"确定删除吗???"}/>
                        : <></>
                ]
            },
        }
    ]


    return (
        <>
            <Space>
                <Button
                    type="primary"
                    onClick={() => {
                        setShowEdit(true);
                    }}
                    icon={<PlusOutlined/>}
                >
                    新增
                </Button>
            </Space>
            <EditableProTable<UDFRegisterInfo>
                rowKey="id"
                headerTitle={false}
                maxLength={20}
                pagination={{
                    showQuickJumper: true,
                    pageSize: 20,
                }}
                controlled
                recordCreatorProps={false}
                actionRef={actionRef}
                editable={{
                    type: 'single',
                    editableKeys: udfRegisterState.editableKeys,
                    onChange: editableKeysChange,
                }}
                columns={columns}
                value={req.data}

            />
            <Modal width={1500} open={showEdit} destroyOnClose closable onCancel={() => setShowEdit(false)}
                   onOk={() => {
                       add(targetKeys).then(() => {
                           req.refresh().then(() => {
                               setShowEdit(false)
                           })
                       })
                   }}>
                <TreeTransfer dataSource={buildResourceTreeData(req_resources.data ?? [])}
                              targetKeys={targetKeys} onChange={setTargetKeys}/>
            </Modal>
        </>
    )
}

export default UDFRegister

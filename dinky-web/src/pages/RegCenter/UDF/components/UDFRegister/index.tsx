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

import {ProColumns} from "@ant-design/pro-components";
import {UDFRegisterInfo, UDFRegisterInfoParent} from "@/types/RegCenter/data";
import {EditBtn} from "@/components/CallBackButton/EditBtn";
import {PopconfirmDeleteBtn} from "@/components/CallBackButton/PopconfirmDeleteBtn";
import {Key, useEffect, useRef, useState} from "react";
import ProTable, {ActionType} from "@ant-design/pro-table";
import {useRequest} from "@@/plugin-request";
import {API_CONSTANTS} from "@/services/endpoints";
import {Button, Modal} from "antd";
import {PlusOutlined} from "@ant-design/icons";
import {TreeTransfer} from "@/components/Transfer/TreeTransfer";
import {buildResourceTreeData} from "@/pages/RegCenter/Resource/components/FileTree/function";
import {add, update} from "./service";
import {l} from "@/utils/intl";


const UDFRegister = () => {
    const req = useRequest<{
        data: UDFRegisterInfo[]
    }>({
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

    const groupData: {
        [p: string]: UDFRegisterInfo[] | undefined
    } = Object.fromEntries(
        Array.from(new Set(req.data?.map(({fileName}) => fileName))).map((type) => [
            type,
            req.data?.filter((item) => item.fileName === type),
        ]),
    )
    const parentData: UDFRegisterInfoParent[] = Object.keys(groupData)?.map((key) => {
        const d = groupData[key] ?? [];
        return {
            resourcesId: d[0].resourcesId,
            dialect: d[0].dialect,
            source: d[0].source,
            fileName: key,
            num: d.length,
        }
    })


    const columnsParent: ProColumns<UDFRegisterInfoParent>[] = [
        {
            title: '文件名称',
            width: 120,
            dataIndex: 'fileName',
        }, {
            title: 'udf解析数量',
            width: 120,
            dataIndex: 'num',
        }, {
            title: '来源',
            width: 120,
            dataIndex: 'source',
            valueEnum: {
                resources: {text: "resources"},
                develop: {text: 'develop'},
            }
        }, {
            title: '语言',
            width: 120,
            dataIndex: 'dialect',
            valueEnum: {
                java: {text: "Java"},
                python: {text: 'Python'},
            }
        }
    ]
    const expandedRowRender = (expandedRow: UDFRegisterInfoParent) => {

        const columns: ProColumns<UDFRegisterInfo>[] = [
            {
                title: '名称',
                dataIndex: 'name',
                width: '10%',
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
                readonly: true,
                width: '15%',
            },
            {
                title: '更新时间',
                dataIndex: 'updateTime',
                readonly: true,
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
            <ProTable
                rowKey={"id"}
                columns={columns}
                headerTitle={false}
                search={false}
                options={false}
                dataSource={groupData[expandedRow.fileName]}
                pagination={false}
                actionRef={actionRef}
                editable={{
                    deleteText: false,
                    type: 'single',
                    editableKeys: udfRegisterState.editableKeys,
                    onChange: editableKeysChange,
                    onSave: async (_, row) => {
                        await update(row.id, row.name);
                        await req.refresh();
                        actionRef.current?.reload();
                    },
                    actionRender: (_, _2, defaultDom) => [
                        defaultDom.save,
                        defaultDom.cancel
                    ]
                }}
            />
        );
    };


    return (
        <>
            <ProTable<UDFRegisterInfoParent>
                dataSource={parentData}
                columns={columnsParent}
                rowKey="resourcesId"
                pagination={{
                    showQuickJumper: true,
                }}
                expandable={{expandedRowRender}}
                search={false}
                dateFormatter="string"
                options={false}
                toolBarRender={() => [
                    <Button
                        type="primary"
                        onClick={() => {
                            setShowEdit(true);
                        }}
                        icon={<PlusOutlined/>}
                    >
                        {l('button.create')}
                    </Button>
                ]}
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
                                  targetKeys={targetKeys} onChange={setTargetKeys} height={500}/>
            </Modal>
        </>
    )
}

export default UDFRegister

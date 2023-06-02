/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

import React, {useState} from "react";
import {ProFieldFCMode, ProList} from "@ant-design/pro-components";
import {BaseConfigProperties} from "@/types/SettingCenter/data";
import {Descriptions, Input, Space, Switch, Tag} from "antd";
import {l} from "@/utils/intl";
import {SettingTwoTone} from "@ant-design/icons";
import {EditBtn} from "@/components/CallBackButton/EditBtn";
import {SWITCH_OPTIONS} from "@/services/constants";

const GeneralConfig = (props: any) => {
    const [state, setState] = useState<ProFieldFCMode>('read');
    const [value, setValue] = useState<any>();

    return <>
        <ProList<BaseConfigProperties>
            rowKey="key"
            style={{margin: 0}}
            size={'small'}
            dataSource={props.data}
            showActions="hover"
            metas={{
                title: {
                    editable: false,
                    render: (dom: any, entity: BaseConfigProperties) => {
                        return <>
                            <Space size={10}>
                                <Descriptions.Item>
                                    {l(`sys.${entity.key}`)}
                                </Descriptions.Item>
                                {props.tag}
                            </Space>
                        </>
                    }
                },
                avatar: {
                    editable: false,
                    render: (dom: any, entity: BaseConfigProperties) => {
                        return <SettingTwoTone/>
                    }
                },
                description: {
                    editable: false,
                    render: (dom: any, entity: BaseConfigProperties) => {
                        return <>{l(`sys.${entity.key}.note`)}</>
                    }
                },
                content: {
                    dataIndex: 'value',
                    valueType: (item: BaseConfigProperties) => item.frontType,
                    renderFormItem: (entity: BaseConfigProperties, config, form) => {
                        console.log(config, form, entity, 'renderFormItem');
                        return <>
                            {
                                entity.frontType === 'boolean' ?
                                    <Switch
                                        {...SWITCH_OPTIONS()}
                                        style={{width: '4vw'}}
                                        checked={entity.value}
                                        onChange={() => console.log(entity)}
                                    />
                                    :
                                    <Input style={{width: '30vw'}} value={entity.value}/>
                            }
                        </>
                    },
                    render: (dom: any, entity: BaseConfigProperties) => {
                        return <>
                            {
                                entity.frontType === 'boolean' ?
                                    <Switch
                                        {...SWITCH_OPTIONS()}
                                        style={{width: '4vw'}}
                                        checked={entity.value}
                                        onChange={() => console.log(entity)}
                                    />
                                    :
                                    <Input style={{width: '30vw'}} disabled value={entity.value}/>
                            }
                        </>
                    }
                },
                actions: {
                    render: (text: string, row: BaseConfigProperties, index: number, action: any) => [
                        <EditBtn
                            onClick={() => {
                                action.startEditable(row.key);
                                setState('update')
                            }}
                        />
                    ],
                },
            }}
            editable={{
                onDelete: undefined,
                onSave: async (key, record, originRow) => {
                    console.log(key, record, originRow);
                    return true;
                },
            }}
        />
    </>
}

export default GeneralConfig;
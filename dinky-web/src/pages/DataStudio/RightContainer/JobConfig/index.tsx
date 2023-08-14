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

import {InfoCircleOutlined} from "@ant-design/icons";
import {Badge, Space, Typography} from "antd";
import {l} from "@/utils/intl";
import {RUN_MODE, SWITCH_OPTIONS} from "@/services/constants";
import {connect} from "umi";
import {SessionType, StateType} from "@/pages/DataStudio/model";
import {AlertStateType} from "@/pages/RegCenter/Alert/AlertInstance/model";
import {getCurrentData} from "@/pages/DataStudio/function";
import {useForm} from "antd/es/form/Form";
import {
    buildAlertGroupOptions,
    buildClusterConfigOptions,
    buildClusterOptions,
    buildEnvOptions,
    buildRunModelOptions
} from "@/pages/DataStudio/RightContainer/JobConfig/function";
import {
    ProForm,
    ProFormDigit,
    ProFormGroup,
    ProFormList,
    ProFormSelect,
    ProFormSwitch,
    ProFormText
} from "@ant-design/pro-components";
import {SAVE_POINT_TYPE} from "@/pages/DataStudio/constants";
import React from "react";

const {Text} = Typography;

const JobConfig = (props: any) => {
    const {
        sessionCluster,
        clusterConfiguration,
        dispatch,
        tabs: {panes, activeKey},
        env,
        group,
        rightContainer,
    } = props;

    const current = getCurrentData(panes, activeKey);
    const currentSession: SessionType = {
        connectors: [], sessionConfig: {
            clusterId: current.clusterId,
            clusterName: current.clusterName,
        }
    }
    const [form] = useForm();
    form.setFieldsValue(current);


    const onValuesChange = (change: any, all: any) => {
        for (let i = 0; i < panes.length; i++) {
            if (panes[i].key === activeKey) {
                for (let key in change) {
                    panes[i].params.taskData[key] = all[key];
                }
                break;
            }
        }
        dispatch({
            type: "Studio/saveTabs",
            payload: {...props.tabs},
        });
    };

    const onChangeClusterSession = () => {
        //todo 这里需要验证

        // showTables(currentSession.session, dispatch);
    };

    return <div style={{maxHeight: rightContainer.height}}>
        <ProForm size={'middle'}
                 initialValues={{
                     name: RUN_MODE.LOCAL,
                     envId: 0,
                     parallelism: 1,
                     savePointStrategy: 0,
                     alertGroupId: 0,
                 }}
                 className={"data-studio-form"}
                 style={{paddingInline: '15px' ,overflow: 'scroll'}}
                 form={form}
                 submitter={false}
                 layout="vertical"
                 onValuesChange={onValuesChange}
        >
            <ProFormSelect
                name="type"
                label={l('global.table.execmode')}
                tooltip={l('pages.datastudio.label.jobConfig.execmode.tip')}
                options={buildRunModelOptions()}
                showSearch
            />

            {(current.type === RUN_MODE.YARN_SESSION || current.type === RUN_MODE.KUBERNETES_SESSION || current.type === RUN_MODE.STANDALONE) &&
                <>
                    {
                        currentSession.session ?
                            (currentSession.sessionConfig && currentSession.sessionConfig.clusterId ?
                                    <><Badge status="success"/><Text
                                        type="success">{currentSession.sessionConfig.clusterName}</Text></>
                                    : <><Badge status="error"/><Text
                                        type="danger">{l('pages.devops.jobinfo.localenv')}</Text></>
                            ) :
                            <>
                                <ProFormSelect
                                    style={{width: '100%'}}
                                    placeholder={l('pages.datastudio.label.jobConfig.cluster.tip')}
                                    label={l('pages.datastudio.label.jobConfig.cluster')}
                                    tooltip={l('pages.datastudio.label.jobConfig.clusterConfig.tip1', '', {type: current.type})}
                                    name="clusterId"
                                    options={buildClusterOptions(sessionCluster)}
                                    fieldProps={{
                                        onChange: onChangeClusterSession,
                                    }}
                                />
                            </>
                    }
                </>
            }
            {(current.type === RUN_MODE.YARN_PER_JOB || current.type === RUN_MODE.YARN_APPLICATION
                || current.type === RUN_MODE.KUBERNETES_APPLICATION || current.type === RUN_MODE.KUBERNETES_APPLICATION_OPERATOR) && <>
                <ProFormSelect
                    name="clusterConfigurationId"
                    placeholder={l('pages.datastudio.label.jobConfig.clusterConfig.tip2')}
                    label={l('pages.datastudio.label.jobConfig.clusterConfig')}
                    tooltip={l('pages.datastudio.label.jobConfig.clusterConfig.tip1', '', {
                        type: current.type
                    })}
                    options={buildClusterConfigOptions(current, clusterConfiguration)}
                />
            </>}

            <ProFormSelect
                name="envId"
                label={l('pages.datastudio.label.jobConfig.flinksql.env')}
                tooltip={l('pages.datastudio.label.jobConfig.flinksql.env.tip1')}
                options={buildEnvOptions(env)}
                showSearch
            />

            <ProFormGroup>
                <ProFormDigit
                    width={'xs'}
                    label={l('pages.datastudio.label.jobConfig.parallelism')}
                    name="parallelism"
                    tooltip={l('pages.datastudio.label.jobConfig.parallelism.tip')}
                    max={9999} min={1}
                />

                <ProFormSwitch
                    label={l('pages.datastudio.label.jobConfig.insert')} name="statementSet"
                    valuePropName="checked"
                    tooltip={{
                        title: l('pages.datastudio.label.jobConfig.insert.tip'),
                        icon: <InfoCircleOutlined/>
                    }}
                    {...SWITCH_OPTIONS()}
                />
                <ProFormSwitch
                    label={l('pages.datastudio.label.jobConfig.fragment')} name="fragment"
                    valuePropName="checked"
                    tooltip={{
                        title: l('pages.datastudio.label.jobConfig.fragment.tip'),
                        icon: <InfoCircleOutlined/>
                    }}
                    {...SWITCH_OPTIONS()}
                />
                <ProFormSwitch
                    label={l('pages.datastudio.label.jobConfig.batchmode')} name="batchModel"
                    valuePropName="checked"
                    tooltip={{
                        title: l('pages.datastudio.label.jobConfig.batchmode.tip'),
                        icon: <InfoCircleOutlined/>
                    }}
                    {...SWITCH_OPTIONS()}
                />
            </ProFormGroup>

            <ProFormSelect
                label={l('pages.datastudio.label.jobConfig.savePointStrategy')}
                name="savePointStrategy"
                tooltip={l('pages.datastudio.label.jobConfig.savePointStrategy.tip')}
                options={SAVE_POINT_TYPE}
            />

            {current.savePointStrategy === 3 &&
                <>
                    <ProFormText
                        label={l('pages.datastudio.label.jobConfig.savePointpath')}
                        name="savePointPath"
                        tooltip={l('pages.datastudio.label.jobConfig.savePointpath.tip1')}
                        placeholder={l('pages.datastudio.label.jobConfig.savePointpath.tip2')}
                    />
                </>
            }
            <ProFormSelect
                label={l('pages.datastudio.label.jobConfig.alertGroup')} name="alertGroupId"
                placeholder={l('pages.datastudio.label.jobConfig.alertGroup.tip')}
                options={buildAlertGroupOptions(group)}
            />

            {/*todo 这里需要优化，有有异常抛出*/}
            <ProFormList
                label={l('pages.datastudio.label.jobConfig.other')}
                tooltip={l('pages.datastudio.label.jobConfig.other.tip')}
                name={'configJson'}
                copyIconProps={false}
                creatorButtonProps={{
                    style: {width: '100%'},
                    creatorButtonText: l('pages.datastudio.label.jobConfig.addConfig'),
                }}
            >
                <ProFormGroup  style={{display: 'flex',width: '100%'}} >
                    <Space key={'config'} style={{display: 'flex'}} align="baseline">
                        <ProFormText name="key"
                                     placeholder={l('pages.datastudio.label.jobConfig.addConfig.params')}/>
                        <ProFormText name="value"
                                     placeholder={l('pages.datastudio.label.jobConfig.addConfig.value')}/>
                    </Space>
                </ProFormGroup>
            </ProFormList>
        </ProForm>
    </div>
};

export default connect(({Studio, Alert}: { Studio: StateType, Alert: AlertStateType }) => ({
    sessionCluster: Studio.sessionCluster,
    clusterConfiguration: Studio.clusterConfiguration,
    rightContainer: Studio.rightContainer,
    tabs: Studio.tabs,
    env: Studio.env,
    group: Alert.group,
}))(JobConfig);

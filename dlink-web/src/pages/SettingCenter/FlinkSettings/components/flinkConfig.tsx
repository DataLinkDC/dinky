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

import React, {useEffect, useState} from 'react';
import {Form, Input, List, Switch} from 'antd';
import {connect} from "umi";
import {SettingsStateType} from "@/pages/SettingCenter/FlinkSettings/model";
import {saveSettings} from "@/pages/SettingCenter/FlinkSettings/function";

type FlinkConfigProps = {
  sqlSubmitJarPath: SettingsStateType['sqlSubmitJarPath'];
  sqlSubmitJarParas: SettingsStateType['sqlSubmitJarParas'];
  sqlSubmitJarMainAppClass: SettingsStateType['sqlSubmitJarMainAppClass'];
  useRestAPI: SettingsStateType['useRestAPI'];
  useLogicalPlan: SettingsStateType['useLogicalPlan'];
  sqlSeparator: SettingsStateType['sqlSeparator'];
  jobIdWait: SettingsStateType['jobIdWait'];
  dispatch: any;
};

const FlinkConfigView: React.FC<FlinkConfigProps> = (props) => {

  const {
    sqlSubmitJarPath,
    sqlSubmitJarParas,
    sqlSubmitJarMainAppClass,
    useRestAPI,
    useLogicalPlan,
    sqlSeparator,
    jobIdWait,
    dispatch
  } = props;
  const [editName, setEditName] = useState<string>('');
  const [formValues, setFormValues] = useState(props);
  const [form] = Form.useForm();

  useEffect(() => {
    form.setFieldsValue(props);
  }, [props]);

  const getData = () => [
    {
      title: '提交FlinkSQL的Jar文件路径',
      description: (
        editName != 'sqlSubmitJarPath' ?
          (sqlSubmitJarPath ? sqlSubmitJarPath : '未设置') : (
            <Input
              id='sqlSubmitJarPath'
              defaultValue={sqlSubmitJarPath}
              onChange={onChange}
              placeholder="hdfs:///dlink/jar/dlink-app.jar"/>)),
      actions: editName != 'sqlSubmitJarPath' ? [<a onClick={({}) => handleEditClick('sqlSubmitJarPath')}>修改</a>] :
        [<a onClick={({}) => handleSaveClick('sqlSubmitJarPath')}>保存</a>,
          <a onClick={({}) => handleCancelClick()}>取消</a>],
    },
    {
      title: '提交FlinkSQL的Jar的主类入参',
      description: (
        editName != 'sqlSubmitJarParas' ?
          (sqlSubmitJarParas ? sqlSubmitJarParas : '未设置') : (<Input
            id='sqlSubmitJarParas'
            defaultValue={sqlSubmitJarParas}
            onChange={onChange}
            placeholder=""/>)),
      actions: editName != 'sqlSubmitJarParas' ? [<a onClick={({}) => handleEditClick('sqlSubmitJarParas')}>修改</a>] :
        [<a onClick={({}) => handleSaveClick('sqlSubmitJarParas')}>保存</a>,
          <a onClick={({}) => handleCancelClick()}>取消</a>],
    },
    {
      title: '提交FlinkSQL的Jar的主类',
      description: (
        editName != 'sqlSubmitJarMainAppClass' ?
          (sqlSubmitJarMainAppClass ? sqlSubmitJarMainAppClass : '未设置') : (<Input
            id='sqlSubmitJarMainAppClass'
            defaultValue={sqlSubmitJarMainAppClass}
            onChange={onChange}
            placeholder="com.dlink.app.MainApp"/>)),
      actions: editName != 'sqlSubmitJarMainAppClass' ? [<a
          onClick={({}) => handleEditClick('sqlSubmitJarMainAppClass')}>修改</a>] :
        [<a onClick={({}) => handleSaveClick('sqlSubmitJarMainAppClass')}>保存</a>,
          <a onClick={({}) => handleCancelClick()}>取消</a>],
    }, {
      title: '使用 RestAPI',
      description: '启用后，Flink 任务的 savepoint、停止等操作将通过 JobManager 的 RestAPI 进行',
      actions: [
        <Form.Item
          name="useRestAPI" valuePropName="checked"
        >
          <Switch checkedChildren="启用" unCheckedChildren="禁用"
                  checked={useRestAPI}
          /></Form.Item>],
    }, {
      title: 'FlinkSQL语句分割符',
      description: (
        editName != 'sqlSeparator' ?
          (sqlSeparator ? sqlSeparator : '未设置') : (<Input
            id='sqlSeparator'
            defaultValue={sqlSeparator}
            onChange={onChange}
            placeholder=";"/>)),
      actions: editName != 'sqlSeparator' ? [<a onClick={({}) => handleEditClick('sqlSeparator')}>修改</a>] :
        [<a onClick={({}) => handleSaveClick('sqlSeparator')}>保存</a>,
          <a onClick={({}) => handleCancelClick()}>取消</a>],
    },
    {
      title: '使用逻辑计划计算血缘',
      description: '在计算 Flink 任务的字段血缘分析时是否基于逻辑计划进行，只支持 1.14 版本',
      actions: [
        <Form.Item
          name="useLogicalPlan" valuePropName="checked"
        >
          <Switch checkedChildren="启用" unCheckedChildren="禁用"
                  checked={useLogicalPlan}
          /></Form.Item>],
    },
    {
      title: '获取 Job ID 的最大等待时间（秒）',
      description: (
        editName != 'jobIdWait' ?
          (jobIdWait ? jobIdWait : '30') : (
            <Input
              id='jobIdWait'
              defaultValue={jobIdWait}
              onChange={onChange}
              placeholder="30"/>)),
      actions: editName != 'jobIdWait' ? [<a onClick={({}) => handleEditClick('jobIdWait')}>修改</a>] :
        [<a onClick={({}) => handleSaveClick('jobIdWait')}>保存</a>,
          <a onClick={({}) => handleCancelClick()}>取消</a>],
    },
  ];

  const onChange = e => {
    let values = {};
    values[e.target.id] = e.target.value;
    setFormValues({...formValues, ...values});
  };

  const onValuesChange = (change: any, all: any) => {
    let values = {};
    for (let key in change) {
      values[key] = all[key];
    }
    saveSettings(values, dispatch);
  };

  const handleEditClick = (name: string) => {
    setEditName(name);
  };

  const handleSaveClick = (name: string) => {
    if (formValues[name] != props[name]) {
      let values = {};
      values[name] = formValues[name];
      saveSettings(values, dispatch);
    }
    setEditName('');
  };

  const handleCancelClick = () => {
    setFormValues(props);
    setEditName('');
  };

  const data = getData();
  return (
    <>
      <Form
        form={form}
        layout="vertical"
        onValuesChange={onValuesChange}
      >
        <List
          itemLayout="horizontal"
          dataSource={data}
          renderItem={(item) => (
            <List.Item actions={item.actions}>
              <List.Item.Meta title={item.title} description={item.description}/>
            </List.Item>
          )}
        />
      </Form>
    </>
  );
};
export default connect(({Settings}: { Settings: SettingsStateType }) => ({
  sqlSubmitJarPath: Settings.sqlSubmitJarPath,
  sqlSubmitJarParas: Settings.sqlSubmitJarParas,
  sqlSubmitJarMainAppClass: Settings.sqlSubmitJarMainAppClass,
  useRestAPI: Settings.useRestAPI,
  useLogicalPlan: Settings.useLogicalPlan,
  sqlSeparator: Settings.sqlSeparator,
  jobIdWait: Settings.jobIdWait,
}))(FlinkConfigView);

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
import {connect, useIntl} from "umi";
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


  const intl = useIntl();
  const l = (id: string, defaultMessage?: string, value?: {}) => intl.formatMessage({id, defaultMessage}, value);

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
      title: l('pages.settings.FlinkURL'),
      description: (
        editName != 'sqlSubmitJarPath' ?
          (sqlSubmitJarPath ? sqlSubmitJarPath : l('pages.settings.FlinkNoSetting')) : (
            <Input
              id='sqlSubmitJarPath'
              defaultValue={sqlSubmitJarPath}
              onChange={onChange}
              placeholder="hdfs:///dlink/jar/dlink-app.jar"/>)),
      actions: editName != 'sqlSubmitJarPath' ? [<a
          onClick={({}) => handleEditClick('sqlSubmitJarPath')}>{l('pages.settings.FlinkUpdate')}</a>] :
        [<a onClick={({}) => handleSaveClick('sqlSubmitJarPath')}>{l('pages.settings.FlinkSave')}</a>,
          <a onClick={({}) => handleCancelClick()}>{l('pages.settings.FlinkCancel')}</a>],
    },
    {
      title: l('pages.settings.FlinkSQLJarMainParameter'),
      description: (
        editName != 'sqlSubmitJarParas' ?
          (sqlSubmitJarParas ? sqlSubmitJarParas : l('pages.settings.FlinkNoSetting')) : (<Input
            id='sqlSubmitJarParas'
            defaultValue={sqlSubmitJarParas}
            onChange={onChange}
            placeholder=""/>)),
      actions: editName != 'sqlSubmitJarParas' ? [<a
          onClick={({}) => handleEditClick('sqlSubmitJarParas')}>{l('pages.settings.FlinkUpdate')}</a>] :
        [<a onClick={({}) => handleSaveClick('sqlSubmitJarParas')}>{l('pages.settings.FlinkSave')}</a>,
          <a onClick={({}) => handleCancelClick()}>{l('pages.settings.FlinkCancel')}</a>],
    },
    {
      title: l('pages.settings.FlinkSQLJarMainClass'),
      description: (
        editName != 'sqlSubmitJarMainAppClass' ?
          (sqlSubmitJarMainAppClass ? sqlSubmitJarMainAppClass : l('pages.settings.FlinkNoSetting')) : (<Input
            id='sqlSubmitJarMainAppClass'
            defaultValue={sqlSubmitJarMainAppClass}
            onChange={onChange}
            placeholder="com.dlink.app.MainApp"/>)),
      actions: editName != 'sqlSubmitJarMainAppClass' ? [<a
          onClick={({}) => handleEditClick('sqlSubmitJarMainAppClass')}>{l('pages.settings.FlinkUpdate')}</a>] :
        [<a onClick={({}) => handleSaveClick('sqlSubmitJarMainAppClass')}>{l('pages.settings.FlinkSave')}</a>,
          <a onClick={({}) => handleCancelClick()}>{l('pages.settings.FlinkCancel')}</a>],
    }, {
      title: l('pages.settings.FlinkRestAPI'),
      description: l('pages.settings.FlinkNoUseSetting'),
      actions: [
        <Form.Item
          name="useRestAPI" valuePropName="checked"
        >
          <Switch checkedChildren={l('pages.settings.FlinkUse')}
                  unCheckedChildren={l('pages.settings.FlinkNotUse')}
                  checked={useRestAPI}
          /></Form.Item>],
    }, {
      title: l('pages.settings.FlinkURLSplit'),
      description: (
        editName != 'sqlSeparator' ?
          (sqlSeparator ? sqlSeparator : l('pages.settings.FlinkNoSetting')) : (<Input
            id='sqlSeparator'
            defaultValue={sqlSeparator}
            onChange={onChange}
            placeholder=";"/>)),
      actions: editName != 'sqlSeparator' ? [<a
          onClick={({}) => handleEditClick('sqlSeparator')}>{l('pages.settings.FlinkUpdate')}</a>] :
        [<a onClick={({}) => handleSaveClick('sqlSeparator')}>{l('pages.settings.FlinkSave')}</a>,
          <a onClick={({}) => handleCancelClick()}>{l('pages.settings.FlinkCancel')}</a>],
    },
    {
      title: l('pages.settings.FlinkSQLLogic'),
      description: l('pages.settings.FlinkLogic'),
      actions: [
        <Form.Item
          name="useLogicalPlan" valuePropName="checked"
        >
          <Switch checkedChildren={l('pages.settings.FlinkUse')}
                  unCheckedChildren={l('pages.settings.FlinkNotUse')}
                  checked={useLogicalPlan}
          /></Form.Item>],
    },
    {
      title: l('pages.settings.FlinkJobID'),
      description: (
        editName != 'jobIdWait' ?
          (jobIdWait ? jobIdWait : '30') : (
            <Input
              id='jobIdWait'
              defaultValue={jobIdWait}
              onChange={onChange}
              placeholder="30"/>)),
      actions: editName != 'jobIdWait' ? [<a
          onClick={({}) => handleEditClick('jobIdWait')}>{l('pages.settings.FlinkUpdate')}</a>] :
        [<a onClick={({}) => handleSaveClick('jobIdWait')}>{l('pages.settings.FlinkSave')}</a>,
          <a onClick={({}) => handleCancelClick()}>{l('pages.settings.FlinkCancel')}</a>],
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

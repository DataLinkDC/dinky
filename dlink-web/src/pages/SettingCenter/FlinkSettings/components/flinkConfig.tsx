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
import {l} from "@/utils/intl";

type FlinkConfigProps = {
  sqlSubmitJarPath: SettingsStateType['sqlSubmitJarPath'];
  sqlSubmitJarParas: SettingsStateType['sqlSubmitJarParas'];
  sqlSubmitJarMainAppClass: SettingsStateType['sqlSubmitJarMainAppClass'];
  useRestAPI: SettingsStateType['useRestAPI'];
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
          onClick={({}) => handleEditClick('sqlSubmitJarPath')}>{l('button.edit')}</a>] :
        [<a onClick={({}) => handleSaveClick('sqlSubmitJarPath')}>{l('button.save')}</a>,
          <a onClick={({}) => handleCancelClick()}>{l('button.cancel')}</a>],
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
          onClick={({}) => handleEditClick('sqlSubmitJarParas')}>{l('button.edit')}</a>] :
        [<a onClick={({}) => handleSaveClick('sqlSubmitJarParas')}>{l('button.save')}</a>,
          <a onClick={({}) => handleCancelClick()}>{l('button.cancel')}</a>],
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
          onClick={({}) => handleEditClick('sqlSubmitJarMainAppClass')}>{l('button.edit')}</a>] :
        [<a onClick={({}) => handleSaveClick('sqlSubmitJarMainAppClass')}>{l('button.save')}</a>,
          <a onClick={({}) => handleCancelClick()}>{l('button.cancel')}</a>],
    }, {
      title: l('pages.settings.FlinkRestAPI'),
      description: l('pages.settings.FlinkNoUseSetting'),
      actions: [
        <Form.Item
          name="useRestAPI" valuePropName="checked"
        >
          <Switch checkedChildren={l('button.enable')}
                  unCheckedChildren={l('button.disable')}
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
          onClick={({}) => handleEditClick('sqlSeparator')}>{l('button.edit')}</a>] :
        [<a onClick={({}) => handleSaveClick('sqlSeparator')}>{l('button.save')}</a>,
          <a onClick={({}) => handleCancelClick()}>{l('button.cancel')}</a>],
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
          onClick={({}) => handleEditClick('jobIdWait')}>{l('button.edit')}</a>] :
        [<a onClick={({}) => handleSaveClick('jobIdWait')}>{l('button.save')}</a>,
          <a onClick={({}) => handleCancelClick()}>{l('button.cancel')}</a>],
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
  sqlSeparator: Settings.sqlSeparator,
  jobIdWait: Settings.jobIdWait,
}))(FlinkConfigView);

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

import {Divider, Form, Input, Space,} from 'antd';
import {Dispatch, DocumentStateType} from "@@/plugin-dva/connect";
import {connect} from "umi";
import Button from "antd/es/button/button";
import {useState} from "react";
import {
  APP_CONFIG_LIST,
  Config,
  FLINK_CONFIG_NAME_LIST,
  KUBERNETES_CONFIG_NAME_LIST
} from "@/pages/RegistrationCenter/ClusterManage/ClusterConfiguration/conf";
import {MinusCircleOutlined, PlusOutlined} from "@ant-design/icons";
import {getConfig} from "@/pages/RegistrationCenter/ClusterManage/ClusterConfiguration/function";
import {l} from "@/utils/intl";


const formLayout = {
  labelCol: {span: 7},
  wrapperCol: {span: 13},
};

const StudioKubernetes = (props: any) => {

  const {
    height = '100%',
    width = '100%',
    conf,
  } = props;

  const [form] = Form.useForm();

  const CUSTOM_KUBERNETS_CONFIG_LIST: Config[] = [
    {
    name: 'kubernetes.jobmanager.cpu',
    lable: 'kubernetes.jobmanager.cpu',
    showType: 'input',
      placeholder: l('pages.rc.clusterConfig.help.kubernets.jmcpu'),
  }, {
    name: 'kubernetes.taskmanager.cpu',
    lable: 'kubernetes.taskmanager.cpu',
    showType: 'input',
    placeholder: l('pages.rc.clusterConfig.help.kubernets.tmcpu'),
  },
  ];
  const CUSTOM_FLINK_CONFIG_LIST: Config[] = [
    {
      name: 'jobmanager.memory.process.size',
      lable: 'jobmanager.memory.process.size',
      placeholder: l('pages.rc.clusterConfig.help.kubernets.jobManagerMemory'),
    }, {
      name: 'taskmanager.memory.process.size',
      lable: 'taskmanager.memory.process.size',
      placeholder: l('pages.rc.clusterConfig.help.kubernets.taskManagerMemory'),
    }, {
      name: 'taskmanager.numberOfTaskSlots',
      lable: 'taskmanager.numberOfTaskSlots',
      placeholder: '',
    }
  ];
  //Separate normal configuration and user-defined configuration.
  function addMapToList(map: Record<string, unknown>, keys: string[]) {
    const list = [];
    for (const i in map) {
      if (!keys.includes(i)) {
        list.push({
          name: i,
          value: map[i],
        })
      }
    }
    return list;
  }

  //Merge pre-defined config
  function mergeConfig(source: Record<string, unknown>, from: Record<string, unknown>) {
    for (const key in from) {
      source[key] = from[key];
    }
    return source;
  }

  const initValue = (configJson: string) => {
    //formValue cannot show hierarchical relationships
    // and needs to be converted from a profile within the database to display
    const config = configJson ? JSON.parse(configJson) : {}
    let initValues = {}
    //Normal configuration and user-defined configuration are mixed together
    // need to be separated
    //Normal config set
    if (config["kubernetesConfig"]) {
      initValues = mergeConfig(initValues, config["kubernetesConfig"])
    }
    if (config["flinkConfig"]) {
      initValues = mergeConfig(initValues, config["flinkConfig"])
    }
    if (config["appConfig"]) {
      initValues = mergeConfig(initValues, config["appConfig"])
    }
    //user custom config set
    initValues["flinkConfigList"] = addMapToList(config["flinkConfig"], FLINK_CONFIG_NAME_LIST())
    initValues["kubernetesConfigList"] = addMapToList(config["kubernetesConfig"], KUBERNETES_CONFIG_NAME_LIST())
    return initValues;
  }

  const [formVals, setFormVals] = useState<Record<string, unknown>>(initValue(conf));


  const onValuesChange = (change: any, all: any) => {
    all.type = "Kubernetes"
    const values = getConfig(all)
    let appConfig = {}
    APP_CONFIG_LIST.forEach((value, index) => {
      appConfig[APP_CONFIG_LIST[index].name] = all[APP_CONFIG_LIST[index].name]
    })
    setFormVals(all)
    props.saveSql(JSON.stringify({...values,"appConfig":appConfig}))
  }

  // build pre-defined config item
  const buildConfig = (config: Config[]) => {
    const itemList: JSX.Element[] = [];
    config.forEach(configItem => {
      itemList.push(<Form.Item
        key={configItem.name}
        name={configItem.name}
        label={configItem.lable}
        help={configItem.help}
      >
        <Input placeholder={configItem.placeholder}/>

      </Form.Item>)
    });
    return itemList;
  };
  // build user-defined config item
  const buildOtherConfig = (itemName: string, configName: string, addDescription: string) => {
    return (
      <Form.Item
        label={itemName}
      >
        <Form.List name={configName}>
          {(fields, {add, remove}) => (
            <>
              {fields.map(({key, name, fieldKey, ...restField}) => (
                <Space key={key} style={{display: 'flex'}} align="baseline">
                  <Form.Item
                    {...restField}
                    name={[name, 'name']}
                    fieldKey={[fieldKey, 'name']}
                  >
                    <Input placeholder="name"/>
                  </Form.Item>
                  <Form.Item
                    {...restField}
                    name={[name, 'value']}
                    fieldKey={[fieldKey, 'value']}
                  >
                    <Input placeholder="value"/>
                  </Form.Item>
                  <MinusCircleOutlined onClick={() => remove(name)}/>
                </Space>
              ))}
              <Form.Item>
                <Button type="dashed" onClick={() => add()} block icon={<PlusOutlined/>}>
                  {addDescription}
                </Button>
              </Form.Item>
            </>
          )}
        </Form.List>
      </Form.Item>
    )
  }
  // render
  const renderContent = () => {
    return (
      <>
        <Divider>{l('pages.rc.clusterConfig.appConfig')}</Divider>
        {buildConfig(APP_CONFIG_LIST)}
        <Divider>{l('pages.rc.clusterConfig.k8sConfig')}</Divider>
        {buildConfig(CUSTOM_KUBERNETS_CONFIG_LIST)}
        <Divider>{l('pages.rc.clusterConfig.flinkConfig')}</Divider>
        {buildConfig(CUSTOM_FLINK_CONFIG_LIST)}
        {buildOtherConfig(l('pages.rc.clusterConfig.otherConfig'),
          "flinkConfigList",
          l('pages.rc.clusterConfig.addDefineConfig'))}

      </>
    );
  };


  return (
    <div style={{
      width: width,
      height: height,
      padding: "10px",
      overflowY: "scroll"
    }}>
      <Form
        style={{width: "1000px"}}
        labelAlign={"left"}
        {...formLayout}
        form={form}
        initialValues={formVals}
        onValuesChange={onValuesChange}
      >
        {renderContent()}
      </Form>
    </div>
  );
}


const mapDispatchToProps = (dispatch: Dispatch) => ({
  saveSql: (val: any) => dispatch({
    type: "Studio/saveSql",
    payload: val,
  }), saveSqlMetaData: (sqlMetaData: any, key: number) => dispatch({
    type: "Studio/saveSqlMetaData",
    payload: {
      activeKey: key,
      sqlMetaData,
      isModified: true,
    }
  }),
})

export default connect(({Document}: { Document: DocumentStateType }) => ({
  fillDocuments: Document.fillDocuments,
}), mapDispatchToProps)(StudioKubernetes);

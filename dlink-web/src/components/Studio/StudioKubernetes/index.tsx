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
import {connect, useIntl} from "umi";
import Button from "antd/es/button/button";
import {useState} from "react";
import {
  APP_CONFIG_LIST,
  Config,
  FLINK_CONFIG_LIST,
  FLINK_CONFIG_NAME_LIST,
  KUBERNETES_CONFIG_LIST,
  KUBERNETES_CONFIG_NAME_LIST
} from "@/pages/ClusterConfiguration/conf";
import {MinusCircleOutlined, PlusOutlined} from "@ant-design/icons";
import {getConfig} from "@/pages/ClusterConfiguration/function";


const formLayout = {
  labelCol: {span: 7},
  wrapperCol: {span: 13},
};

const StudioKubernetes = (props: any) => {

  const intl = useIntl();
  const l = (id: string, defaultMessage?: string, value?: {}) => intl.formatMessage({id, defaultMessage}, value);

  const {
    height = '100%',
    width = '100%',
    conf,
  } = props;

  const [form] = Form.useForm();

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
    //App config is mix in flink config , need separated
    APP_CONFIG_LIST.forEach((value, index) => {
      initValues[APP_CONFIG_LIST[index].name] = config[APP_CONFIG_LIST[index].name]
    })
    //user custom config set
    initValues["flinkConfigList"] = addMapToList(config["flinkConfig"], FLINK_CONFIG_NAME_LIST())
    initValues["kubernetesConfigList"] = addMapToList(config["kubernetesConfig"], KUBERNETES_CONFIG_NAME_LIST())
    initValues["flinkConfigPath"] = config["flinkConfigPath"]
    return initValues;
  }

  const [formVals, setFormVals] = useState<Record<string, unknown>>(initValue(conf));


  const onValuesChange = (change: any, all: any) => {
    all.type = "Kubernetes"
    const values = getConfig(all)
    APP_CONFIG_LIST.forEach((value, index) => {
      values[APP_CONFIG_LIST[index].name] = all[APP_CONFIG_LIST[index].name]
    })
    setFormVals(all)
    props.saveSql(JSON.stringify(values))
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
        <Input placeholder={configItem.placeholder}
          // defaultValue={configItem.defaultValue}
        />

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
        <Divider>Kubernetes 配置(必选)</Divider>
        {buildConfig(KUBERNETES_CONFIG_LIST)}
        {buildOtherConfig("其他配置", "kubernetesConfigList", "添加一个自定义项")}
        <Divider>App 配置(必选)</Divider>
        <Form.Item
          name="flinkConfigPath"
          label="配置文件路径"
          rules={[{required: true, message: '请输入 flink-conf.yaml 路径！'}]}
          help="指定 flink-conf.yaml 的路径（末尾无/）"
        >
          <Input placeholder="值如 /opt/flink/conf"/>
        </Form.Item>

        {buildConfig(APP_CONFIG_LIST)}

        <Divider>其他配置(可选)</Divider>
        {buildConfig(FLINK_CONFIG_LIST)}
        {buildOtherConfig("其他配置", "flinkConfigList", "添加一个自定义项")}
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

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

import React, {useState} from 'react';
import {Button, Divider, Form, Input, message, Modal, Select, Space, Switch, Upload} from 'antd';
import {MinusCircleOutlined, PlusOutlined, UploadOutlined} from '@ant-design/icons';
import {getConfig, getConfigFormValues} from "@/pages/ClusterConfiguration/function";
import type {Config} from "@/pages/ClusterConfiguration/conf";
import {FLINK_CONFIG_LIST, HADOOP_CONFIG_LIST, KUBERNETES_CONFIG_LIST} from "@/pages/ClusterConfiguration/conf";
import {testClusterConfigurationConnect} from "@/pages/ClusterConfiguration/service";
import type {ClusterConfigurationTableListItem} from "@/pages/ClusterConfiguration/data";
import {CODE} from "@/components/Common/crud";
import {useIntl} from 'umi';

export type ClusterConfigurationFormProps = {
  onCancel: (flag?: boolean) => void;
  onSubmit: (values: Partial<ClusterConfigurationTableListItem>) => void;
  modalVisible: boolean;
  values: Partial<ClusterConfigurationTableListItem>;
};
const {Option} = Select;

const formLayout = {
  labelCol: {span: 7},
  wrapperCol: {span: 13},
};

const ClusterConfigurationForm: React.FC<ClusterConfigurationFormProps> = (props) => {


  const intl = useIntl();
  const l = (id: string, defaultMessage?: string, value?: {}) => intl.formatMessage({id, defaultMessage}, value);


  const [form] = Form.useForm();
  const [formVals, setFormVals] = useState<Partial<ClusterConfigurationTableListItem>>({
    id: props.values.id,
    name: props.values.name,
    alias: props.values.alias,
    type: props.values.type ? props.values.type : "Yarn",
    configJson: props.values.configJson,
    note: props.values.note,
    enabled: props.values.enabled,
  });

  const [hadoopConfigPath, setHadoopConfigPath] = useState<string>(getConfigFormValues(formVals)['hadoopConfigPath']);
  const [flinkLibPath, setFlinkLibPath] = useState<string>(getConfigFormValues(formVals)['flinkLibPath']);
  const [flinkConfigPath, setFlinkConfigPath] = useState<string>(getConfigFormValues(formVals)['flinkConfigPath']);

  const {
    onSubmit: handleSubmit,
    onCancel: handleModalVisible,
    modalVisible,
  } = props;

  const onValuesChange = (change: any, all: any) => {
    setFormVals({...formVals, ...change});
    setHadoopConfigPath(all['hadoopConfigPath']);
    setFlinkLibPath(all['flinkLibPath']);
    setFlinkConfigPath(all['flinkConfigPath']);
  };

  const buildConfig = (config: Config[]) => {
    const itemList: JSX.Element[] = [];
    config.forEach(configItem => {
      itemList.push(<Form.Item
        name={configItem.name}
        label={configItem.lable}
      >
        <Input placeholder={configItem.placeholder} defaultValue={configItem.defaultValue}/>
      </Form.Item>)
    });
    return itemList;
  };

  const submitForm = async () => {
    const fieldsValue = await form.validateFields();
    const formValues = {
      id: formVals.id,
      name: fieldsValue.name,
      alias: fieldsValue.alias,
      type: fieldsValue.type,
      note: fieldsValue.note,
      enabled: fieldsValue.enabled,
      configJson: JSON.stringify(getConfig(fieldsValue)),
    };
    setFormVals(formValues);
    handleSubmit(formValues);
  };

  const getUploadProps = (dir: string) => {
    return {
      name: 'files',
      action: '/api/fileUpload',
      headers: {
        authorization: 'authorization-text',
      },
      data: {
        dir
      },
      showUploadList: true,
      onChange(info) {
        if (info.file.status === 'done') {
          if (info.file.response.code == CODE.SUCCESS) {
            message.success(info.file.response.msg);
          } else {
            message.warn(info.file.response.msg);
          }
        } else if (info.file.status === 'error') {
          message.error(`${info.file.name} 上传失败`);
        }
      },
    }
  };

  const getUploadHdfsProps = (dir: string) => {
    return {
      name: 'files',
      action: '/api/fileUpload/hdfs',
      headers: {
        authorization: 'authorization-text',
      },
      data: {
        dir,
        hadoopConfigPath
      },
      showUploadList: true,
      onChange(info) {
        if (info.file.status === 'done') {
          if (info.file.response.code == CODE.SUCCESS) {
            message.success(info.file.response.msg);
          } else {
            message.warn(info.file.response.msg);
          }
        } else if (info.file.status === 'error') {
          message.error(`${info.file.name} 上传失败`);
        }
      },
    }
  };

  const renderContent = (formValsPara: Partial<ClusterConfigurationTableListItem>) => {
    return (
      <>
        <Form.Item
          name="type"
          label={l('pages.registerCenter.clusterConfig.type')}
        >
          <Select defaultValue="Yarn" value="Yarn">
            <Option value="Yarn">Flink On Yarn</Option>
            <Option value="Kubernetes">Flink On Kubernetes</Option>
          </Select>
        </Form.Item>
        {formValsPara.type == 'Yarn' ? <>
          <Divider>{l('pages.registerCenter.clusterConfig.hadoopConfig')}</Divider>
          <Form.Item
            name="hadoopConfigPath"
            label={l('pages.registerCenter.clusterConfig.hadoopConfigPath')}
            rules={[{required: true, message: l('pages.registerCenter.clusterConfig.hadoopConfigPathPlaceholder')}]}
            help={l('pages.registerCenter.clusterConfig.hadoopConfigPathHelp')}
          >
            <Input placeholder={l('pages.registerCenter.clusterConfig.hadoopConfigPath')} addonAfter={
              <Form.Item name="suffix" noStyle>
                <Upload {...getUploadProps(hadoopConfigPath)} multiple>
                  <UploadOutlined/>
                </Upload>
              </Form.Item>}/>
          </Form.Item>
          <Divider orientation="left" plain>{l('pages.registerCenter.clusterConfig.defineConfig.highPriority')}</Divider>
          {buildConfig(HADOOP_CONFIG_LIST)}
          <Form.Item
            label={l('pages.registerCenter.clusterConfig.otherConfig')}
          >
            <Form.List name="hadoopConfigList">
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
                      {l('pages.registerCenter.clusterConfig.addDefineConfig')}
                    </Button>
                  </Form.Item>
                </>
              )}
            </Form.List>
          </Form.Item></> : undefined}
        {formValsPara.type == 'Kubernetes' ? <>
          <Divider>{l('pages.registerCenter.clusterConfig.k8sConfig')}</Divider>
          {buildConfig(KUBERNETES_CONFIG_LIST)}
          <Form.Item
            label={l('pages.registerCenter.clusterConfig.otherConfig')}
          >
            <Form.List name="kubernetesConfigList">
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
                      {l('pages.registerCenter.clusterConfig.addDefineConfig')}
                    </Button>
                  </Form.Item>
                </>
              )}
            </Form.List>
          </Form.Item>
        </> : undefined}
        <Divider>{l('pages.registerCenter.clusterConfig.flinkConfig')}</Divider>
        {formValsPara.type == 'Yarn' ? <>
          <Form.Item
            name="flinkLibPath"
            label={l('pages.registerCenter.clusterConfig.libPath')}
            rules={[{required: true, message: l('pages.registerCenter.clusterConfig.libPathPlaceholder')}]}
            help={l('pages.registerCenter.clusterConfig.libPathHelp')}
          >
            <Input placeholder={l('pages.registerCenter.clusterConfig.libPathPlaceholder')} addonAfter={
              <Form.Item name="suffix" noStyle>
                <Upload {...getUploadHdfsProps(flinkLibPath)} multiple>
                  <UploadOutlined/>
                </Upload>
              </Form.Item>}/>
          </Form.Item>
        </> : undefined}
        <Form.Item
          name="flinkConfigPath"
          label={l('pages.registerCenter.clusterConfig.flinkConfigPath')}
          rules={[{required: true, message: l('pages.registerCenter.clusterConfig.flinkConfigPathPlaceholder')}]}
          help={l('pages.registerCenter.clusterConfig.flinkConfigPathHelp')}
        >
          <Input placeholder={l('pages.registerCenter.clusterConfig.flinkConfigPathPlaceholder')} addonAfter={
            <Form.Item name="suffix" noStyle>
              <Upload {...getUploadProps(flinkConfigPath)}>
                <UploadOutlined/>
              </Upload>
            </Form.Item>}/>
        </Form.Item>
        <Divider orientation="left" plain>{l('pages.registerCenter.clusterConfig.defineConfig.highPriority')}</Divider>
        {buildConfig(FLINK_CONFIG_LIST)}
        <Form.Item
          label={l('pages.registerCenter.clusterConfig.otherConfig')}
        >
          <Form.List name="flinkConfigList">
            {(fields, {add, remove}) => (
              <>
                {fields.map(({key, name, fieldKey, ...restField}) => (
                  <Space key={key} style={{display: 'flex', marginBottom: 8}} align="baseline">
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
                    {l('pages.registerCenter.clusterConfig.addDefineConfig')}
                  </Button>
                </Form.Item>
              </>
            )}
          </Form.List>
        </Form.Item>
        <Divider>{l('pages.registerCenter.clusterConfig.baseConfig')}</Divider>
        <Form.Item
          name="name"
          label={l('pages.registerCenter.clusterConfig.name')}
          rules={[{required: true, message: l('pages.registerCenter.clusterConfig.namePlaceholder')}]}>
          <Input placeholder={l('pages.registerCenter.clusterConfig.namePlaceholder')}/>
        </Form.Item>
        <Form.Item
          name="alias"
          label={l('pages.registerCenter.clusterConfig.alias')}
        >
          <Input placeholder={l('pages.registerCenter.clusterConfig.aliasPlaceholder')}/>
        </Form.Item>
        <Form.Item
          name="note"
          label={l('global.table.note')}
        >
          <Input.TextArea placeholder={l('global.table.notePlaceholder')} allowClear
                          autoSize={{minRows: 3, maxRows: 10}}/>
        </Form.Item>
        <Form.Item
          name="enabled"
          label={l('global.table.isEnable')}>
          <Switch  checkedChildren={l('button.enable')} unCheckedChildren={l('button.disable')}
                  defaultChecked={formValsPara.enabled}/>
        </Form.Item>
      </>
    );
  };

  const testForm = async () => {
    const fieldsValue = await form.validateFields();
    const formValues = {
      id: formVals.id,
      name: fieldsValue.name,
      alias: fieldsValue.alias,
      type: fieldsValue.type,
      note: fieldsValue.note,
      enabled: fieldsValue.enabled,
      configJson: JSON.stringify(getConfig(fieldsValue)),
    } as ClusterConfigurationTableListItem;
    setFormVals(formValues);
    testClusterConfigurationConnect(formValues);
  };

  const renderFooter = () => {
    return (
      <>
        <Button onClick={() => handleModalVisible(false)}>{l('button.cancel')}</Button>
        <Button type="primary" htmlType="button" onClick={testForm}>{l('button.test')}</Button>
        <Button type="primary" onClick={() => submitForm()}>{l('button.finish')}</Button>
      </>
    );
  };

  return (
    <Modal
      width={"60%"}
      bodyStyle={{padding: '32px 40px 48px', height: '600px', overflowY: 'auto'}}
      destroyOnClose
      title={formVals.id ? l('pages.registerCenter.clusterConfig.modify') : l('pages.registerCenter.clusterConfig.create')}
      visible={modalVisible}
      footer={renderFooter()}
      onCancel={() => handleModalVisible()}
    >
      <Form
        {...formLayout}
        form={form}
        initialValues={getConfigFormValues(formVals)}
        onValuesChange={onValuesChange}
      >
        {renderContent(formVals)}
      </Form>
    </Modal>
  );
};

export default ClusterConfigurationForm;

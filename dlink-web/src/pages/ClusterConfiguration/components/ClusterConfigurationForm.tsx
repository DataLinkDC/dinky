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
          label="类型"
        >
          <Select defaultValue="Yarn" value="Yarn">
            <Option value="Yarn">Flink On Yarn</Option>
            <Option value="Kubernetes">Flink On Kubernetes</Option>
          </Select>
        </Form.Item>
        {formValsPara.type == 'Yarn' ? <>
          <Divider>Hadoop 配置</Divider>
          <Form.Item
            name="hadoopConfigPath"
            label="配置文件路径"
            rules={[{required: true, message: '请输入 hadoop 配置文件路径！'}]}
            help="指定配置文件路径（末尾无/），需要包含以下文件：core-site.xml,hdfs-site.xml,yarn-site.xml"
          >
            <Input placeholder="值如 /etc/hadoop/conf" addonAfter={
              <Form.Item name="suffix" noStyle>
                <Upload {...getUploadProps(hadoopConfigPath)} multiple>
                  <UploadOutlined/>
                </Upload>
              </Form.Item>}/>
          </Form.Item>
          <Divider orientation="left" plain>自定义配置（高优先级）</Divider>
          {buildConfig(HADOOP_CONFIG_LIST)}
          <Form.Item
            label="其他配置"
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
                      添加一个自定义项
                    </Button>
                  </Form.Item>
                </>
              )}
            </Form.List>
          </Form.Item></> : undefined}
        {formValsPara.type == 'Kubernetes' ? <>
          <Divider>Kubernetes 配置</Divider>
          {buildConfig(KUBERNETES_CONFIG_LIST)}
          <Form.Item
            label="其他配置"
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
                      添加一个自定义项
                    </Button>
                  </Form.Item>
                </>
              )}
            </Form.List>
          </Form.Item>
        </> : undefined}
        <Divider>Flink 配置</Divider>
        {formValsPara.type == 'Yarn' ? <>
          <Form.Item
            name="flinkLibPath"
            label="lib 路径"
            rules={[{required: true, message: '请输入 lib 的 hdfs 路径！'}]}
            help="指定 lib 的 hdfs 路径（末尾无/），需要包含 Flink 运行时的依赖"
          >
            <Input placeholder="值如 hdfs:///flink/lib" addonAfter={
              <Form.Item name="suffix" noStyle>
                <Upload {...getUploadHdfsProps(flinkLibPath)} multiple>
                  <UploadOutlined/>
                </Upload>
              </Form.Item>}/>
          </Form.Item>
        </> : undefined}
        <Form.Item
          name="flinkConfigPath"
          label="配置文件路径"
          rules={[{required: true, message: '请输入 flink-conf.yaml 路径！'}]}
          help="指定 flink-conf.yaml 的路径（末尾无/）"
        >
          <Input placeholder="值如 /opt/module/flink/conf" addonAfter={
            <Form.Item name="suffix" noStyle>
              <Upload {...getUploadProps(flinkConfigPath)}>
                <UploadOutlined/>
              </Upload>
            </Form.Item>}/>
        </Form.Item>
        <Divider orientation="left" plain>自定义配置（高优先级）</Divider>
        {buildConfig(FLINK_CONFIG_LIST)}
        <Form.Item
          label="其他配置"
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
                    添加一个自定义项
                  </Button>
                </Form.Item>
              </>
            )}
          </Form.List>
        </Form.Item>
        <Divider>基本配置</Divider>
        <Form.Item
          name="name"
          label="名称"
          rules={[{required: true, message: '请输入名称！'}]}>
          <Input placeholder="请输入唯一英文标识"/>
        </Form.Item>
        <Form.Item
          name="alias"
          label="别名"
        >
          <Input placeholder="请输入名称"/>
        </Form.Item>
        <Form.Item
          name="note"
          label="注释"
        >
          <Input.TextArea placeholder="请输入文本注释" allowClear
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
        <Button type="primary" htmlType="button" onClick={testForm}>
          测试
        </Button>
        <Button type="primary" onClick={() => submitForm()}>
          {l('button.finish')}
        </Button>
      </>
    );
  };

  return (
    <Modal
      width={"60%"}
      bodyStyle={{padding: '32px 40px 48px', height: '600px', overflowY: 'auto'}}
      destroyOnClose
      title={formVals.id ? "维护集群配置" : "创建集群配置"}
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

import React, {useEffect, useState} from 'react';
import {Form, Button, Input, Modal, Select,Divider,Space} from 'antd';
import { MinusCircleOutlined, PlusOutlined } from '@ant-design/icons';
import {ClusterConfigurationTableListItem} from "@/pages/ClusterConfiguration/data";

export type ClusterConfigurationFormProps = {
  onCancel: (flag?: boolean) => void;
  onSubmit: (values: Partial<ClusterConfigurationTableListItem>) => void;
  modalVisible: boolean;
};
const Option = Select.Option;

const formLayout = {
  labelCol: {span: 7},
  wrapperCol: {span: 13},
};

const ClusterConfigurationForm: React.FC<ClusterConfigurationFormProps> = (props) => {

  const [form] = Form.useForm();
  const {
    onSubmit: handleSubmit,
    onCancel: handleModalVisible,
    modalVisible,
  } = props;

  const submitForm = async () => {
    const fieldsValue = await form.validateFields();
    handleSubmit(fieldsValue);
  };

  const renderContent = () => {
    return (
      <>
        <Form.Item
          name="type"
          label="类型"
        >
          <Select defaultValue="Yarn" allowClear>
            <Option value="Yarn">Flink On Yarn</Option>
          </Select>
        </Form.Item>
        <Divider>Hadoop 配置</Divider>
        <Form.Item
          name="hadoopConfigPath"
          label="配置文件路径"
          help="可指定配置文件路径（末尾无/），需要包含以下文件：core-site.xml,hdfs-site.xml,yarn-site.xml"
        >
          <Input placeholder="值如 /usr/local/dlink/conf"/>
        </Form.Item>
        <Divider orientation="left" plain>自定义配置（高优先级）</Divider>
        <Form.Item
          name="ha.zookeeper.quorum"
          label="ha.zookeeper.quorum"
        >
          <Input placeholder="值如 192.168.123.1:2181,192.168.123.2:2181,192.168.123.3:2181"/>
        </Form.Item>
        <Form.Item
          label="其他配置"
        >
        <Form.List name="hadoopConfig">
          {(fields, { add, remove }) => (
            <>
              {fields.map(({ key, name, fieldKey, ...restField }) => (
                <Space key={key} style={{ display: 'flex' }} align="baseline">
                  <Form.Item
                    {...restField}
                    name={[name, 'name']}
                    fieldKey={[fieldKey, 'name']}
                  >
                    <Input placeholder="name" />
                  </Form.Item>
                  <Form.Item
                    {...restField}
                    name={[name, 'value']}
                    fieldKey={[fieldKey, 'value']}
                  >
                    <Input placeholder="value" />
                  </Form.Item>
                  <MinusCircleOutlined onClick={() => remove(name)} />
                </Space>
              ))}
              <Form.Item>
                <Button type="dashed" onClick={() => add()} block icon={<PlusOutlined />}>
                  添加一个自定义项
                </Button>
              </Form.Item>
            </>
          )}
        </Form.List>
        </Form.Item>
        <Divider>Flink 配置</Divider>
        <Form.Item
          name="flinkLibPath"
          label="lib 路径"
          rules={[{required: true, message: '请输入 lib 路径！'}]}
          help="必须指定 lib 的 hdfs 路径（末尾无/），需要包含 Flink 运行时的依赖"
        >
          <Input placeholder="值如 hdfs:///flink/lib"/>
        </Form.Item>
        <Form.Item
          name="flinkConfigPath"
          label="配置文件路径"
          help="可指定配置文件 flink-conf.yaml 的具体路径"
        >
          <Input placeholder="值如 /usr/local/dlink/conf/flink-conf.yaml"/>
        </Form.Item>
        <Divider orientation="left" plain>自定义配置（高优先级）</Divider>
        <Form.Item
          name="jobmanager.memory.process.size"
          label="jobmanager.memory.process.size"
        >
          <Input placeholder="值如 1024m"/>
        </Form.Item>
        <Form.Item
          name="taskmanager.memory.flink.size"
          label="taskmanager.memory.flink.size"
        >
          <Input placeholder="值如 1024m"/>
        </Form.Item>
        <Form.Item
          name="taskmanager.memory.framework.heap.size"
          label="taskmanager.memory.framework.heap.size"
        >
          <Input placeholder="值如 1024m"/>
        </Form.Item>
        <Form.Item
          name="taskmanager.numberOfTaskSlots"
          label="taskmanager.numberOfTaskSlots"
        >
          <Input placeholder="值如 4"/>
        </Form.Item>
        <Form.Item
          name="parallelism.default"
          label="parallelism.default"
        >
          <Input placeholder="值如 4"/>
        </Form.Item>
        <Form.Item
          label="其他配置"
        >
        <Form.List name="flinkConfig">
          {(fields, { add, remove }) => (
            <>
              {fields.map(({ key, name, fieldKey, ...restField }) => (
                <Space key={key} style={{ display: 'flex', marginBottom: 8 }} align="baseline">
                  <Form.Item
                    {...restField}
                    name={[name, 'name']}
                    fieldKey={[fieldKey, 'name']}
                  >
                    <Input placeholder="name" />
                  </Form.Item>
                  <Form.Item
                    {...restField}
                    name={[name, 'value']}
                    fieldKey={[fieldKey, 'value']}
                  >
                    <Input placeholder="value" />
                  </Form.Item>
                  <MinusCircleOutlined onClick={() => remove(name)} />
                </Space>
              ))}
              <Form.Item>
                <Button type="dashed" onClick={() => add()} block icon={<PlusOutlined />}>
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
          label="标识"
          rules={[{required: true, message: '请输入名称！'}]}>
          <Input placeholder="请输入唯一英文标识"/>
        </Form.Item>
        <Form.Item
          name="alias"
          label="名称"
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
      </>
    );
  };

  const renderFooter = () => {
    return (
      <>
        <Button onClick={() => handleModalVisible(false)}>取消</Button>
        <Button type="primary" onClick={() => submitForm()}>
          完成
        </Button>
      </>
    );
  };

  return (
    <Modal
      width={1200}
      bodyStyle={{padding: '32px 40px 48px'}}
      destroyOnClose
      title="创建集群配置"
      visible={modalVisible}
      footer={renderFooter()}
      onCancel={() => handleModalVisible()}
    >
      <Form
        {...formLayout}
        form={form}
      >
        {renderContent()}
      </Form>
    </Modal>
  );
};

export default ClusterConfigurationForm;

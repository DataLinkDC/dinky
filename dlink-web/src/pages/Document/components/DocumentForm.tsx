import React, {useState} from 'react';
import {Button, Form, Input, Modal, Select, Switch} from 'antd';
import {DocumentTableListItem} from "@/pages/Document/data";
import TextArea from "antd/es/input/TextArea";
import {getDocumentFormData,} from "@/pages/Document/function";

export type DocumentFormProps  = {
  onCancel: (flag?: boolean) => void;
  onSubmit: (values: Partial<DocumentTableListItem>) => void;
  modalVisible: boolean;
  values: Partial<DocumentTableListItem>;
  // instance: DocumentTableListItem[];
};

const FormItem = Form.Item;
const Option = Select.Option;


const formLayout = {
  labelCol: {span: 7},
  wrapperCol: {span: 13},
};

const DocumentForm: React.FC<DocumentFormProps> = (props) => {

  const [form] = Form.useForm();
  const [formVals, setFormVals] = useState<Partial<DocumentTableListItem>>({
    id: props.values.id,
    name: props.values.name,
    category: props.values.category,
    type: props.values.type,
    subtype: props.values.subtype,
    description: props.values.description,
    fillValue: props.values.fillValue,
    version: props.values.version,
    likeNum: props.values.likeNum,
    enabled: props.values.enabled?props.values.enabled:true,
  });

  const {
    onSubmit: handleSubmit,
    onCancel: handleModalVisible,
    modalVisible,
  } = props;


  const submitForm = async () => {
    const fieldsValue = await form.validateFields();
    setFormVals({...formVals,...fieldsValue});
    handleSubmit({...formVals,...fieldsValue});
  };

  const renderContent = (formVals) => {
    return (
      <>
        <FormItem
          name="name"
          label="名称"
          rules={[{required: true, message: '请输入名称！'}]}>
          <Input placeholder="请输入"/>
        </FormItem>
        <FormItem
          name="category"
          label="文档类型"
          rules={[{required: true, message: '请选择该文档所属类型！'}]}
        >
          <Select  allowClear>
            <Option value="Method">Method</Option>
            <Option value="Function">Function</Option>
            <Option value="Constructor">Constructor</Option>
            <Option value="Field">Field</Option>
            <Option value="Variable">Variable</Option>
            <Option value="Class">Class</Option>
            <Option value="Struct">Struct</Option>
            <Option value="Interface">Interface</Option>
            <Option value="Module">Module</Option>
            <Option value="Property">Property</Option>
            <Option value="Event">Event</Option>
            <Option value="Operator">Operator</Option>
            <Option value="Unit">Unit</Option>
            <Option value="Value">Value</Option>
            <Option value="Constant">Constant</Option>
            <Option value="Enum">Enum</Option>
            <Option value="EnumMember">EnumMember</Option>
            <Option value="Keyword">Keyword</Option>
            <Option value="Text">Text</Option>
            <Option value="Color">Color</Option>
            <Option value="File">File</Option>
            <Option value="Reference">Reference</Option>
            <Option value="Customcolor">Customcolor</Option>
            <Option value="Folder">Folder</Option>
            <Option value="TypeParameter">TypeParameter</Option>
            <Option value="User">User</Option>
            <Option value="Issue">Issue</Option>
            <Option value="Snippet">Snippet</Option>
          </Select>
        </FormItem>
        <FormItem
          name="type"
          label="类型"
          rules={[{required: true, message: '请选择该文档所属函数类型！'}]}
        >
          <Select  allowClear>
            <Option value="优化参数">优化参数</Option>
            <Option value="建表语句">建表语句</Option>
            <Option value="CataLog">CataLog</Option>
            <Option value="设置参数">设置参数</Option>
            <Option value="内置函数">内置函数</Option>
            <Option value="UDF">UDF</Option>
            <Option value="Other">Other</Option>
          </Select>
        </FormItem>
        <FormItem
          name="subtype"
          label="子类型"
          rules={[{required: true, message: '请选择该文档所属函数子类型！'}]}
        >
          <Select allowClear>
            <Option value="比较函数">比较函数</Option>
            <Option value="逻辑函数">逻辑函数</Option>
            <Option value="算术函数">算术函数</Option>
            <Option value="字符串函数">字符串函数</Option>
            <Option value="时间函数">时间函数</Option>
            <Option value="条件函数">条件函数</Option>
            <Option value="类型转换函数">类型转换函数</Option>
            <Option value="Collection 函数">Collection 函数</Option>
            <Option value="Value Collection 函数">Value Collection 函数</Option>
            <Option value="Value Access 函数">Value Access 函数</Option>
            <Option value="分组函数">分组函数</Option>
            <Option value="hash函数">hash函数</Option>
            <Option value="聚合函数">聚合函数</Option>
            <Option value="列函数">列函数</Option>
            <Option value="表值聚合函数">表值聚合函数</Option>
            <Option value="其他函数">其他函数</Option>
          </Select>
        </FormItem>
        <FormItem
          name="description"
          label="描述"
        >
          <TextArea placeholder="请输入文档描述信息!" allowClear autoSize={{minRows: 3, maxRows: 10}}/>
        </FormItem>
        <FormItem
          name="fillValue"
          label="填充值"
          rules={[{required: true, message: '请输入填充值！'}]}
        >
          <TextArea placeholder="请输入填充值,编辑器内使用名称触发提示 eg: 如果希望在函数LTRIM(parms)中输入参数 则语法为: LTRIM(${1:})  此时的1代表第一个光标 如需多个 数字+1即可 tab键切换光标 ; 如不需要参数则直接输入期望填充值"
                    allowClear
                    autoSize={{minRows: 3, maxRows: 10}}/>
        </FormItem>
        <FormItem
          name="version"
          label="版本"
          rules={[{required: true, message: '请选择该文档所属版本！'}]}
        >
          <Select allowClear >
            <Option value="1.11">Flink-1.11</Option>
            <Option value="1.12">Flink-1.12</Option>
            <Option value="1.13">Flink-1.13</Option>
            <Option value="1.14">Flink-1.14</Option>
            <Option value="1.15">Flink-1.15</Option>
            <Option  value="ALL Version">ALL Version</Option>
          </Select>
        </FormItem>
        <FormItem
          name="enabled"
          label="是否启用"
          rules={[{required: true, message: '请输入是否启用！'}]}>
          <Switch checkedChildren="启用" unCheckedChildren="禁用"
                  defaultChecked={formVals.enabled}/>
        </FormItem>
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
      title={formVals.id?"维护文档":"创建文档"}
      visible={modalVisible}
      footer={renderFooter()}
      onCancel={() => handleModalVisible()}
    >
      <Form
        {...formLayout}
        form={form}
        initialValues={getDocumentFormData(formVals)}
      >
        {renderContent(getDocumentFormData(formVals))}
      </Form>
    </Modal>
  );
};

export default DocumentForm;

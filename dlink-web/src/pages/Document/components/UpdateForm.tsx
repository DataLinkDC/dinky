import React, {useEffect, useState} from 'react';
import { Form, Button, Input, Modal,Select } from 'antd';

import Switch from "antd/es/switch";
import TextArea from "antd/es/input/TextArea";
import {DocumentTableListItem} from "@/pages/Document/data";

export type UpdateFormProps = {
    onCancel: (flag?: boolean, formVals?: Partial<DocumentTableListItem>) => void;
    onSubmit: (values: Partial<DocumentTableListItem>) => void;
    updateModalVisible: boolean;
    values: Partial<DocumentTableListItem>;
};
const FormItem = Form.Item;
const Option = Select.Option;

const formLayout = {
    labelCol: { span: 7 },
    wrapperCol: { span: 13 },
};

const UpdateForm: React.FC<UpdateFormProps> = (props) => {
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
        enabled: props.values.enabled,
    });

    const [form] = Form.useForm();

    const {
        onSubmit: handleUpdate,
        onCancel: handleUpdateModalVisible,
        updateModalVisible,
        values,
    } = props;

    const submitForm = async () => {
        const fieldsValue = await form.validateFields();
        setFormVals({ ...formVals, ...fieldsValue });
        handleUpdate({ ...formVals, ...fieldsValue });
    };

    const renderContent = (formVals) => {
        return (
            <>
                    <FormItem
                        name="name"
                        label="名称"
 rules={[{ required: true, message: '请输入名称！' }]}                     >
                        <Input placeholder="请输入" />
                    </FormItem>
                    <FormItem
                        name="category"
                        label="文档类型"
                    >
                      <Select defaultValue="function" allowClear>
                        <Option value="function">函数</Option>
                      </Select>
                    </FormItem>
                    <FormItem
                        name="type"
                        label="类型"
                    >
                      <Select defaultValue="内置函数" allowClear>
                        <Option value="内置函数">内置函数</Option>
                        <Option value="UDF">UDF</Option>
                      </Select>
                    </FormItem>
              <FormItem
                        name="subtype"
                        label="子类型"
                    >
                      <Select defaultValue="比较函数" allowClear>
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
                <TextArea placeholder="" allowClear autoSize={{ minRows: 3, maxRows: 10 }}/>
              </FormItem>
              <FormItem
                name="fillValue"
                label="填充值"
              >
                <TextArea placeholder="" allowClear autoSize={{ minRows: 3, maxRows: 10 }}/>
              </FormItem>
                    <FormItem
                        name="version"
                        label="版本"
                    >
                        <Input placeholder="请输入" />
                    </FormItem>
                    <FormItem
                        name="enabled"
                        label="是否启用"
 rules={[{ required: true, message: '请输入是否启用！' }]}                     >
                        <Switch checkedChildren="启用" unCheckedChildren="禁用"
                                defaultChecked={formVals.enabled}/>
                    </FormItem>
            </>
        );
    };

    const renderFooter = () => {
        return (
            <>
                <Button onClick={() => handleUpdateModalVisible(false, values)}>取消</Button>
                <Button type="primary" onClick={() => submitForm()}>
                    完成
                </Button>
            </>
        );
    };

    return (
        <Modal
            width={640}
            bodyStyle={{ padding: '32px 40px 48px' }}
            destroyOnClose
            title="编辑文档"
            visible={updateModalVisible}
            footer={renderFooter()}
            onCancel={() => handleUpdateModalVisible()}
        >
            <Form
                {...formLayout}
                form={form}
                initialValues={{
        id: formVals.id,
        name: formVals.name,
        category: formVals.category,
        type: formVals.type,
        subtype: formVals.subtype,
        description: formVals.description,
                  fillValue: formVals.fillValue,
        version: formVals.version,
        likeNum: formVals.likeNum,
        enabled: formVals.enabled,
                }}
            >
                {renderContent(formVals)}
            </Form>
        </Modal>
    );
};

export default UpdateForm;

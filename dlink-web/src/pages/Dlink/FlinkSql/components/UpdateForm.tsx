import React, {useEffect, useState} from 'react';
import { Form, Button, Input, Modal, InputNumber, Select } from 'antd';

import type { TableListItem } from '../data.d';
import Switch from "antd/es/switch";
import TextArea from "antd/es/input/TextArea";

export type UpdateFormProps = {
    onCancel: (flag?: boolean, formVals?: Partial<TableListItem>) => void;
    onSubmit: (values: Partial<TableListItem>) => void;
    updateModalVisible: boolean;
    values: Partial<TableListItem>;
};
const FormItem = Form.Item;

const formLayout = {
    labelCol: { span: 7 },
    wrapperCol: { span: 13 },
};

const UpdateForm: React.FC<UpdateFormProps> = (props) => {
    const [formVals, setFormVals] = useState<Partial<TableListItem>>({
        id: props.values.id,
        name: props.values.name,
        alias: props.values.alias,
        type: props.values.type,
        sqlIndex: props.values.sqlIndex,
        statement: props.values.statement,
        note: props.values.note,
        enabled: props.values.enabled,
        taskId: props.values.taskId,
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
                    >
                        <Input placeholder="请输入" />
                    </FormItem>
                    <FormItem
                        name="alias"
                        label="表名"
                    >
                        <Input placeholder="请输入" />
                    </FormItem>
                    <FormItem
                        name="type"
                        label="类型"
                    >
                      <Select defaultValue="CREATE TABLE" allowClear>
                        <Option value="CREATE TABLE">CREATE TABLE</Option>
                        <Option value="INSERT INTO">INSERT INTO</Option>
                        <Option value="CREATE VIEW">CREATE VIEW</Option>
                        <Option value="CREATE AGGTABLE">CREATE AGGTABLE</Option>
                      </Select>
                    </FormItem>
                    <FormItem
                        name="sqlIndex"
                        label="次序"
                    >
                      <InputNumber min={0} max={100}/>
                    </FormItem>
                    <FormItem
                        name="statement"
                        label="语句"
                    >
                      <TextArea placeholder="添加 Flink Sql..." allowClear autoSize={{ minRows: 3, maxRows: 10 }}/>
                    </FormItem>
                    <FormItem
                        name="note"
                        label="备注"
                    >
                      <TextArea placeholder="添加备注" allowClear />
                    </FormItem>
                    <FormItem
                        name="enabled"
                        label="是否启用"
 rules={[{ required: true, message: '请输入是否启用！' }]}                     >
                        <Switch checkedChildren="启用" unCheckedChildren="禁用"
                                defaultChecked={formVals.enabled}/>
                    </FormItem>
                    <FormItem
                        name="taskId"
                        label="任务ID"
 rules={[{ required: true, message: '请输入任务ID！' }]}                     >
                        <Input placeholder="请输入" />
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
            title="编辑FlinkSql"
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
        alias: formVals.alias,
        type: formVals.type,
        sqlIndex: formVals.sqlIndex,
        statement: formVals.statement,
        note: formVals.note,
        enabled: formVals.enabled,
        taskId: formVals.taskId,
                }}
            >
                {renderContent(formVals)}
            </Form>
        </Modal>
    );
};

export default UpdateForm;

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


import React, {useEffect} from 'react';
import {Form, Space} from 'antd';
import {UserBaseInfo} from "@/types/User/data.d";
import {FormContextValue} from '@/components/Context/FormContext';
import {ProForm, ProFormText, ProFormTextArea} from "@ant-design/pro-components";
import {l} from "@/utils/intl";
import {FORM_LAYOUT_PUBLIC} from "@/services/constants";


type MenuCardProps = {
    onCancel: (flag?: boolean) => void;
    onSubmit: (values: Partial<UserBaseInfo.Role>) => void;
    modalVisible: boolean;
    values: Partial<UserBaseInfo.Role>;
    disabled?: boolean;
};

const MenuCard: React.FC<MenuCardProps> = (props) => {

    /**
     * init form
     */
    const [form] = Form.useForm();
    /**
     * init form context
     */
    const formContext = React.useMemo<FormContextValue>(() => ({
        resetForm: () => form.resetFields(), // 定义 resetForm 方法
    }), [form]);

    /**
     * init props
     */
    const {
        onSubmit: handleSubmit,
        onCancel: handleModalVisible,
        modalVisible,
        values,
        disabled=false,
    } = props;

    /**
     * when modalVisible or values changed, set form values
     */
    useEffect(() => {
        form.setFieldsValue(values);
    }, [modalVisible, values, form]);

    /**
     * handle cancel
     */
    const handleCancel = () => {
        handleModalVisible();
        formContext.resetForm();
    }
    /**
     * submit form
     */
    const submitForm = async () => {
        const fieldsValue = await form.validateFields();
        await handleSubmit({...values, ...fieldsValue});
        await handleCancel();
    };
    /**
     * construct role form
     * @constructor
     */
    const renderMenuForm = () => {
        return <>
            <ProFormText
                name="roleCode"
                label={l('role.roleCode')}
                placeholder={l('role.EnterRoleCode')}
                rules={[{required: true, message: l('role.EnterRoleCode')}]}
            />

            <ProFormText
                name="roleName"
                label={l('role.roleName')}
                placeholder={l('role.EnterRoleName')}
                rules={[{required: true, message: l('role.EnterRoleName')}]}
            />

            <ProFormTextArea
                name="note"
                label={l('global.table.note')}
                placeholder={l('role.EnterNote')}
                allowClear
            />
        </>
    };


    /**
     * render
     */
    return <>
        <ProForm
            disabled={disabled}
            {...FORM_LAYOUT_PUBLIC}
            form={form}
            initialValues={values}
            onReset={handleCancel}
            onFinish={() => submitForm()}
            submitter={{render: (_, dom) => <Space style={{display:'flex',justifyContent:'center'}}>{dom}</Space>,}}
            layout={'horizontal'}
        >
            {renderMenuForm()}
        </ProForm>
    </>

};
export default MenuCard;

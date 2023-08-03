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
import {ProForm, ProFormText, ProFormTextArea, ProFormTreeSelect} from "@ant-design/pro-components";
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
        if (!modalVisible){
            formContext.resetForm();
        }
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
            {
                values.id &&
                <ProFormText
                    name="id"
                    label={'ID'}
                    disabled
                />
            }
            {
                !modalVisible && <>
                <ProFormTreeSelect

                    name={'parentId'}
                    label={'父级菜单'}
                    rules={[{required: true, message: '父级菜单'}]}
                />
                </>
            }
            <ProFormText
                name="name"
                label={'菜单名称'}
                placeholder={'菜单名称'}
                rules={[{required: true, message: '菜单名称'}]}
            />
            <ProFormText
                name="component"
                label={'组件'}
                placeholder={'组件'}
                rules={[{required: true, message: '组件'}]}
            />
            <ProFormText
                name="path"
                label={'路由'}
                placeholder={'路由'}
                rules={[{required: true, message: '路由'}]}
            />

            <ProFormText
                name="perms"
                label={'权限标识'}
                placeholder={'权限标识'}
                rules={[{required: true, message: '权限标识'}]}
            />
            <ProFormText
                name="icon"
                label={'菜单图标'}
                placeholder={'菜单图标'}
                rules={[{required: true, message: '菜单图标'}]}
            />
            <ProFormText
                name="type"
                label={'菜单类型'}
                placeholder={'菜单类型'}
                rules={[{required: true, message: '菜单类型'}]}
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
            onReset={()=> formContext.resetForm()}
            onFinish={() => submitForm()}
            submitter={{
                render: (_, dom) => <Space style={{display:'flex',justifyContent:'center'}}>{dom}</Space>,
                resetButtonProps: {style: {display: 'none'},}
            }}
            layout={'horizontal'}
        >
            {renderMenuForm()}
        </ProForm>
    </>

};
export default MenuCard;

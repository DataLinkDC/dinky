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


import React, {useEffect, useState} from 'react';
import {Form, Space, TreeSelect} from 'antd';
import {UserBaseInfo} from "@/types/User/data.d";
import {FormContextValue} from '@/components/Context/FormContext';
import {Key, ProForm, ProFormRadio, ProFormText, ProFormTextArea, ProFormTreeSelect} from "@ant-design/pro-components";
import {l} from "@/utils/intl";
import {FORM_LAYOUT_PUBLIC} from "@/services/constants";
import {SysMenu} from "@/types/RegCenter/data";
import {buildMenuTree} from "@/pages/AuthCenter/Menu/function";


type MenuCardProps = {
    onCancel: (flag?: boolean) => void;
    onSubmit: (values: Partial<UserBaseInfo.Role>) => void;
    values: Partial<UserBaseInfo.Role>;
    open: boolean,
    disabled?: boolean;
    selectedKeys: Key[]
    treeData: SysMenu[];
    isRootMenu?: boolean
};

const MenuCard: React.FC<MenuCardProps> = (props) => {

    const [searchValue, setSearchValue] = useState('')
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
        values,
        open,
        disabled = false,
        isRootMenu = false,
        treeData,
        selectedKeys,
    } = props;

    /**
     * when modalVisible or values changed, set form values
     */
    useEffect(() => {
        if (open) form.resetFields();
        form.setFieldsValue(values);
    }, [open, values, form]);

    /**
     * handle cancel
     */
    const handleCancel = () => {
        handleModalVisible();
        formContext.resetForm();
    }

    /**
     * reset form data
     */
    const handleReset = () => {
        formContext.resetForm();
    }


    /**
     * submit form
     */
    const submitForm = async (formData: FormData) => {
        await form.validateFields();
        const parentId = isRootMenu ? 0 : form.getFieldValue('parentId');
        form.setFieldValue('parentId',parentId)
        await handleSubmit({...values, ...formData});
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
            <ProFormTreeSelect
                hidden={isRootMenu}
                initialValue={!isRootMenu ? selectedKeys : [0]}
                shouldUpdate
                name={'parentId'}
                label={'父级菜单'}
                rules={[{required: true, message: '父级菜单'}]}
                placeholder={'父级菜单'}
                fieldProps={{
                    showSearch: true,
                    treeData: buildMenuTree(treeData, searchValue),
                    treeCheckable: true,
                    onSearch: value => setSearchValue(value),
                    showCheckedStrategy: TreeSelect.SHOW_PARENT,
                }}
            />
            <ProFormText name="rootMenu" hidden/>
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
                fieldProps={{
                    addonAfter: <a type={'link'} target={'_blank'}
                                   href={'https://ant.design/components/icon-cn'}>菜单图标参考</a>,
                }}
            />

            <ProFormRadio.Group
                label="菜单类型"
                name={'type'}
                radioType="button"
                rules={[{required: true, message: '菜单类型'}]}
                // fieldProps={{
                //     value: formLayoutType,
                //     onChange: (e) => setFormLayoutType(e.target.value),
                // }}
                options={[
                    {
                        title: '菜单',
                        label: '菜单',
                        value: 0,
                    },
                    {
                        title: '按钮',
                        label: '按钮',
                        value: 1,
                    }
                ]}
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
            onReset={handleReset}
            onFinish={submitForm}
            submitter={{render: (_, dom) => <Space style={{display: 'flex', justifyContent: 'center'}}>{dom}</Space>,}}
            layout={'horizontal'}
        >
            {renderMenuForm()}
        </ProForm>
    </>

};
export default MenuCard;

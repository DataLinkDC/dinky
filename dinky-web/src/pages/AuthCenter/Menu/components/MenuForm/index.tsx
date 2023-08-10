/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */


import React, {useEffect, useState} from 'react';
import {Form, Space, TreeSelect} from 'antd';
import {FormContextValue} from '@/components/Context/FormContext';
import {
    Key,
    ProForm,
    ProFormRadio,
    ProFormSelect,
    ProFormText,
    ProFormTextArea,
    ProFormTreeSelect
} from "@ant-design/pro-components";
import {l} from "@/utils/intl";
import {FORM_LAYOUT_PUBLIC} from "@/services/constants";
import {SysMenu} from "@/types/RegCenter/data";
import {buildMenuFormTree} from "@/pages/AuthCenter/Menu/function";
import {
    MENU_TYPE_OPTIONS,
    MENU_ICON_OPTIONS
} from "@/pages/AuthCenter/Menu/components/MenuList/constants";


type MenuFormProps = {
    onCancel: (flag?: boolean) => void;
    onSubmit: (values: SysMenu) => void;
    values: Partial<SysMenu>;
    open: boolean,
    disabled?: boolean;
    selectedKeys: Key[]
    treeData: SysMenu[];
    isRootMenu?: boolean
};

const MenuForm: React.FC<MenuFormProps> = (props) => {

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
    const submitForm = async (formData: SysMenu) => {
        await form.validateFields();
        handleSubmit({...values, ...formData});
        handleCancel();
    };
    /**
     * construct role form
     * @constructor
     */
    const renderMenuForm = () => {
        return <>
            {
               <>
                    <ProFormTreeSelect
                        shouldUpdate
                        name={'parentId'}
                        label={l('menu.parentId')}
                        rules={[{required: true, message: l('menu.parentIdPlaceholder')}]}
                        placeholder={l('menu.parentIdPlaceholder')}
                        fieldProps={{
                            labelInValue: false,
                            value: selectedKeys,
                            treeData: buildMenuFormTree(treeData, searchValue,true),
                            onSearch: value => setSearchValue(value),
                            treeDefaultExpandAll: true,
                        }}
                    />
                </>
            }
            <ProFormText
                name="name"
                label={l('menu.name')}
                placeholder={l('menu.namePlaceholder')}
                rules={[{required: true, message: l('menu.namePlaceholder')}]}
            />
            <ProFormText
                name="component"
                label={l('menu.component')}
                placeholder={l('menu.componentPlaceholder')}
                rules={[{required: true, message: l('menu.componentPlaceholder')}]}
            />
            <ProFormText
                name="path"
                label={l('menu.path')}
                placeholder={l('menu.pathPlaceholder')}
                rules={[{required: true, message: l('menu.pathPlaceholder')}]}
            />

            <ProFormText
                name="perms"
                label={l('menu.perms')}
                placeholder={l('menu.permsPlaceholder')}
                rules={[{required: true, message: l('menu.permsPlaceholder')}]}
            />

            <ProFormSelect
                addonAfter={
                    <a key={'reference'} href={'https://ant.design/components/icon-cn'}>{l('menu.icon.reference')}</a>
                }
                cacheForSwr
                name="icon" allowClear showSearch
                mode={'single'} width={'md'}
                options={MENU_ICON_OPTIONS()}
                label={l('menu.icon')}
                placeholder={l('menu.iconPlaceholder')}
                rules={[{required: true, message: l('menu.iconPlaceholder')}]}
            />

            <ProFormRadio.Group
                label={l('menu.type')}
                name={'type'}
                radioType="button"
                rules={[{required: true, message: l('menu.typePlaceholder')}]}
                placeholder={l('menu.typePlaceholder')}
                options={MENU_TYPE_OPTIONS}
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
            initialValues={{...values}} // init form data
            onReset={handleReset}
            onFinish={submitForm}
            submitter={{render: (_, dom) => <Space style={{display: 'flex', justifyContent: 'center'}}>{dom}</Space>,}}
            layout={'horizontal'}
        >
            {renderMenuForm()}
        </ProForm>
    </>

};
export default MenuForm;

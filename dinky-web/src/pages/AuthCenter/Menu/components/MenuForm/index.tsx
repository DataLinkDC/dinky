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
import {UserBaseInfo} from "@/types/User/data.d";
import {FormContextValue} from '@/components/Context/FormContext';
import {Key, ProForm, ProFormRadio, ProFormText, ProFormTextArea, ProFormTreeSelect} from "@ant-design/pro-components";
import {l} from "@/utils/intl";
import {FORM_LAYOUT_PUBLIC} from "@/services/constants";
import {SysMenu} from "@/types/RegCenter/data";
import {buildMenuTree} from "@/pages/AuthCenter/Menu/function";
import {MENU_TYPE_OPTIONS} from "@/pages/AuthCenter/Menu/components/MenuList/constants";


type MenuFormProps = {
    onCancel: (flag?: boolean) => void;
    onSubmit: (values: Partial<UserBaseInfo.Role>) => void;
    values: Partial<UserBaseInfo.Role>;
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
    const submitForm = async (formData: FormData) => {
        await form.validateFields();
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
                label={l('menu.parentId')}
                rules={[{required: true, message: l('menu.parentIdPlaceholder')}]}
                placeholder={l('menu.parentIdPlaceholder')}
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
            <ProFormText
                name="icon"
                label={l('menu.icon')}
                placeholder={l('menu.iconPlaceholder')}
                rules={[{required: true, message: l('menu.iconPlaceholder')}]}
                fieldProps={{
                    addonAfter: <a type={'link'} target={'_blank'}
                                   href={'https://ant.design/components/icon-cn'}>{l('menu.icon.reference')}</a>,
                }}
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
export default MenuForm;

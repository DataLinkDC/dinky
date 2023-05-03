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
import {Form, Modal} from 'antd';
import {l} from "@/utils/intl";
import {RowPermissions, UserBaseInfo} from "@/types/User/data";
import {API_CONSTANTS, NORMAL_MODAL_OPTIONS} from "@/services/constants";
import PermissionsForm from "@/pages/AuthCenter/RowPermissions/components/PermissionsModal/PermissionsForm";
import {FormContextValue} from "@/components/Context/FormContext";
import {queryList} from "@/services/api";

/**
 * RoleSelectPermissionsFormProps
 */
type RoleSelectPermissionsFormProps = {
    onCancel: (flag?: boolean) => void;
    onSubmit: (values: Partial<RowPermissions>) => void;
    modalVisible: boolean;
    values: Partial<RowPermissions>;
};


export const PermissionsModal: React.FC<RoleSelectPermissionsFormProps> = (props) => {

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

    const [roleList, setRoleList] = useState<Partial<UserBaseInfo.Role>[]>([]);


    /**
     * init role list
     */
    useEffect(() => {
        queryList(API_CONSTANTS.ROLE).then((res) => {
            setRoleList(res.data);
        });
    }, []);

    /**
     * init props
     */
    const {
        onSubmit: handleSubmit,
        onCancel: handleModalVisible,
        modalVisible,
        values
    } = props;


    /**
     * when modalVisible or values changed, set form values
     */
    useEffect(() => {
        form.setFieldsValue(values);
    }, [modalVisible, values, form]);


    /**
     * cancel modal
     */
    const handleCancel = () => {
        handleModalVisible();
        formContext.resetForm();
    }


    /**
     * submit form
     * @returns {Promise<void>}
     */
    const submitForm = async () => {
        const fieldsValue = await form.validateFields();
        await handleSubmit({...values, ...fieldsValue});
        await handleCancel();
    };

    /**
     * render
     */
    return <>
        <Modal
            {...NORMAL_MODAL_OPTIONS}
            title={values.id ? l('rowPermissions.update') : l('rowPermissions.create')}
            open={modalVisible}
            onOk={() => submitForm()}
            onCancel={() => handleCancel()}
        >
            <PermissionsForm form={form} values={values} roles={roleList}/>
        </Modal>
    </>
};

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

import {ModalForm, ProFormSelect, ProFormText} from "@ant-design/pro-components";
import {l} from "@/utils/intl";
import {Form} from "antd";
import {Catalogue} from "@/types/Studio/data";
import React, {useEffect, useState} from "react";
import {FormContextValue} from "@/components/Context/FormContext";
import {JOB_TYPE} from "@/pages/DataStudio/LeftContainer/Project/constants";
import {isUDF} from "@/pages/DataStudio/LeftContainer/Project/function";
import {ProFormCascader} from "@ant-design/pro-form/lib";
import {queryDataByParams} from "@/services/BusinessCrud";
import {API_CONSTANTS} from "@/services/constants";

type JobModalProps = {
    onCancel: () => void
    onSubmit: (values: Catalogue) => void
    modalVisible: boolean
    title: React.ReactNode
    values: Partial<Catalogue>
}
const JobModal: React.FC<JobModalProps> = (props) => {

    const {onCancel, onSubmit, modalVisible, title, values} = props;
    const [jobType, setJobType] = React.useState<string>(values.type || '');
    const [form] = Form.useForm<Catalogue>();

    /**
     * init form context
     */
    const formContext = React.useMemo<FormContextValue>(
        () => ({
            resetForm: () => form.resetFields(), // 定义 resetForm 方法
        }),
        [form],
    );
    /**
     * when modalVisible or values changed, set form values
     */
    useEffect(() => {
        if (modalVisible) form.resetFields();
        form.setFieldsValue(values);
    }, [open, values, form]);


    /**
     * handle cancel
     */
    const handleCancel = () => {
        onCancel();
        formContext.resetForm();
    };

    /**
     * form values change
     * @param changedValues
     * @param allValues
     */
    const onValuesChange = (changedValues: any, allValues: any) => {
        if (allValues.type) setJobType(allValues.type);
    }

    /**
     * submit form
     */
    const submitForm = async (formData: Catalogue) => {
        await form.validateFields();
        if(!formData.configJson.templateId) return
        formData.configJson.templateId = formData.configJson.templateId.reverse().pop();
        onSubmit({...values, ...formData} as Catalogue);
    };

    const renderForm = () => {
        return <>
            <ProFormSelect
                name={'type'}
                label={l('catalog.type')}
                tooltip={l('catalog.type.tip')}
                options={JOB_TYPE}
                disabled={!!values.id}
                placeholder={l('catalog.type.placeholder')}
                rules={[{required: true, message: l('catalog.type.placeholder')}]}
            />
            <ProFormText
                name="name"
                label={l('catalog.name')}
                tooltip={l('catalog.name.tip')}
                placeholder={l('catalog.name.placeholder')}
                rules={[{required: true, message: l('catalog.name.placeholder')}]}
            />
            {
                isUDF(jobType) &&
                <>
                    <ProFormCascader
                        name={['configJson', 'templateId']}
                        label={l('catalog.udf.templateId')}
                        placeholder={l('catalog.udf.templateId.placeholder')}
                        request={async () => await queryDataByParams(API_CONSTANTS.UDF_TEMPLATE_TREE)}
                        rules={[{required: true, message: l('catalog.udf.templateId.placeholder')}]}
                    />

                    <ProFormText
                        name={['configJson', 'className']}
                        label={l('catalog.udf.className')}
                        placeholder={l('catalog.udf.className.placeholder')}
                        rules={[{required: true, message: l('catalog.udf.className.placeholder')}]}
                    />
                </>

            }
        </>
    };


    return <>
        <ModalForm<Catalogue>
            title={title}
            form={form}
            width={'30%'}
            initialValues={{...values}}
            open={modalVisible}
            layout={'horizontal'}
            autoFocusFirstInput onValuesChange={onValuesChange}
            modalProps={{destroyOnClose: true, maskClosable: false, onCancel: handleCancel}}
            onFinish={async (values) => submitForm(values)}
        >
            {renderForm()}
        </ModalForm>
    </>
};

export default JobModal

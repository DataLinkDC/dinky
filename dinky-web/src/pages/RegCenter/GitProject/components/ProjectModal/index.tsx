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

import React, {useEffect, useState} from "react";
import {GitProject} from "@/types/RegCenter/data";
import {l} from "@/utils/intl";
import {API_CONSTANTS, MODAL_FORM_STYLE} from "@/services/constants";
import {ModalForm} from "@ant-design/pro-components";
import ProjectForm from "@/pages/RegCenter/GitProject/components/ProjectModal/ProjectForm";
import {Button, Form, Space} from "antd";
import {FormContextValue} from "@/components/Context/FormContext";
import {getDataByParams, handleData} from "@/services/BusinessCrud";

type ProjectModalProps = {
    modalVisible: boolean;
    values: Partial<GitProject>;
    onSubmit: (values: Partial<GitProject>) => void;
    onCancel: () => void;
}
const ProjectModal: React.FC<ProjectModalProps> = (props) => {

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
        values
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


    const renderFooter = () => {
        return [
            <Button key={"cancel"} onClick={() => handleCancel()}>{l("button.cancel")}</Button>,
            <Button key={"finish"} type="primary" onClick={() => submitForm()}>{l("button.finish")}</Button>,
        ];
    };

    return <>
        <ModalForm<GitProject>
            {...MODAL_FORM_STYLE}
            title={values.id ? l("rc.gp.modify") : l("rc.gp.create")}
            open={modalVisible}
            form={form}
            submitter={{render: () => [...renderFooter()]}}
        >
            <ProjectForm values={values} form={form}/>
        </ModalForm>
    </>
}
export default ProjectModal;
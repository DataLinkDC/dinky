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


import React, {useEffect} from "react";
import {Form, Modal} from "antd";
import {FormContextValue} from "@/components/Context/FormContext";
import {NORMAL_MODAL_OPTIONS} from "@/services/constants";
import {l} from "@/utils/intl";
import {Document} from "@/types/RegCenter/data";
import DocumentForm from "@/pages/RegCenter/Document/components/DocumentModal/DocumentForm";

type DocumentModalProps = {
    onCancel: (flag?: boolean) => void;
    values: Partial<Document>;
    modalVisible: boolean;
    onSubmit: (values: Partial<Document>) => void;
}
const DocumentModalForm: React.FC<DocumentModalProps> = (props) => {
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
         handleSubmit({...values, ...fieldsValue});
         handleCancel();
    };

    return <>
        <Modal
            {...NORMAL_MODAL_OPTIONS}
            title={values.id ? l('rc.doc.modify') : l('rc.doc.create')}
            open={modalVisible}
            onOk={() => submitForm()}
            onCancel={() => handleModalVisible()}
        >
            <DocumentForm values={values} form={form}/>
        </Modal>
    </>
}

export default DocumentModalForm;

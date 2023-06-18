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

import {Cluster} from '@/types/RegCenter/data';
import {Button, Form} from 'antd';
import React, {useEffect} from 'react';
import {ModalForm} from '@ant-design/pro-components';
import {l} from '@/utils/intl';
import {FormContextValue} from '@/components/Context/FormContext';
import ConfigurationForm from "@/pages/RegCenter/Cluster/Configuration/components/ConfigurationModal/ConfigurationForm";
import {buildClusterConfig, parseConfigJsonToValues} from "@/pages/RegCenter/Cluster/Configuration/components/function";
import {ClusterType} from "@/pages/RegCenter/Cluster/constants";

type ConfigurationModalProps = {
    visible: boolean;
    onClose: () => void;
    value: Partial<Cluster.Config>;
    onSubmit: (values: Partial<Cluster.Config>) => void;
}
const InstanceModal: React.FC<ConfigurationModalProps> = (props) => {

    const {visible, onClose, onSubmit, value} = props;




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

    const [submitting, setSubmitting] = React.useState<boolean>(false);


    /**
     * when modalVisible or values changed, set form values
     */
    useEffect(() => {
        form.setFieldsValue(parseConfigJsonToValues(value as Cluster.Config));
    }, [visible, value, form]);

    /**
     * handle cancel
     */
    const handleCancel = () => {
        onClose();
        formContext.resetForm();
        setSubmitting(false);
    };
    /**
     * submit form
     */
    const submitForm = async () => {
        const fieldsValue = await form.validateFields();
        setSubmitting(true);
        await onSubmit({...value, ...buildClusterConfig(fieldsValue)});
        handleCancel();
    };


    /**
     * render footer
     * @returns {[JSX.Element, JSX.Element]}
     */
    const renderFooter = () => {
        return [
            <Button key={'cancel'} onClick={() => handleCancel()}>{l('button.cancel')}</Button>,
            <Button key={'finish'} loading={submitting} type="primary"
                    onClick={() => submitForm()}>{l('button.finish')}</Button>,
        ];
    };


    return <>
        <ModalForm
            width={'80%'}
            open={visible}
            modalProps={{
                onCancel: handleCancel,
                bodyStyle: {maxHeight: '70vh', overflowY: 'auto', overflowX: 'hidden'},
            }}
            title={value.id ? l('rc.cc.modify') : l('rc.cc.create')}
            submitter={{render: () => [...renderFooter()]}}
            initialValues={parseConfigJsonToValues(value as Cluster.Config)}
            form={form}
        >
            <ConfigurationForm form={form} value={parseConfigJsonToValues(value as Cluster.Config)}/>
        </ModalForm>
    </>;
};

export default InstanceModal;

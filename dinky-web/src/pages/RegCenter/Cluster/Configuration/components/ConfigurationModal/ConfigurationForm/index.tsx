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

import React from 'react';
import {FormInstance} from 'antd/es/form/hooks/useForm';
import {Values} from 'async-validator';
import {Cluster} from '@/types/RegCenter/data';
import {
    ProForm,
    ProFormGroup,
    ProFormSelect,
    ProFormText,
} from '@ant-design/pro-components';
import {l} from '@/utils/intl';
import {Divider} from 'antd';
import {CLUSTER_CONFIG_TYPE} from "@/pages/RegCenter/Cluster/Configuration/components/contants";
import {RUN_MODE} from "@/services/constants";
import HadoopConfig
    from "@/pages/RegCenter/Cluster/Configuration/components/ConfigurationModal/ConfigurationForm/HadoopConfig";
import FlinkKubernetesNative
    from "@/pages/RegCenter/Cluster/Configuration/components/ConfigurationModal/ConfigurationForm/FlinkKubernetesNative";
import FlinkKubernetesOperator
    from "@/pages/RegCenter/Cluster/Configuration/components/ConfigurationModal/ConfigurationForm/FlinkKubernetesOperator";
import BaseConfig
    from "@/pages/RegCenter/Cluster/Configuration/components/ConfigurationModal/ConfigurationForm/BaseConfig";
import ApplicationConfig
    from "@/pages/RegCenter/Cluster/Configuration/components/ConfigurationModal/ConfigurationForm/ApplicationConfig";
import HighPriorityConfig
    from "@/pages/RegCenter/Cluster/Configuration/components/ConfigurationModal/ConfigurationForm/HighPriorityConfig";


type ConfigurationFormProps = {
    form: FormInstance<Values>
    value: Partial<Cluster.Config>;
}
const ConfigurationForm: React.FC<ConfigurationFormProps> = (props) => {
    const {form, value} = props;

    const [type, setType] = React.useState<string>(value.type || 'yarn');


    const renderAllForm = () => {
        return <>
            <BaseConfig/>
            {type === RUN_MODE.YARN && <HadoopConfig/>}
            {type === RUN_MODE.KUBERNETES_APPLICATION && <FlinkKubernetesNative/>}
            {type === RUN_MODE.KUBERNETES_APPLICATION_OPERATOR && <FlinkKubernetesOperator/>}
            <HighPriorityConfig/>
            <ApplicationConfig/>
        </>;
    };

    const onTypeChange = (changedValues: any) => {
        if (changedValues.type) setType(changedValues.type)
    }

    return <>
        <ProForm
            form={form}
            initialValues={value}
            submitter={false}
            onValuesChange={onTypeChange}
        >
            {renderAllForm()}
        </ProForm>
    </>;


};

export default ConfigurationForm;

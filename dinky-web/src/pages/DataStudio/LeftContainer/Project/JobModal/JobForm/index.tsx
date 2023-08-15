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

import {ProFormSelect, ProFormText} from "@ant-design/pro-components";
import {JOB_TYPE} from "@/pages/DataStudio/LeftContainer/Project/constants";

const JobForm = () => {

    return <>
        <ProFormSelect
            name={'type'}
            label="作业类型"
            options={JOB_TYPE}
            placeholder="请选择作业类型"
            rules={[{required: true , message: '请选择作业类型'}]}
        />
        <ProFormText
            name="name"
            label="作业名称"
            placeholder="请输入作业名称"
            rules={[{required: true , message: '请输入作业名称'}]}
        />
    </>
};

export default JobForm;
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

import {RowPermissions, UserBaseInfo} from "@/types/User/data";
import {Values} from "async-validator";
import {FormInstance} from "antd/es/form/hooks/useForm";
import React, {useState} from "react";
import {l} from "@/utils/intl";
import {ProForm, ProFormItem, ProFormSelect, ProFormText} from "@ant-design/pro-components";
import CodeEdit from "@/components/CustomEditor/CodeEdit";
import {FORM_LAYOUT_PUBLIC} from "@/services/constants";
import {DefaultOptionType} from "rc-select/lib/Select";

/**
 * PermissionsFormProps
 */
type PermissionsFormProps = {
    values: Partial<RowPermissions>;
    form: FormInstance<Values>;
    roles: Partial<UserBaseInfo.Role>[]
}

/**
 * CodeEditProps
 * @type {{lineNumbers: string, height: string}}
 */
const CodeEditProps = {
    height: "25vh",
    lineNumbers: "off",
}

const PermissionsForm: React.FC<PermissionsFormProps> = (props) => {

    const {values,roles, form} = props;
    const [expression, setExpression] = useState<string>(values.expression || '');

    /**
     * get role options
     * @returns {DefaultOptionType[]}
     */
    const getRoleOptions = () => {
        const itemList: DefaultOptionType []  = [];
        roles.map((item) => {
           return itemList.push({
                label: item.roleName,
                value: item.id,
            })
        })
        return itemList;
    };

    /**
     * render row permissions form
     * @returns {JSX.Element}
     */
    const renderRowPermissionsForm = () => {
        return <>
            <ProFormSelect
                name="roleId"
                label={l('rowPermissions.roleName')}
                rules={[{required: true, message: l('rowPermissions.roleNamePlaceholder')}]}
                options={getRoleOptions()}
            />

            <ProFormText
                name="tableName"
                label={l('rowPermissions.tableName')}
                rules={[{required: true, message: l('rowPermissions.tableNamePlaceholder')}]}
            />

            <ProFormItem
                name="expression"
                label={l('rowPermissions.expression')}
                rules={[{required: true, message: l('rowPermissions.expressionPlaceholder')}]}
            >
                <CodeEdit
                    onChange={(value => {
                        setExpression(value)
                    })}
                    code={expression}
                    language={"sql"}
                    {...CodeEditProps}
                />
            </ProFormItem>
        </>
    };


    /**
     * render
     */
    return <>
        <ProForm
            {...FORM_LAYOUT_PUBLIC}
            form={form}
            submitter={false}
            layout={'horizontal'}
            initialValues={values}
        >
            {renderRowPermissionsForm()}
        </ProForm>
    </>
};

export default PermissionsForm;
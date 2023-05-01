/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */


import {l} from "@/utils/intl";
import {ProForm, ProFormText} from '@ant-design/pro-components';
import {FORM_LAYOUT_PUBLIC} from "@/services/constants";
import {UserBaseInfo} from "@/types/User/data";
import React from "react";
import {FormInstance} from "antd/es/form/hooks/useForm";
import {Values} from "async-validator";

type PasswordFormProps = {
    values: Partial<UserBaseInfo.ChangePasswordParams>;
    form: FormInstance<Values>
};


const PasswordModal: React.FC<PasswordFormProps> = (props) => {

    /**
     * init props
     */
    const {values, form} = props;

    /**
     * render changePassword form
     */
    const pwdFormRender = () => {
        return <>
            <ProFormText.Password
                width="md"
                name="password"
                hasFeedback
                label={l('user.oldpwd')}
                placeholder={l('user.oldpwdPlaceholder')}
                rules={[{required: true, message: l('user.oldpwdPlaceholder')}]}
            />
            <ProFormText.Password
                width="md"
                name="newPassword"
                hasFeedback
                label={l('user.newpwd')}
                placeholder={l('user.newpwdPlaceholder')}
                rules={[{required: true, message: l('user.newpwdPlaceholder')}]}
            />
            <ProFormText.Password
                width="md"
                name="newPasswordCheck"
                hasFeedback
                dependencies={['newPassword']}
                label={l('user.repeatpwd')}
                placeholder={l('user.repeatpwdPlaceholder')}
                rules={[
                    {
                        required: true,
                        message: l('user.oldNewPwdNoMatch'),
                    },
                    ({getFieldValue}) => ({
                        validator(_, value) {
                            if (!value || getFieldValue('newPassword') === value) {
                                return Promise.resolve();
                            }
                            return Promise.reject(new Error(l('user.oldNewPwdNoMatch')));
                        },
                    }),
                ]}

            />
        </>
    };

    /**
     * render
     */
    return <>
        <ProForm
            {...FORM_LAYOUT_PUBLIC}
            form={form}
            initialValues={values}
            layout={"horizontal"}
            submitter={false}
        >
            {pwdFormRender()}
        </ProForm>
    </>
};

export default PasswordModal;

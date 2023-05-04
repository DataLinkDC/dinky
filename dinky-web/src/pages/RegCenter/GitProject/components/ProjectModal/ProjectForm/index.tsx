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
import {Button, Form, Radio, Select} from "antd";
import {Document, GitProject} from "@/types/RegCenter/data";
import {getDataByParams, handleData} from "@/services/BusinessCrud";
import {API_CONSTANTS, FORM_LAYOUT_PUBLIC} from "@/services/constants";
import {buildFormData} from "@/pages/RegCenter/Alert/AlertGroup/function";
import {CLONE_TYPES, GIT_PROJECT_CODE_TYPE_ENUM} from "@/pages/RegCenter/GitProject/constans";
import {
    ProForm,
    ProFormRadio,
    ProFormSelect,
    ProFormText,
    ProFormTextArea,
    RequestOptionsType
} from "@ant-design/pro-components";
import {l} from "@/utils/intl";
import CodeEdit from "@/components/CustomEditor/CodeEdit";
import {FormInstance} from "antd/es/form/hooks/useForm";
import {Values} from "async-validator";
import {DefaultOptionType} from "rc-select/lib/Select";


/**
 * props
 */
type ProjectFormProps = {
    values: Partial<GitProject>;
    form: FormInstance<Values>
}

/**
 * code edit props
 */
const CodeEditProps = {
    height: "15vh",
    width: "20vw",
    lineNumbers: "off",
};


const ProjectForm: React.FC<ProjectFormProps> = (props) => {

    const {values, form} = props;

    /**
     * state
     */

    const [cloneType, setCloneType] = useState<string>("http");
    const [buildArgs, setBuildArgsValue] = useState<string>(values.buildArgs || "");
    const [branches, setBranches] = useState<string[]>([]);

    const queryBranches = async (gitProject: Partial<GitProject>) => {
        await getDataByParams(API_CONSTANTS.GIT_BRANCH, {...gitProject}).then((data) => {
            setBranches(data || []);
        });
    }


    useEffect(() => {
        // queryBranches(form.getFieldsValue());
    }, [form.getFieldsValue()])


    /**
     * build git project branches select options
     */
    const buildBranchesSelectOptions = () => {
        const tagSelectList: { label: string; value: string }[] = [];
        branches.forEach((branch) => {
            tagSelectList.push({label: branch, value: branch});
        });
        return tagSelectList;
    }

    /**
     * render url before select
     */
    const renderUrlBeforeSelect = () => {
        return (
            <Select
                style={{width: "6vw"}}
                defaultValue={cloneType}
                onChange={(value) => setCloneType(value)}
                options={CLONE_TYPES}
            />
        );
    };


    /**
     * render form
     */
    const renderGitProjectForm = () => {
        return <>
            <ProForm.Group>
                <ProFormText
                    name="name"
                    width={"sm"}
                    label={l("rc.gp.name")}
                    rules={[{required: true, message: l("rc.gp.namePlaceholder")}]}
                    placeholder={l("rc.gp.namePlaceholder")}
                />

                <ProFormText
                    name="url"
                    width={"xl"}
                    label={l("rc.gp.url")}
                    rules={[{required: true, message: l("rc.gp.urlPlaceholder")}]}
                    placeholder={l("rc.gp.urlPlaceholder")}
                    fieldProps={{addonBefore: renderUrlBeforeSelect()}}
                />
            </ProForm.Group>

            <ProForm.Group>
                {
                    cloneType !== "ssh" ? (
                        <ProFormText
                            name="username"
                            allowClear
                            width={"sm"}
                            label={l("rc.gp.username")}
                            rules={[{required: true, message: l("rc.gp.usernamePlaceholder")}]}
                            placeholder={l("rc.gp.usernamePlaceholder")}
                        />
                    ) : (
                        <ProFormText
                            name="privateKey"
                            width={"sm"}
                            label={l("rc.gp.privateKey")}
                            rules={[{required: true, message: l("rc.gp.privateKeyPlaceholder")}]}
                            placeholder={l("rc.gp.privateKeyPlaceholder")}
                        />
                    )
                }

                <ProFormText.Password
                    name="password"
                    allowClear
                    width={"sm"}
                    label={l("rc.gp.password")}
                    rules={[{required: true, message: l("rc.gp.passwordPlaceholder")}]}
                    placeholder={l("rc.gp.passwordPlaceholder")}
                />
                <ProFormSelect
                    request={async () => {
                        let branch: RequestOptionsType[] = []
                        console.log(branch,'进来了')
                        await getDataByParams(API_CONSTANTS.GIT_BRANCH, {...form.getFieldsValue()}).then((data) => {
                            setBranches(data || []);
                            data?.forEach((item: string) => {
                                branch.push({label: item, value: item})
                            })
                        });

                        return branch
                    }}
                    name="branches"
                    width={"sm"}
                    label={l("rc.gp.branches")}
                    placeholder={l("rc.gp.branchesPlaceholder")}
                    rules={[{required: true, message: l("rc.gp.branchesPlaceholder")}]}
                    mode="single"
                    showSearch
                    shouldUpdate
                    colon
                    dependencies={["url", "username" || "privateKey", "password"]}
                    // options={buildBranchesSelectOptions()}
                />
            </ProForm.Group>


            <ProForm.Group>
                <ProForm.Item
                    name="buildArgs"
                    label={l("rc.gp.buildArgs")}
                >
                    <CodeEdit
                        onChange={(value) => setBuildArgsValue(value)}
                        code={buildArgs}
                        language={"shell"}
                        {...CodeEditProps}
                    />
                </ProForm.Item>

                <ProFormRadio.Group
                    name="codeType"
                    width={"xs"}
                    label={l("rc.gp.codeType")}
                    rules={[{required: true, message: l("rc.gp.codeTypePlaceholder")}]}

                >
                    <Radio.Group>
                        <Radio value={1}>{GIT_PROJECT_CODE_TYPE_ENUM[1].text}</Radio>
                        <Radio value={2}>{GIT_PROJECT_CODE_TYPE_ENUM[2].text}</Radio>
                    </Radio.Group>
                </ProFormRadio.Group>

            </ProForm.Group>

            <ProForm.Group>
                <ProFormTextArea
                    name="pom"
                    width={"md"}
                    label={l("rc.gp.pom")}
                    placeholder={l("rc.gp.pomPlaceholder")}
                />
                <ProFormTextArea
                    name="description"
                    width={"md"}
                    label={l("global.table.note")}
                    placeholder={l("global.table.notePlaceholder")}
                />
            </ProForm.Group>
        </>
    };

    return <>
        <ProForm
            form={form}
            submitter={false}
            initialValues={values}
        >
            {renderGitProjectForm()}
        </ProForm>
    </>;
}
export default ProjectForm;
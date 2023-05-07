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

import React, {useState} from "react";
import {Input, Radio, Select} from "antd";
import {GitProject} from "@/types/RegCenter/data";
import {getDataByParams} from "@/services/BusinessCrud";
import {API_CONSTANTS, SWITCH_OPTIONS} from "@/services/constants";
import {CLONE_TYPES, GIT_PROJECT_CODE_TYPE_ENUM} from "@/pages/RegCenter/GitProject/constans";
import {
  ProForm, ProFormRadio,
  ProFormSelect, ProFormSwitch,
  ProFormText,
  ProFormTextArea,
} from "@ant-design/pro-components";
import {l} from "@/utils/intl";
import CodeEdit from "@/components/CustomEditor/CodeEdit";
import {FormInstance} from "antd/es/form/hooks/useForm";
import {Values} from "async-validator";


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
  language: "shell",
};


const ProjectForm: React.FC<ProjectFormProps> = (props) => {

  const {values, form} = props;

  /**
   * state
   */
  const [cloneType, setCloneType] = useState<number>(values.type || 1);
  const [buildArgs, setBuildArgsValue] = useState<string>(values.buildArgs || "");
  const [branches, setBranches] = useState<string[]>([]);


  /**
   * get branch list
   */
  const getBranchList = async () => {
    const values = form.getFieldsValue();
    await getDataByParams(API_CONSTANTS.GIT_BRANCH, {...values}).then((result: any) => {
      setBranches(result);
    });
  };


  /**
   * handle type change
   * @param value
   */
  const handleTypeChange = (value: number) => {
    setCloneType(value);
    form.setFieldsValue({type: value, url: ""});
  };


  /**
   * render url before select
   */
  const renderUrlBeforeSelect = () => {
    return <Select
      style={{width: "5vw"}}
      defaultValue={cloneType}
      onChange={handleTypeChange}
      options={CLONE_TYPES}
    />;
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
        <ProFormSelect
          name="type"
          hidden
          shouldUpdate
          initialValue={cloneType}
        />

        <ProFormText
          name="url"
          width={"xs"}
          label={l("rc.gp.url")}
          rules={[{required: true, message: l("rc.gp.urlPlaceholder")}]}
          addonBefore={renderUrlBeforeSelect()}
        >
          <Input style={{
            width: "18vw",
          }} addonBefore={undefined} onBlur={cloneType === 1 ? getBranchList : (() => Promise<void>)}
                 placeholder={l("rc.gp.urlPlaceholder")}/>
        </ProFormText>
      </ProForm.Group>

      <ProForm.Group>
        {
          cloneType !== 2 ?
            <ProFormText
              name="username"
              allowClear
              width={"sm"}
              label={l("rc.gp.username")}
              placeholder={l("rc.gp.usernamePlaceholder")}
            />
            :
            <ProFormText
              name="privateKey"
              width={"xl"}
              tooltip={l("rc.gp.privateKeyPlaceholder")}
              label={l("rc.gp.privateKey")}
              rules={[{required: true, message: l("rc.gp.privateKeyPlaceholder")}]}
              placeholder={l("rc.gp.privateKeyPlaceholder")}
            >
              <Input style={{
                width: "12vw",
              }} onBlur={cloneType === 2 ? getBranchList : (() => Promise<void>)}
                     placeholder={l("rc.gp.privateKeyPlaceholder")}/>
            </ProFormText>
        }

        <ProFormText.Password
          name="password"
          label={l("rc.gp.password")}
          placeholder={l("rc.gp.passwordPlaceholder")}
        />

        <ProFormSelect
          options={branches}
          shouldUpdate
          disabled={cloneType === 1 ? !form.getFieldsValue().url : (!form.getFieldsValue().privateKey && !form.getFieldsValue().url)}
          colon={true}
          name="branch"
          width={"sm"}
          label={l("rc.gp.branch")}
          placeholder={l("rc.gp.branchPlaceholder")}
          rules={[{required: true, message: l("rc.gp.branchPlaceholder")}]}
          showSearch
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

            {...CodeEditProps}
          />
        </ProForm.Item>

        <ProFormSwitch
          width="xs"
          name="enabled"
          label={l("global.table.isEnable")}
          {...SWITCH_OPTIONS()}
        />
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
    </>;
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
};
export default ProjectForm;

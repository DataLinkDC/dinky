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

import {Form, InputNumber, Space, Switch} from "antd";
import {InfoCircleOutlined} from "@ant-design/icons";
import {l} from "@/utils/intl";
import {connect} from "umi";
import {StateType} from "@/pages/DataStudio/model";
import {useForm} from "antd/es/form/Form";
import {getCurrentData} from "@/pages/DataStudio/function";
import {SWITCH_OPTIONS} from "@/services/constants";

const ExecuteConfig = (props: any) => {


  const { dispatch, tabs:{panes, activeKey}} = props;
  const [form] = useForm();
  const current = getCurrentData(panes, activeKey);

  form.setFieldsValue(current);
  const onValuesChange = (change: any, all: any) => {
    for (let i = 0; i < panes.length; i++) {
      if (panes[i].key === activeKey) {
        for (const key in change) {
          panes[i].params.taskData[key] = all[key];
        }
        break;
      }
    }
    dispatch({
      type: "Studio/saveTabs",
      payload: {...props.tabs},
    });
  };

  return (
    <>
        <Form
          initialValues={{
            maxRowNum:"100"
          }}
          style={{padding: "10px"}}
          form={form}
          layout="vertical"
          onValuesChange={onValuesChange}
        >
          <Space size={[50, 16]} wrap>
                <Form.Item
                  label={l('pages.datastudio.label.execConfig.preview.result')}  name="useResult" valuePropName="checked"
                  tooltip={{title: l('pages.datastudio.label.execConfig.preview.result.tip'), icon: <InfoCircleOutlined/>}}
                >
                  <Switch defaultChecked {...SWITCH_OPTIONS()}/>
                </Form.Item>
                <Form.Item
                  label={l('pages.datastudio.label.execConfig.changelog')} name="useChangeLog" valuePropName="checked"
                  tooltip={{
                    title: l('pages.datastudio.label.execConfig.changelog.tip'),
                    icon: <InfoCircleOutlined/>
                  }}
                >
                  <Switch  {...SWITCH_OPTIONS()}/>
                </Form.Item>
                <Form.Item
                  label={l('pages.datastudio.label.execConfig.maxrow')}  name="maxRowNum"
                  tooltip={l('pages.datastudio.label.execConfig.maxrow.tip')}
                >
                  <InputNumber min={1} max={9999}/>
                </Form.Item>
                <Form.Item
                  label={l('pages.datastudio.label.execConfig.autostop')} name="useAutoCancel" valuePropName="checked"
                  tooltip={{title: l('pages.datastudio.label.execConfig.autostop.tip'), icon: <InfoCircleOutlined/>}}
                >
                  <Switch {...SWITCH_OPTIONS()}/>
                </Form.Item>
          </Space>
        </Form>
    </>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  tabs: Studio.tabs,
}))(ExecuteConfig);

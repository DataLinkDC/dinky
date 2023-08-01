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

import {Col, Form, InputNumber, Row, Space, Switch} from "antd";
import {InfoCircleOutlined} from "@ant-design/icons";
import {l} from "@/utils/intl";
import {connect} from "umi";
import {StateType} from "@/pages/DataStudio/model";
import {useForm} from "antd/es/form/Form";
import {getCurrentData} from "@/pages/DataStudio/function";

const ExecuteConfig = (props: any) => {


  const { dispatch, tabs:{panes, activeKey}} = props;
  const [form] = useForm();
  const current = getCurrentData(panes, activeKey);

  form.setFieldsValue(current);


  const onValuesChange = (change: any, all: any) => {
    // let newTabs = tabs;
    // for (let i = 0; i < newTabs.panes.length; i++) {
    //   if (newTabs.panes[i].key == newTabs.activeKey) {
    //     for (let key in change) {
    //       newTabs.panes[i].task[key] = change[key];
    //     }
    //     break;
    //   }
    // }
    //
    // dispatch && dispatch({
    //   type: "Studio/saveTabs",
    //   payload: newTabs,
    // });
  };

  return (
    <>
        <Form
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
                  <Switch defaultChecked checkedChildren={l('button.enable')} unCheckedChildren={l('button.disable')}
                  />
                </Form.Item>
                <Form.Item
                  label={l('pages.datastudio.label.execConfig.changelog')} name="useChangeLog" valuePropName="checked"
                  tooltip={{
                    title: l('pages.datastudio.label.execConfig.changelog.tip'),
                    icon: <InfoCircleOutlined/>
                  }}
                >
                  <Switch  checkedChildren={l('button.enable')} unCheckedChildren={l('button.disable')}
                  />
                </Form.Item>
                <Form.Item
                  label={l('pages.datastudio.label.execConfig.maxrow')}  name="maxRowNum"
                  tooltip={l('pages.datastudio.label.execConfig.maxrow.tip')}
                >
                  <InputNumber min={1} max={9999} defaultValue={100}/>
                </Form.Item>
                <Form.Item
                  label={l('pages.datastudio.label.execConfig.autostop')} name="useAutoCancel" valuePropName="checked"
                  tooltip={{title: l('pages.datastudio.label.execConfig.autostop.tip'), icon: <InfoCircleOutlined/>}}
                >
                  <Switch checkedChildren={l('button.enable')} unCheckedChildren={l('button.disable')}
                  />
                </Form.Item>
          </Space>
        </Form>
    </>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  tabs: Studio.tabs,
}))(ExecuteConfig);

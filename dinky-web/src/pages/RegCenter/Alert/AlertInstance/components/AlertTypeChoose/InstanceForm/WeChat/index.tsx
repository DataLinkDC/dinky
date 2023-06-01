/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import React from "react";
import {Radio} from "antd";
import {l} from "@/utils/intl";
import {
  ProForm, ProFormDigit,
  ProFormRadio,
  ProFormSwitch,
  ProFormText,
  ProFormTextArea
} from "@ant-design/pro-components";
import {SWITCH_OPTIONS} from "@/services/constants";


const WeChat = (props:any) => {
  const {values} = props;
  /**
   * render
   */
  return  <>
    <ProForm.Group>
      <ProFormRadio.Group
        name="msgtype"
        label={l("rc.ai.msgtype")}
        rules={[{required: true, message: l("rc.ai.msgtypePleaseHolder")}]}
      >
        <Radio.Group>
          <Radio value="markdown">{l("rc.ai.markdown")}</Radio>
          <Radio value="text">{l("rc.ai.text")}</Radio>
        </Radio.Group>
      </ProFormRadio.Group>

      <ProFormRadio.Group
        name="sendType"
        label={l("rc.ai.sendType")}
        rules={[{required: true, message: l("rc.ai.sendTypePleaseHolder")}]}
      >
        <Radio.Group>
          <Radio value="app">{l("rc.ai.sendType.app")}</Radio>
          <Radio value="wechat">{l("rc.ai.sendType.wechat")}</Radio>
        </Radio.Group>
      </ProFormRadio.Group>

      <ProFormSwitch
        name="enabled"
        label={l("global.table.isEnable")}
        {...SWITCH_OPTIONS()}
      />
      {(values.sendType === "wechat") &&
        // if sendType is wechat render this switch group
        <ProFormSwitch
          name="isAtAll"
          label={l("rc.ai.isAtAll")}
          {...SWITCH_OPTIONS()}
        />
      }
    </ProForm.Group>

    <ProForm.Group>
      {(values.sendType === "wechat") ?
        // if sendType is wechat
        <>
          <ProFormTextArea
            width="xl"
            allowClear
            name="webhook"
            label={l("rc.ai.webhook")}
            rules={[{required: true, message: l("rc.ai.webhookPleaseHolder")}]}
            placeholder={l("rc.ai.webhookPleaseHolder")}
          />
          <ProFormTextArea
            width="sm"
            name="keyword"
            label={l("rc.ai.keyword")}
            placeholder={l("rc.ai.keywordPleaseHolder")}
          />

          {/* if not Enable At All this group do render */}
          {!values.isAtAll &&
            <>
              <ProFormTextArea
                width="xl"
                name="users"
                label={l("rc.ai.atUsers")}
                rules={[{required: true, message: l("rc.ai.wechatAtUsersPleaseHolder")}]}
                placeholder={l("rc.ai.wechatAtUsersPleaseHolder")}
              />
            </>
          }
        </>:
        (values.sendType === "app") &&
        // if sendType is app
        <>
          <ProFormText
            width="md"
            name="corpId"
            label={l("rc.ai.corpId")}
            rules={[{required: true, message: l("rc.ai.corpIdPleaseHolder")}]}
            placeholder={l("rc.ai.corpIdPleaseHolder")}
          />
          <ProFormText.Password
            width="lg"
            name="secret"
            label={l("rc.ai.secret")}
            rules={[{required: true, message: l("rc.ai.secretPleaseHolder")}]}
            placeholder={l("rc.ai.secretPleaseHolder")}
          />
          <ProFormDigit
            width="md"
            name="agentId"
            label={l("rc.ai.agentId")}
            rules={[{required: true, message: l("rc.ai.agentIdPleaseHolder")}]}
            placeholder={l("rc.ai.agentIdPleaseHolder")}
          />
          <ProFormText
            width="lg"
            name="users"
            label={l("rc.ai.user")}
            rules={[{required: true, message: l("rc.ai.userPleaseHolder")}]}
            placeholder={l("rc.ai.userPleaseHolder")}
          />
        </>
      }
    </ProForm.Group>
  </>;
};

export default WeChat;

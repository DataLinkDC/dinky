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

import React, {useEffect, useState} from 'react';
import {PageContainer} from "@ant-design/pro-layout";
import {Button, Empty, Tabs} from "antd";
import CodeShow from "@/components/Common/CodeShow";
import {getRootLog} from "@/pages/SettingCenter/SystemInfo/service";
import {RedoOutlined} from "@ant-design/icons";
import {l} from "@/utils/intl";

const {TabPane} = Tabs;

const SystemInfo = (props: any) => {

  const [log, setLog] = useState<string>("Nothing.");

  useEffect(() => {
    refreshRootLog();
  }, []);

  const refreshRootLog = () => {
    const res = getRootLog();
    res.then((result) => {
      result.datas && setLog(result.datas);
    });
  }

  return (
    <PageContainer title={false}>
      <Tabs defaultActiveKey="metrics" size="small" tabPosition="top"
            style={{
              border: "1px solid #f0f0f0",
              backgroundColor: '#fff',
            }}
            tabBarExtraContent={<Button
              icon={<RedoOutlined/>}
              onClick={refreshRootLog}
            ></Button>}>
        <TabPane tab={<span>&nbsp; Metrics &nbsp;</span>} key="metrics">
          <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description={l('global.stay.tuned')}/>
        </TabPane>
        <TabPane tab={<span>&nbsp; Configuration &nbsp;</span>} key="configuration">
          <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description={l('global.stay.tuned')}/>
        </TabPane>
        <TabPane tab={<span>&nbsp; Logs &nbsp;</span>} key="logs">
          <CodeShow code={log} language='java' height='70vh'/>
        </TabPane>
        <TabPane tab={<span>&nbsp; Log List &nbsp;</span>} key="logList">
          <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description={l('global.stay.tuned')}/>
        </TabPane>
      </Tabs>
    </PageContainer>
  );
};
export default SystemInfo;

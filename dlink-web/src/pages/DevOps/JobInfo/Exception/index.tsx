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


import {Empty, Tabs} from 'antd';
import CodeShow from "@/components/Common/CodeShow";
import {l} from "@/utils/intl";

const {TabPane} = Tabs;

const Exception = (props: any) => {

  const {job} = props;

  return (<>
    {job.jobHistory?.exceptions && <Tabs defaultActiveKey="RootException" size="small" tabPosition="top" style={{
      border: "1px solid #f0f0f0"
    }}>

      <TabPane tab={<span>Root Exception</span>} key="RootException">
        <CodeShow code={job.jobHistory?.exceptions['root-exception'] as string} language='java' height='500px'/>
      </TabPane>
      <TabPane tab={<span>Exception History</span>} key="ExceptionHistory">
        <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description={l('global.stay.tuned')}/>
      </TabPane>
    </Tabs>}
  </>)
};

export default Exception;

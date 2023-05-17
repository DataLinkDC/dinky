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

import {Space, Tabs} from 'antd';
import {l} from '@/utils/intl';
import {
  BookOutlined,
  ConsoleSqlOutlined,
  LaptopOutlined,
  SearchOutlined
} from '@ant-design/icons';
import React from 'react';

const {TabPane} = Tabs;

const RightTagsRouter = () => {
  return <>
    <Tabs>
      <TabPane
        tab={<Space><BookOutlined/>{l('rc.ds.detail.tag.desc')}</Space>}
        key={'desc'}
      >
        <div>desc</div>
      </TabPane>

      <TabPane
        tab={<Space><SearchOutlined/>{l('rc.ds.detail.tag.query')}</Space>}
        key={'query'}
      >
        <div>query</div>
      </TabPane>

      <TabPane
        tab={<Space><LaptopOutlined/>{l('rc.ds.detail.tag.gensql')}</Space>}
        key={'gensql'}
      >
        <div>gensql</div>
      </TabPane>

      <TabPane
        tab={<Space><ConsoleSqlOutlined/>{l('rc.ds.detail.tag.console')}</Space>}
        key={'console'}
      >
        <div>console</div>
      </TabPane>
    </Tabs>
  </>;
};

export default RightTagsRouter;

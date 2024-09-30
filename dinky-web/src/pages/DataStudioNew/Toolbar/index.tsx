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

import { Flex, Row, Typography } from 'antd';
import React from 'react';
import './index.less';
import { ToolbarProp } from '@/pages/DataStudioNew/Toolbar/data.d';

export default (props: ToolbarProp) => {
  const { showDesc, showActiveTab, route, onClick, toolbarSelect } = props;
  return (
    <Flex wrap gap={4} justify={'center'} className={'toolbar-side'}>
      {route.map((item) => {
        // 设置显示样式
        let className = 'toolbar-icon-container';
        if (toolbarSelect?.currentSelect === item.key) {
          className += ' toolbar-icon-container-select';
        } else if (toolbarSelect?.allTabs?.has(item.key) && showActiveTab) {
          className += ' toolbar-icon-container-open';
        }
        return (
          <Row
            className={className}
            align={'middle'}
            justify={'center'}
            key={item.key}
            onClick={() => {
              onClick(item);
            }}
          >
            <span
              style={{
                width: '100%',
                textAlign: 'center'
              }}
            >
              {React.cloneElement(item.icon, { className: 'toolbar-icon' })}
            </span>
            {showDesc && <span className={'toolbar-desc'}>{item.title}</span>}
          </Row>
        );
      })}
    </Flex>
  );
};

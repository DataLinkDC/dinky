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

import {Flex, Row} from 'antd';
import React from 'react';
import './index.less';
import {ToolbarPosition, ToolbarProp} from '@/pages/DataStudioNew/Toolbar/data.d';
import {toolbarRoutes} from "@/pages/DataStudioNew/Toolbar/toolbar-route";
import {ReactSortable} from "react-sortablejs";

export default (props: ToolbarProp) => {
  const {showDesc, onClick, toolbarSelect, position, saveToolbarLayout} = props;
  const routes = toolbarSelect.allTabs
  const currentRoutes = routes.map(value => toolbarRoutes.find(item => item.key === value)!!);
  const list = currentRoutes.map(item => ({id: item.key, name: item.title}));
  const justifyContent = position === 'leftBottom' ? 'flex-end' : 'flex-start'
  return (
    <Flex wrap gap={4} justify={'center'} className={'toolbar-side'} id={position}>
      <ReactSortable style={{width: '100%', height: '100%', flexDirection: 'column', display: 'flex', justifyContent}}
                     group={"toolbar"}
                     multiDragKey={position} animation={150} list={list}
                     setList={
                       (newState, sortable) => {
                         if (sortable) {
                           saveToolbarLayout(sortable?.options.multiDragKey!! as ToolbarPosition, newState.map(item => item.id))
                         }
                       }
                     }>
        {currentRoutes.map((item) => {
          // 设置显示样式
          let className = 'toolbar-icon-container';
          if (toolbarSelect?.currentSelect === item.key) {
            className += ' toolbar-icon-container-select';
          } else if (toolbarSelect?.allOpenTabs?.includes(item.key)) {
            className += ' toolbar-icon-container-open';
          }
          return (
            <Row
              id={item.key}
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
              {React.cloneElement(item.icon, {className: 'toolbar-icon'})}
            </span>
              {showDesc && <span className={'toolbar-desc'}>{item.title}</span>}
            </Row>
          );
        })}
      </ReactSortable>
    </Flex>

  );
};

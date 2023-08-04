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

import {Alert, Space} from "antd";
import React from "react";

export default () => {
  return <>
       <Space className={'code-content-empty'} direction="horizontal" size={'large'} style={{ width: '100%' }}>
          <Alert showIcon banner message="修改菜单: 点击左侧树状图中的想要修改的节点, 在此处渲染修改表单" type="success" />
          <Alert showIcon banner message="添加根菜单: 点击树状图的右上角的按钮进行新增" type="info" />
          <Alert showIcon banner message="新增/删除子菜单: 树状图上右键单击,进行添加/删除" type="warning" />
      </Space>
  </>
}
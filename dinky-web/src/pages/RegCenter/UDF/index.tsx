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

import SlowlyAppear from '@/components/Animation/SlowlyAppear';
import TemplateTable from '@/pages/RegCenter/UDF/components/UDFTemplate/TemplateTable';
import {PageContainer} from '@ant-design/pro-components';
import UDFRegister from "@/pages/RegCenter/UDF/components/UDFRegister";
import * as React from "react";

export default () => {
  return (
    <SlowlyAppear>
      <PageContainer
        tabProps={{
          type: 'card',
          size: 'small',
          animated: true,
          tabBarGutter: 10,
          centered: true,
        }}
        tabList={[
            {
                tab: 'UDF 注册管理',
                key: 'udf-register',
                children: <UDFRegister />,
            },
          {
            tab: 'UDF 模版',
            key: 'udf-template',
            children: <TemplateTable />,
          },
        ]}
        title={false}
      />
    </SlowlyAppear>
  );
};

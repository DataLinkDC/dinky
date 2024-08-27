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
import { CreateBtn } from '@/components/CallBackButton/CreateBtn';
import UDFRegister from '@/pages/RegCenter/UDF/components/UDFRegister';
import TemplateTable from '@/pages/RegCenter/UDF/components/UDFTemplate/TemplateTable';
import { l } from '@/utils/intl';
import { PageContainer } from '@ant-design/pro-components';
import { Alert } from 'antd';
import * as React from 'react';

export default () => {
  const [showEdit, setShowEdit] = React.useState<boolean>(false);
  const [activeKey, setActiveKey] = React.useState<string>('udf-register');

  const renderExtra = () => {
    return activeKey === 'udf-register' ? (
      <CreateBtn key={'add'} onClick={() => setShowEdit(true)} />
    ) : null;
  };

  return (
    <SlowlyAppear>
      <PageContainer
        tabProps={{
          type: 'card',
          size: 'small',
          animated: true,
          tabBarGutter: 5,
          centered: true
        }}
        onTabChange={setActiveKey}
        tabActiveKey={activeKey}
        tabBarExtraContent={renderExtra()}
        tabList={[
          {
            tab: l('rc.udf.register.management'),
            key: 'udf-register',
            children: <UDFRegister showEditChange={setShowEdit} showEdit={showEdit} />
          },
          {
            tab: l('rc.udf.template.management'),
            key: 'udf-template',
            children: <TemplateTable />
          }
        ]}
        title={false}
      />
    </SlowlyAppear>
  );
};

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
            children: (
              <>
                {/* TODO: 等待UDF和数据开发联动功能开发完成之后,删除该提示语*/}
                <Alert
                  message={
                    '该功能目前没有和数据开发进行联动,目前仅为展示该 jar 中的相关 UDF,如你在数据开发中使用 UDF 时,你仍然需要按照 Flink 中的创建 UDF 的方式'
                  }
                  type='info'
                  showIcon
                />
                <UDFRegister showEditChange={setShowEdit} showEdit={showEdit} />
              </>
            )
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

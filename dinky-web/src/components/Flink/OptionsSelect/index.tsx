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

import { l } from '@/utils/intl';
import { ProFormSelect } from '@ant-design/pro-components';
import { ProFormSelectProps } from '@ant-design/pro-form/es/components/Select';
import { Divider, Typography } from 'antd';

const { Link } = Typography;

export type FlinkOptionsProps = ProFormSelectProps & {};

const FlinkOptionsSelect = (props: FlinkOptionsProps) => {
  const renderTemplateDropDown = (item: any) => {
    return (
      <>
        <Link href={'#/registration/document'}>+ {l('rc.cc.addConfig')}</Link>
        <Divider style={{ margin: '8px 0' }} />
        {item}
      </>
    );
  };

  return (
    <ProFormSelect
      {...props}
      fieldProps={{ dropdownRender: (item) => renderTemplateDropDown(item) }}
    />
  );
};

export default FlinkOptionsSelect;

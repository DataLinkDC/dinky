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

import { GlobalVar } from '@/types/RegCenter/data';
import ProDescriptions from '@ant-design/pro-descriptions';
import React from 'react';

type GlobalVarDescProps = {
  values: Partial<GlobalVar>;
  columns: any;
};
const GlobalVarDesc: React.FC<GlobalVarDescProps> = (props) => {
  const { values, columns } = props;
  return (
    <>
      <ProDescriptions<GlobalVar>
        column={1}
        loading={values && Object.keys(values).length === 0}
        title={values.name}
        request={async () => ({
          data: values
        })}
        params={{ id: values.id }}
        columns={columns}
      />
    </>
  );
};

export default GlobalVarDesc;

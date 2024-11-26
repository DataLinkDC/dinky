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

import { Flex, Form } from 'antd';
import { useState } from 'react';
import WelcomeItem from '@/pages/Other/Welcome/WelcomeItem/WelcomItem';
import BaseConfigItem from '@/pages/Other/Welcome/WelcomeItem/BaseConfigItem';
import FlinkConfigItem from '@/pages/Other/Welcome/WelcomeItem/FlinkConfigItem';
import FinishPage from '@/pages/Other/Welcome/WelcomeItem/FinishPage';
import { log } from '@antv/g6/lib/utils/scale';
import { history, useRequest } from '@@/exports';
import { API_CONSTANTS } from '@/services/endpoints';
import { GLOBAL_SETTING_KEYS } from '@/types/SettingCenter/data';
import { postAll } from '@/services/api';
import { sleep } from '@antfu/utils';

const boxStyle: React.CSSProperties = {
  width: '100%',
  height: '100%',
  borderRadius: 6
};

export type WelcomeProps = {
  onNext: () => void;
  onPrev: () => void;
  onSubmit?: () => void;
};

const Welcome = () => {
  const [form] = Form.useForm();
  const [formData, setFormData] = useState({});
  const [current, setCurrent] = useState(0);

  const { data, loading } = useRequest(API_CONSTANTS.GET_NEEDED_CFG);

  const setCfgReq = useRequest((params) => postAll(API_CONSTANTS.SET_INIT_CFG, params), {
    manual: true
  });

  const next = () => {
    setFormData((prev) => {
      return { ...prev, ...form.getFieldsValue() };
    });
    setCurrent(current + 1);
  };
  const prev = () => {
    setCurrent(current - 1);
  };

  const submit = async () => {
    const data = { ...formData, ...form.getFieldsValue() };
    await setCfgReq.run(data);
    next();
  };

  return (
    <Flex style={boxStyle} justify={'center'} align={'center'}>
      <div style={{ height: '60vh', background: 'white', width: '50%' }}>
        {loading ? (
          <div>loading</div>
        ) : (
          <Form form={form} initialValues={data} layout='vertical'>
            {current == 0 && <WelcomeItem onNext={next} onPrev={prev} />}
            {current == 1 && <BaseConfigItem onNext={next} onPrev={prev} />}
            {current == 2 && <FlinkConfigItem onNext={next} onPrev={prev} onSubmit={submit} />}
            {current == 3 && <FinishPage />}
          </Form>
        )}
      </div>
    </Flex>
  );
};

export default Welcome;

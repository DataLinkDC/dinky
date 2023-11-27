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

import { renderTimeLineItems } from '@/pages/Other/PersonCenter/LoginLogRecord/function';
import { queryDataByParams } from '@/services/BusinessCrud';
import { API_CONSTANTS } from '@/services/endpoints';
import { LoginLog } from '@/types/AuthCenter/data';
import { l } from '@/utils/intl';
import { Button, Card, Spin, Timeline } from 'antd';
import React, { useCallback, useEffect, useState } from 'react';

type LoginLogRecordProps = {
  userId: number;
};
const LoginLogRecord: React.FC<LoginLogRecordProps> = (props) => {
  const { userId } = props;

  const [loginRecord, setLoginRecord] = useState<LoginLog[]>([]);
  const [refresh, setRefresh] = useState(false);

  const queryLoginLog = useCallback(
    async (id: number) => {
      setRefresh(true);
      await queryDataByParams(`${API_CONSTANTS.LOGIN_RECORD}/${id}`).then((res) => {
        setLoginRecord(res);
        setRefresh(false);
      });
    },
    [userId]
  );

  useEffect(() => {
    queryLoginLog(userId);
  }, [userId]);

  return (
    <>
      <Card
        bordered={false}
        extra={<Button onClick={() => queryLoginLog(userId)}>{l('button.refresh')}</Button>}
        size='small'
      >
        <Spin spinning={refresh}>
          <Timeline mode={'alternate'} reverse items={renderTimeLineItems(loginRecord)} />
        </Spin>
      </Card>
    </>
  );
};

export default LoginLogRecord;

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

import { restartTask } from '@/pages/DataStudio/HeaderContainer/service';
import { l } from '@/utils/intl';
import { ErrorMessage, SuccessMessage } from '@/utils/messages';
import { Modal } from 'antd';

export const recoveryCheckPoint = (taskId: number, path: string) => {
  Modal.confirm({
    title: l('devops.jobinfo.ck.recovery'),
    content: l('devops.jobinfo.ck.recoveryConfirm', '', {
      path: path
    }),
    okText: l('button.confirm'),
    cancelText: l('button.cancel'),
    onOk: async () => {
      const result = await restartTask(taskId, path, l('devops.jobinfo.ck.recovery'));
      if (result && result.code == 0) {
        SuccessMessage(l('devops.jobinfo.ck.recovery.success'));
      } else {
        ErrorMessage(l('devops.jobinfo.ck.recovery.failed'));
      }
    }
  });
};

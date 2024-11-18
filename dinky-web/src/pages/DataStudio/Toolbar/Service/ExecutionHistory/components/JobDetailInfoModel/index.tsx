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

import { JobExecutionHistory } from '@/types/Studio/data';
import { l } from '@/utils/intl';
import { Modal } from 'antd';
import React from 'react';
import { JobConfigInfo } from '@/pages/DataStudio/Toolbar/Service/ExecutionHistory/components/JobDetailInfoModel/JobConfigInfo';
import { StatementInfo } from '@/pages/DataStudio/Toolbar/Service/ExecutionHistory/components/JobDetailInfoModel/StatementInfo';
import { PreViewData } from '@/pages/DataStudio/Toolbar/Service/ExecutionHistory/components/JobDetailInfoModel/PreViewData';
import { ErrorMsgInfo } from '@/pages/DataStudio/Toolbar/Service/ExecutionHistory/components/JobDetailInfoModel/ErrorMsgInfo';

type JobDetailInfoModelProps = {
  modalVisit: boolean;
  handleCancel: () => void;
  row: JobExecutionHistory | undefined;
  type: number;
};

export const JobDetailInfoModel: React.FC<JobDetailInfoModelProps> = (props) => {
  const { modalVisit, handleCancel, row, type } = props;

  return (
    <>
      <Modal
        width={'80%'}
        open={modalVisit}
        destroyOnClose
        maskClosable={false}
        okButtonProps={{
          htmlType: 'submit',
          autoFocus: true,
          style: {
            display: 'none'
          }
        }}
        cancelText={l('button.close')}
        onCancel={handleCancel}
      >
        {type == 1 && <JobConfigInfo row={row} />}
        {type == 2 && <StatementInfo row={row} />}
        {/*todo 预览数据*/}
        {/*{type == 3 && <PreViewData row={row} />}*/}
        {type == 4 && <ErrorMsgInfo row={row} />}
      </Modal>
    </>
  );
};

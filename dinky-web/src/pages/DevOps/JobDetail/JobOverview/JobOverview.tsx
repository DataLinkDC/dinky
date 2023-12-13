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

import FlinkDag from '@/components/Flink/FlinkDag';
import { JobProps } from '@/pages/DevOps/JobDetail/data';
import FlinkTable from '@/pages/DevOps/JobDetail/JobOverview/components/FlinkTable';
import JobDesc from '@/pages/DevOps/JobDetail/JobOverview/components/JobDesc';
import { l } from '@/utils/intl';
import { ProCard } from '@ant-design/pro-components';
import { Button, Empty, Result } from 'antd';
import { useState } from 'react';
import { isStatusDone } from '../../function';

const JobConfigTab = (props: JobProps) => {
  const { jobDetail } = props;
  const job = jobDetail?.jobDataDto?.job;
  const [showHistory, setShowHistory] = useState<boolean>(false);

  return (
    <>
      {isStatusDone(jobDetail?.instance?.status as string) && !showHistory ? (
        <Result
          status='warning'
          title={l('devops.jobinfo.unable.obtain.status')}
          extra={
            <Button
              type='primary'
              key='console'
              onClick={() => {
                setShowHistory(true);
              }}
            >
              {l('devops.jobinfo.recently.job.status')}
            </Button>
          }
        />
      ) : undefined}
      {showHistory || !isStatusDone(jobDetail?.instance?.status as string) ? (
        <>
          <JobDesc jobDetail={jobDetail} />
          <ProCard
            style={{
              height: '40vh'
            }}
          >
            {job ? (
              <FlinkDag job={job} checkPoints={jobDetail.jobDataDto.checkpoints} />
            ) : (
              <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
            )}
          </ProCard>

          <FlinkTable jobDetail={jobDetail} />
        </>
      ) : undefined}
    </>
  );
};

export default JobConfigTab;

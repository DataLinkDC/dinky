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

import useHookRequest from '@/hooks/useHookRequest';
import { JobMetricsItem } from '@/pages/DevOps/JobDetail/data';
import { getFLinkVertices } from '@/pages/DevOps/JobDetail/srvice';
import { Jobs } from '@/types/DevOps/data';
import { l } from '@/utils/intl';
import { Transfer } from 'antd';
import { useState } from 'react';

export type ConfigTabProps = {
  vertice: Jobs.JobVertices;
  initSelected: JobMetricsItem[];
  onValueChange: (vertice: Jobs.JobVertices, targetKeys: string[]) => void;
  jobDetail: Jobs.JobInfoDetail;
};
const MetricsConfigTab = (props: ConfigTabProps) => {
  const { vertice, onValueChange, initSelected, jobDetail } = props;

  const [targetKeys, setTargetKeys] = useState<string[]>(initSelected?.map((i) => i.metrics));

  const jobManagerUrl = jobDetail.clusterInstance?.jobManagerHost;
  const jobId = jobDetail.jobDataDto.job?.jid;

  const { data } = useHookRequest(getFLinkVertices, {
    defaultParams: [jobManagerUrl, jobId, vertice.id]
  });

  const onSelectChange = (targetKeys: string[]) => {
    setTargetKeys(targetKeys);
    onValueChange(vertice, targetKeys);
  };
  return (
    <>
      <Transfer<Jobs.JobVertices>
        showSearch={true}
        dataSource={data ?? []}
        titles={[l('devops.jobinfo.metrics.metricsItems'), l('devops.jobinfo.metrics.selected')]}
        targetKeys={targetKeys}
        onChange={onSelectChange}
        rowKey={(item) => item.id}
        render={(item) => item.id}
        listStyle={{ width: '42vh', height: '50vh' }}
      />
    </>
  );
};

export default MetricsConfigTab;

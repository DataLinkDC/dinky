/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

import { JobMetricsItem } from '@/pages/DevOps/JobDetail/data';
import { DevopsType } from '@/pages/DevOps/JobDetail/model';
import { API_CONSTANTS } from '@/services/endpoints';
import { l } from '@/utils/intl';
import { connect, useRequest } from '@@/exports';
import { Transfer } from 'antd';

const MetricsConfigTab = (props: any) => {
  const { vertice, onValueChange, jobDetail, metricsTarget } = props;
  const jobManagerUrl = jobDetail.cluster?.jobManagerHost;
  const jobId = jobDetail.jobDataDto.job?.jid;

  const { data } = useRequest({
    url: API_CONSTANTS.GET_JOB_METRICS_ITEMS,
    params: { address: jobManagerUrl, jobId: jobId, verticeId: vertice.id }
  });

  const targetKeys = (data: []) => data.map((i: JobMetricsItem) => i.metrics);

  return (
    <>
      <Transfer
        showSearch={true}
        dataSource={data ?? []}
        titles={[l('devops.jobinfo.metrics.metricsItems'), l('devops.jobinfo.metrics.selected')]}
        targetKeys={targetKeys(metricsTarget[vertice.id] ?? [])}
        onChange={(tgk) => onValueChange(vertice, tgk)}
        rowKey={(item) => item.id}
        render={(item) => item.id}
        listStyle={{ width: '42vh', height: '50vh' }}
        // oneWay
      />
    </>
  );
};

export default connect(({ Devops }: { Devops: DevopsType }) => ({
  jobDetail: Devops.jobInfoDetail,
  metricsTarget: Devops.metrics.jobMetricsTarget,
  layoutName: Devops.metrics.layoutName
}))(MetricsConfigTab);

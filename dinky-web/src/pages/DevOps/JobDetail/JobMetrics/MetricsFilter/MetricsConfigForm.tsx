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

import { JOB_STATUS } from '@/pages/DevOps/constants';
import { JobMetricsItem } from '@/pages/DevOps/JobDetail/data';
import MonitorConfigTab from '@/pages/DevOps/JobDetail/JobMetrics/MetricsFilter/MetricsConfigTab';
import { putMetricsLayout } from '@/pages/DevOps/JobDetail/JobMetrics/service';
import { DevopsType } from '@/pages/DevOps/JobDetail/model';
import { l } from '@/utils/intl';
import { ModalForm } from '@ant-design/pro-components';
import { Button, Tabs } from 'antd';
import { connect } from 'umi';

const MetricsConfigForm = (props: any) => {
  const { dispatch, jobDetail, metricsTarget, layoutName } = props;

  const saveJobMetrics = async () => {
    let params: JobMetricsItem[] = [];
    for (let value of Object.values(metricsTarget)) {
      // @ts-ignore
      params.push(...value);
    }
    await putMetricsLayout(layoutName, params);
    dispatch({
      type: 'Devops/queryMetricsTarget',
      payload: { layoutName: layoutName }
    });
    return true;
  };

  const onValueChange = (vertice: any, keys: string[]) => {
    const data = keys.map((key) => ({
      taskId: jobDetail.history.taskId,
      vertices: vertice.id,
      metrics: key,
      title: vertice.name,
      layoutName: layoutName,
      showType: 'Chart',
      showSize: '25%'
    }));

    dispatch({
      type: 'Devops/updateMetricsTarget',
      payload: { verticeId: vertice.id, data: data }
    });
  };

  const itemTabs = jobDetail?.jobDataDto?.job?.vertices?.map((item: any) => {
    return {
      key: item.id,
      label: item.name,
      children: <MonitorConfigTab vertice={item} onValueChange={onValueChange} />
    };
  });

  return (
    <ModalForm
      width={1000}
      layout={'horizontal'}
      title={l('devops.jobinfo.metrics.configMetrics')}
      trigger={
        <Button type='primary' disabled={jobDetail.instance.status != JOB_STATUS.RUNNING}>
          {l('devops.jobinfo.metrics.configMetrics')}
        </Button>
      }
      onFinish={async () => await saveJobMetrics()}
    >
      <Tabs items={itemTabs} />
    </ModalForm>
  );
};

export default connect(({ Devops }: { Devops: DevopsType }) => ({
  jobDetail: Devops.jobInfoDetail,
  metricsTarget: Devops.metrics.jobMetricsTarget,
  layoutName: Devops.metrics.layoutName
}))(MetricsConfigForm);

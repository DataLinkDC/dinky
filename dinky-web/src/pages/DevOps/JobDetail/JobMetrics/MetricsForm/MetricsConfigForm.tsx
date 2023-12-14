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

import { JobMetricsItem } from '@/pages/DevOps/JobDetail/data';
import MetricsConfigTab from '@/pages/DevOps/JobDetail/JobMetrics/MetricsForm/MetricsConfigTab';
import { Jobs } from '@/types/DevOps/data';
import { l } from '@/utils/intl';
import { ModalForm } from '@ant-design/pro-components';
import { Button, Tabs } from 'antd';
import { useState } from 'react';

export type MetricsConfigFormProps = {
  jobDetail: Jobs.JobInfoDetail;
  initSelected: Record<string, JobMetricsItem[]>;
  onSelectChange: (target: Record<string, JobMetricsItem[]>) => Promise<boolean>;
};
const MetricsConfigForm = (props: MetricsConfigFormProps) => {
  const { jobDetail, initSelected, onSelectChange } = props;
  const [selMetricsTarget, setSelMetricsTarget] = useState<Record<string, JobMetricsItem[]>>({});
  const vertices = jobDetail?.jobDataDto?.job?.vertices;

  const saveJobMetrics = async () => {
    return await onSelectChange(selMetricsTarget);
  };

  const onValueChange = (vertice: Jobs.JobVertices, keys: string[]) => {
    setSelMetricsTarget((prevState) => {
      return {
        ...prevState,
        [vertice.id]: keys.map((key) => ({
          taskId: jobDetail.instance.taskId,
          vertices: vertice.id,
          metrics: key,
          title: vertice.name,
          showType: 'Chart',
          showSize: '25%'
        }))
      };
    });
  };

  const itemTabs = vertices?.map((item: any) => {
    return {
      key: item.id,
      label: item.name,
      children: (
        <>
          <MetricsConfigTab
            vertice={item}
            jobDetail={jobDetail}
            initSelected={initSelected?.[item.id]}
            onValueChange={onValueChange}
          />
        </>
      )
    };
  });

  return (
    <ModalForm
      width={1000}
      layout={'horizontal'}
      title={l('devops.jobinfo.metrics.configMetrics')}
      trigger={<Button type='primary'>{l('devops.jobinfo.metrics.configMetrics')}</Button>}
      modalProps={{
        okButtonProps: {
          htmlType: 'submit',
          autoFocus: true
        }
      }}
      onFinish={async () => await saveJobMetrics()}
    >
      <Tabs items={itemTabs} />
    </ModalForm>
  );
};

export default MetricsConfigForm;

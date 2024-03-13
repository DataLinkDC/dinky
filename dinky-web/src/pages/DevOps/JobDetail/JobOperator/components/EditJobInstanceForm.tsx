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

import { CLUSTER_INSTANCE_TYPE } from '@/pages/RegCenter/Cluster/constants';
import { validatorJMHAAdderess } from '@/pages/RegCenter/Cluster/Instance/components/function';
import { handleAddOrUpdate } from '@/services/BusinessCrud';
import { API_CONSTANTS } from '@/services/endpoints';
import { Jobs } from '@/types/DevOps/data';
import { l } from '@/utils/intl';
import { EditOutlined } from '@ant-design/icons';
import {
  DrawerForm,
  ProFormSelect,
  ProFormText,
  ProFormTextArea
} from '@ant-design/pro-components';
import { Button, Divider, Form, Typography } from 'antd';
const { Text } = Typography;

const EditJobInstanceForm = (props: {
  jobDetail: Jobs.JobInfoDetail;
  refeshJob: (isForce: boolean) => void;
}) => {
  const [form] = Form.useForm();
  const { jobDetail, refeshJob } = props;

  const handleSubmit = async (values: any) => {
    const cluster = {
      id: jobDetail.clusterInstance.id,
      hosts: values.hosts,
      autoRegisters: jobDetail.clusterInstance.autoRegisters
    };
    const instance = { id: jobDetail.instance.id, jid: values.jid };
    await handleAddOrUpdate(API_CONSTANTS.CLUSTER_INSTANCE, cluster);
    await handleAddOrUpdate(API_CONSTANTS.JOB_INSTANCE, instance);
    refeshJob(true);
    return true;
  };

  return (
    <DrawerForm
      title={l('devops.jobinfo.remap.title')}
      form={form}
      initialValues={{ ...jobDetail?.clusterInstance, ...jobDetail?.instance }}
      trigger={<Button icon={<EditOutlined />} />}
      onFinish={handleSubmit}
    >
      <Divider orientation={'left'} style={{ margin: '8px 0' }}>
        {l('devops.jobinfo.remap.cluster.title')}
        <Text type='danger'> {l('devops.jobinfo.remap.cluster.title.help')}</Text>
      </Divider>
      <ProFormText
        name='name'
        label={l('rc.ci.name')}
        disabled
        rules={[{ required: true, message: l('rc.ci.namePlaceholder') }]}
        placeholder={l('rc.ci.namePlaceholder')}
      />

      <ProFormText
        name='alias'
        label={l('rc.ci.alias')}
        disabled
        placeholder={l('rc.ci.aliasPlaceholder')}
      />

      <ProFormSelect
        name='type'
        label={l('rc.ci.type')}
        disabled
        options={CLUSTER_INSTANCE_TYPE()}
        rules={[{ required: true, message: l('rc.ci.typePlaceholder') }]}
        placeholder={l('rc.ci.typePlaceholder')}
      />

      <ProFormTextArea
        name='hosts'
        label={l('rc.ci.jmha')}
        tooltip={l('rc.ci.jmha.tips')}
        validateTrigger={['onChange']}
        rules={[
          {
            required: true,
            validator: (rule, hostsValue) => validatorJMHAAdderess(rule, hostsValue)
          }
        ]}
        placeholder={l('rc.ci.jmhaPlaceholder')}
      />
      <ProFormTextArea
        name='note'
        label={l('global.table.note')}
        disabled
        placeholder={l('global.table.notePlaceholder')}
      />

      <Divider orientation={'left'} style={{ margin: '8px 0' }}>
        {l('devops.jobinfo.remap.job.title')}
      </Divider>
      <ProFormText name='jid' label='Job Id' placeholder={l('rc.ci.aliasPlaceholder')} />
    </DrawerForm>
  );
};
export default EditJobInstanceForm;

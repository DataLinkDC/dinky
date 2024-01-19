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

import StatusTag from '@/components/JobTags/StatusTag';
import { JobProps } from '@/pages/DevOps/JobDetail/data';
import { parseMilliSecondStr } from '@/utils/function';
import { l } from '@/utils/intl';
import { Link } from '@@/exports';
import { RocketOutlined } from '@ant-design/icons';
import { ProCard } from '@ant-design/pro-components';
import { Descriptions, Tag, Typography } from 'antd';

const { Text } = Typography;

/**
 * Renders the JobConfigTab component.
 *
 * @param {JobProps} props - The component props containing the job detail.
 * @returns {JSX.Element} - The rendered JobConfigTab component.
 */
const JobDesc = (props: JobProps) => {
  const { jobDetail } = props;

  /**
   * Retrieves the savepoint strategy based on the provided strategy value.
   *
   * @param {string} strategy - The savepoint strategy value.
   * @returns {string} - The localized savepoint strategy label.
   */
  const getSavePointStrategy = (strategy: string) => {
    switch (strategy) {
      case 'NONE':
        return l('global.savepoint.strategy.disabled');
      case 'LATEST':
        return l('global.savepoint.strategy.latest');
      case 'EARLIEST':
        return l('global.savepoint.strategy.earliest');
      case 'CUSTOM':
        return l('global.savepoint.strategy.custom');
      default:
        return l('global.savepoint.strategy.disabled');
    }
  };

  /**
   * Generates the user custom flink configuration description items.
   *
   * @param {object} config - The user configuration object.
   * @returns {JSX.Element[]} - An array of Descriptions.Item components representing the user configuration.
   */
  // const getUserConfig = (config: any) => {
  //   let formList = [];
  //   for (let configKey in config) {
  //     formList.push(<Descriptions.Item label={configKey}>{config[configKey]}</Descriptions.Item>);
  //   }
  //   return formList;
  // };

  return (
    <>
      <ProCard>
        <Descriptions bordered size='small' column={5}>
          <Descriptions.Item label={l('global.table.status')}>
            <StatusTag status={jobDetail?.instance?.status} />
          </Descriptions.Item>

          <Descriptions.Item label={l('devops.jobinfo.config.submitType')}>
            <Tag color='pink'>
              <RocketOutlined /> {jobDetail?.history?.type}
            </Tag>
          </Descriptions.Item>

          <Descriptions.Item label={l('devops.jobinfo.config.RestartStrategy')}>
            <Tag color='blue' title={'Restart Strategy'}>
              {jobDetail?.jobDataDto?.config['execution-config']['restart-strategy']}
            </Tag>
          </Descriptions.Item>

          <Descriptions.Item label={l('devops.jobinfo.config.ClusterInstanceName')}>
            <Link to={'/registration/cluster/instance'}>{jobDetail?.clusterInstance?.alias}</Link>
          </Descriptions.Item>

          <Descriptions.Item label={l('devops.jobinfo.config.JobId')}>
            <Text copyable>{jobDetail?.instance?.jid}</Text>
          </Descriptions.Item>

          <Descriptions.Item label={l('devops.jobinfo.config.taskId')}>
            <Text copyable>{jobDetail?.instance?.taskId}</Text>
          </Descriptions.Item>

          <Descriptions.Item label={l('devops.jobinfo.config.useSqlFragment')}>
            {jobDetail?.history?.configJson?.fragment ? l('button.enable') : l('button.disable')}
          </Descriptions.Item>

          <Descriptions.Item label={l('devops.jobinfo.config.execmode')}>
            {jobDetail?.history?.configJson?.batchModel
              ? l('global.table.execmode.batch')
              : l('global.table.execmode.streaming')}
          </Descriptions.Item>

          <Descriptions.Item label={l('global.table.createTime')}>
            {jobDetail?.instance?.createTime?.toString()}
          </Descriptions.Item>

          <Descriptions.Item label={l('devops.jobinfo.config.JobParallelism')}>
            {jobDetail?.jobDataDto?.config['execution-config']['job-parallelism']}
          </Descriptions.Item>

          <Descriptions.Item label={l('global.table.useTime')}>
            {parseMilliSecondStr(jobDetail?.instance?.duration)}
          </Descriptions.Item>

          <Descriptions.Item label={l('devops.jobinfo.config.startFromSavePoint')}>
            {getSavePointStrategy(jobDetail?.history?.configJson?.savePointStrategy)}
          </Descriptions.Item>

          <Descriptions.Item label={l('devops.jobinfo.config.savePointPath')} span={2}>
            {jobDetail?.history?.configJson?.configJson['state.savepoints.dir'] ?? '-'}
          </Descriptions.Item>
        </Descriptions>
      </ProCard>
      <br />
      {/*<ProCard>*/}
      {/*  <Descriptions title={l('devops.jobinfo.config.UserCustomConf')} bordered size="small">*/}
      {/*    {getUserConfig(jobDetail?.history?.config?.config)}*/}
      {/*  </Descriptions>*/}
      {/*</ProCard>*/}
    </>
  );
};

export default JobDesc;

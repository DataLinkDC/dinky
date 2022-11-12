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


import {Descriptions, Divider, Tag, Typography} from 'antd';
import {RocketOutlined} from '@ant-design/icons';
import {l} from "@/utils/intl";

const {Text, Link} = Typography;

const Config = (props: any) => {

  const {job} = props;


  return <>
    <>
      <Divider children={"Dinky Job Configuration"} orientation={"left"}/>
      <Descriptions bordered size="small">
        <Descriptions.Item label="执行模式">{job?.history?.type ? (
          <Tag color="blue" key={job?.history?.type}>
            <RocketOutlined/> {job?.history?.type}
          </Tag>
        ) : undefined}
        </Descriptions.Item>
        <Descriptions.Item label="集群实例">
          {job?.cluster?.alias ? <Link>{job?.cluster?.alias}</Link> : '-'}
        </Descriptions.Item>
        <Descriptions.Item label="集群配置">
          {job?.clusterConfiguration?.alias ? <Link>{job?.clusterConfiguration?.alias}</Link> : '-'}
        </Descriptions.Item>
        <Descriptions.Item label="共享会话">
          {job?.history?.session ? <Link>{job?.history?.session}</Link> : l('button.disable')}
        </Descriptions.Item>
        <Descriptions.Item label="片段机制">{job?.history?.config.useSqlFragment ? l('button.enable') : l('button.disable')}</Descriptions.Item>
        <Descriptions.Item label="语句集">{job?.history?.config.useStatementSet ? l('button.enable') : l('button.disable')}</Descriptions.Item>
        <Descriptions.Item label="任务类型">{job?.history?.config.isJarTask ? 'Jar' : 'FlinkSQL'}</Descriptions.Item>
        <Descriptions.Item label="批模式">{job?.history?.config.useBatchModel ? l('button.enable') : l('button.disable')}</Descriptions.Item>
        <Descriptions.Item label="CheckPoint">{job?.history?.config.checkpoint}</Descriptions.Item>
        <Descriptions.Item label="SavePoint机制">
          {job?.history?.config.savePointStrategy == 'NONE' ? l('global.savepoint.strategy.disabled') :
            job?.history?.config.savePointStrategy == 'LATEST' ? l('global.savepoint.strategy.latest') :
              job?.history?.config.savePointStrategy == 'EARLIEST' ? l('global.savepoint.strategy.earliest') :
                job?.history?.config.savePointStrategy == 'CUSTOM' ? l('global.savepoint.strategy.custom') : l('global.savepoint.strategy.disabled')}
        </Descriptions.Item>
        <Descriptions.Item label="SavePoint" span={2}>{job?.history?.config.savePointPath}</Descriptions.Item>
        {job?.jar ? <>
          <Descriptions.Item label="Jar 路径">{job?.jar?.path}</Descriptions.Item>
          <Descriptions.Item label="Jar 主类">{job?.jar?.mainClass}</Descriptions.Item>
          <Descriptions.Item label="Jar 入参">{job?.jar?.paras}</Descriptions.Item>
        </> : undefined}
      </Descriptions>
    </>
    <>
      {(!JSON.stringify(job?.jobHistory?.config).includes("errors") && job?.jobHistory?.config) &&
        <>
          <br/>
          <Divider children={"Flink Job Configuration"} orientation={"left"}/>
          <Descriptions bordered size="small">
            <Descriptions.Item label="Execution Mode">
              <Tag color="blue" title={"Execution Mode"}>
                {job?.jobHistory?.config['execution-config']['execution-mode']}
              </Tag>
            </Descriptions.Item>
            <Descriptions.Item label="Restart Strategy">
              <Tag color="blue" title={"Restart Strategy"}>
                {job?.jobHistory?.config['execution-config']['restart-strategy']}
              </Tag>
            </Descriptions.Item>

            <Descriptions.Item label="Job Parallelism">
              <Tag color="blue" title={"Job Parallelism"}>
                {job?.jobHistory?.config['execution-config']['job-parallelism']}
              </Tag>
            </Descriptions.Item>

            <Descriptions.Item label="Object Reuse Mode">
              <Tag color="blue" title={"Object Reuse Mode"}>
                {job?.jobHistory?.config['execution-config']['object-reuse-mode'].toString()}
              </Tag>
            </Descriptions.Item>

            <Descriptions.Item label="Flink User Configuration" span={3}>
              <Text
                code>{JSON.stringify(job?.jobHistory?.config['execution-config']['user-config'])}</Text>
            </Descriptions.Item>
          </Descriptions></>
      }
    </>
  </>
};

export default Config;

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



import {Jobs} from "@/types/DevOps/data";
import {Descriptions, Tag} from "antd";
import {
  CheckCircleOutlined,
  CloseCircleOutlined,
  ExclamationCircleOutlined,
  RocketOutlined,
  SyncOutlined
} from "@ant-design/icons";
import moment from "moment/moment";

type JobProps = {
  jobDetail: Jobs.JobInfoDetail;
};

/**
 * Renders the JobConfigTab component.
 *
 * @param {JobProps} props - The component props containing the job detail.
 * @returns {JSX.Element} - The rendered JobConfigTab component.
 */
const CkDesc = (props: JobProps) => {

  const {jobDetail} = props;

  let counts = jobDetail?.jobHistory?.checkpoints.counts
  let latest = jobDetail?.jobHistory?.checkpoints.latest
  let checkpointsConfigInfo = jobDetail?.jobHistory?.checkpointsConfig
  console.log(jobDetail)
  return (
    <>
      <Descriptions bordered size="small" column={3}>
        <Descriptions.Item label="Checkpointing Mode">
          <Tag color="blue" title={"Checkpointing Mode"}>
            {checkpointsConfigInfo.mode.toUpperCase()}
          </Tag>
        </Descriptions.Item>

        <Descriptions.Item label="Interval">
          <Tag color="blue" title={"Interval"}>
            {checkpointsConfigInfo.interval}
          </Tag>
        </Descriptions.Item>

        <Descriptions.Item label="Timeout">
          <Tag color="blue" title={"Timeout"}>
            {checkpointsConfigInfo.timeout}
          </Tag>
        </Descriptions.Item>

        <Descriptions.Item label="Unaligned Checkpoints ">
          <Tag color="blue" title={"Unaligned Checkpoints"}>
            {checkpointsConfigInfo.unaligned_checkpoints ? 'Enabled' : 'Disabled'}
          </Tag>
        </Descriptions.Item>

        <Descriptions.Item label="Persist Checkpoints Externally Enabled">
          <Tag color="blue" title={"Persist Checkpoints Externally Enabled"}>
            {checkpointsConfigInfo.externalization.enabled ? 'Enabled' : 'Disabled'}
          </Tag>
        </Descriptions.Item>


        <Descriptions.Item label="CheckPoint Counts">
          <Tag color="blue" title={"Total"}>
            <RocketOutlined/> Total: {counts.total}
          </Tag>
          <Tag color="red" title={"Failed"}>
            <CloseCircleOutlined/> Failed: {counts.failed}
          </Tag>
          <Tag color="cyan" title={"Restored"}>
            <ExclamationCircleOutlined/> Restored: {counts.restored}
          </Tag>
          <Tag color="green" title={"Completed"}>
            <CheckCircleOutlined/> Completed: {counts.completed}
          </Tag>
          <Tag color="orange" title={"In Progress"}>
            <SyncOutlined spin/> In Progress: {counts.in_progress}
          </Tag>
        </Descriptions.Item>

        <Descriptions.Item label="Latest Failed CheckPoint">
          {latest.failed === null ?
            <Tag color="red" title={"Latest Failed CheckPoint"}>
              {'None'}
            </Tag> :
            <>
              <Tag color="red" title={"Latest Failed CheckPoint"}>
                {"id： " + latest.failed.id}
              </Tag>
              <Tag color="red" title={"Latest Failed CheckPoint"}>
                {"Cause： " + latest.failed.failure_message}
              </Tag>
            </>
          }
        </Descriptions.Item>

        <Descriptions.Item label="Latest Restored">
          <Tag color="cyan" title={"Latest Restored"}>
            {latest.restored === null ? 'None' :
              latest.restored.external_path}
          </Tag>
        </Descriptions.Item>

        <Descriptions.Item label="Latest Savepoint">
          <Tag color="purple" title={"Latest Savepoint"}>
            {latest.savepoint === null ? 'None' :
              latest.savepoint.external_path
            }
          </Tag>
        </Descriptions.Item>

        <Descriptions.Item label="Latest Completed CheckPoint">
          <Tag color="green" title={"Latest Completed CheckPoint"}>
            {latest.completed === null ? 'None' :
              latest.completed.external_path
            }
          </Tag>
        </Descriptions.Item>

      </Descriptions>
    </>
  )
};

export default CkDesc;

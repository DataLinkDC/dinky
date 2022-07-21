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


import {Button, Descriptions, Empty, message, Modal, Tabs, Tag} from 'antd';
import {
  CheckCircleOutlined,
  CloseCircleOutlined,
  ExclamationCircleOutlined,
  MinusCircleOutlined,
  RocketOutlined,
  SyncOutlined
} from "@ant-design/icons";
import {parseByteStr, parseMilliSecondStr, parseSecondStr} from "@/components/Common/function";
import ProTable, {ActionType, ProColumns} from "@ant-design/pro-table";
import {useRef} from "react";
import {CheckPointsDetailInfo} from "@/pages/DevOps/data";
import {CODE, queryData} from "@/components/Common/crud";
import {selectSavePointRestartTask} from "@/pages/DevOps/service";
import {JOB_LIFE_CYCLE} from "@/components/Common/JobLifeCycle";
import {history} from 'umi';
import {SavePointTableListItem} from "@/components/Studio/StudioRightTool/StudioSavePoint/data";
import moment from "moment";

const {TabPane} = Tabs;

const CheckPoints = (props: any) => {

  const {job} = props;
  const actionRef = useRef<ActionType>();

  const JsonParseObject = (item: any) => {
    return JSON.parse(JSON.stringify(item))
  }


  const getOverview = (checkpoints: any) => {

    let counts = JsonParseObject(checkpoints.counts)
    let latest = JsonParseObject(checkpoints.latest)

    return (
      <>
        {JSON.stringify(job?.jobHistory?.checkpoints).includes("errors") ?
          <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/> :
          <Descriptions bordered size="small" column={1}>
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

            <Descriptions.Item label="Latest Completed CheckPoint">
              <Tag color="green" title={"Latest Completed CheckPoint"}>
                {latest.completed === null ? 'None' :
                  JsonParseObject(latest.completed).external_path
                }
              </Tag>
            </Descriptions.Item>

            <Descriptions.Item label="Latest Failed CheckPoint">
              {latest.failed === null ?
                <Tag color="red" title={"Latest Failed CheckPoint"}>
                  {'None'}
                </Tag> :
                <>
                  <Tag color="red" title={"Latest Failed CheckPoint"}>
                    {"id： " + JsonParseObject(latest.failed).id}
                  </Tag>
                  <Tag color="red" title={"Latest Failed CheckPoint"}>
                    {"Fail Time： " + moment(JsonParseObject(latest.failed).failure_timestamp).format('YYYY-MM-DD HH:mm:ss')}
                  </Tag>
                  <Tag color="red" title={"Latest Failed CheckPoint"}>
                    {"Cause： " + JsonParseObject(latest.failed).failure_message}
                  </Tag>
                </>
              }
            </Descriptions.Item>

            <Descriptions.Item label="Latest Restored">
              <Tag color="cyan" title={"Latest Restored"}>
                {latest.restored === null ? 'None' :
                  JsonParseObject(latest.restored).external_path}
              </Tag>
            </Descriptions.Item>

            <Descriptions.Item label="Latest Savepoint">
              <Tag color="purple" title={"Latest Savepoint"}>
                {latest.savepoint === null ? 'None' :
                  JsonParseObject(latest.savepoint).external_path
                }
              </Tag>
            </Descriptions.Item>
          </Descriptions>
        }
      </>
    )
  }

  const getSummary = (checkpoints: any) => {

    let end_to_end_duration = JsonParseObject(JsonParseObject(checkpoints.summary)).end_to_end_duration
    let state_size = JsonParseObject(JsonParseObject(checkpoints.summary)).state_size
    let processed_data = JsonParseObject(JsonParseObject(checkpoints.summary)).processed_data
    let persisted_data = JsonParseObject(JsonParseObject(checkpoints.summary)).persisted_data
    let alignment_buffered = JsonParseObject(JsonParseObject(checkpoints.summary)).alignment_buffered

    return (
      <>
        {JSON.stringify(job?.jobHistory?.checkpoints).includes("errors") ?
          <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/> :
          <Descriptions bordered size="small" column={1}>
            <Descriptions.Item label="End to End Duration">
              <Tag color="blue" title={"Max"}>
                <RocketOutlined/> Max: {parseSecondStr(end_to_end_duration.max)}
              </Tag>
              <Tag color="green" title={"Min"}>
                <RocketOutlined/> Min: {parseSecondStr(end_to_end_duration.min)}
              </Tag>
              <Tag color="orange" title={"Avg"}>
                <RocketOutlined/> Avg: {parseSecondStr(end_to_end_duration.avg)}
              </Tag>
            </Descriptions.Item>

            <Descriptions.Item label="Checkpointed Data Size">
              <Tag color="blue" title={"Max"}>
                <RocketOutlined/> Max: {parseByteStr(state_size.max)}
              </Tag>
              <Tag color="green" title={"Min"}>
                <RocketOutlined/> Min: {parseByteStr(state_size.min)}
              </Tag>
              <Tag color="orange" title={"Avg"}>
                <RocketOutlined/> Avg: {parseByteStr(state_size.avg)}
              </Tag>
            </Descriptions.Item>

            <Descriptions.Item label="Processed (persisted) in-flight data">
              <Tag color="blue" title={"Max"}>
                <RocketOutlined/> Max: {processed_data.max}
              </Tag>
              <Tag color="green" title={"Min"}>
                <RocketOutlined/> Min: {processed_data.min}
              </Tag>
              <Tag color="orange" title={"Avg"}>
                <RocketOutlined/> Avg: {processed_data.avg}
              </Tag>
            </Descriptions.Item>

            <Descriptions.Item label="Persisted data">
              <Tag color="blue" title={"Max"}>
                <RocketOutlined/> Max: {persisted_data.max}
              </Tag>
              <Tag color="green" title={"Min"}>
                <RocketOutlined/> Min: {persisted_data.min}
              </Tag>
              <Tag color="orange" title={"Avg"}>
                <RocketOutlined/> Avg: {persisted_data.avg}
              </Tag>
            </Descriptions.Item>

            <Descriptions.Item label="Alignment Buffered">
              <Tag color="blue" title={"Max"}>
                <RocketOutlined/> Max: {alignment_buffered.max}
              </Tag>
              <Tag color="green" title={"Min"}>
                <RocketOutlined/> Min: {alignment_buffered.min}
              </Tag>
              <Tag color="orange" title={"Avg"}>
                <RocketOutlined/> Avg: {alignment_buffered.avg}
              </Tag>
            </Descriptions.Item>
          </Descriptions>
        }
      </>
    )
  }


  function recoveryCheckPoint(row: CheckPointsDetailInfo) {
    Modal.confirm({
      title: 'Recovery Of CheckPoint',
      content: `确定从 CheckPoint 【${row.external_path}】恢复吗？`,
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        const res = selectSavePointRestartTask(job?.instance?.taskId, job?.instance?.step == JOB_LIFE_CYCLE.ONLINE, row.external_path);
        res.then((result) => {
          if (result.code == CODE.SUCCESS) {
            message.success("恢复作业成功");
            history.goBack();
          } else {
            message.error("恢复作业失败");
          }
        });
      }
    });
  }

  const getHistory = (checkpoints: any) => {

    const checkPointsList: CheckPointsDetailInfo[] = [];
    checkpoints?.history?.forEach((entity: CheckPointsDetailInfo) => {
        return checkPointsList.push({
          jobID: job?.id,
          historyID: job?.jobHistory.id,
          id: entity.id,
          status: entity.status,
          end_to_end_duration: entity.end_to_end_duration,
          external_path: entity.external_path,
          latest_ack_timestamp: entity.latest_ack_timestamp,
          state_size: entity.state_size,
          trigger_timestamp: entity.trigger_timestamp,
        });
      }
    );

    const columns: ProColumns<CheckPointsDetailInfo>[] = [
      {
        title: 'ID',
        align: 'center',
        dataIndex: 'id',
      },
      {
        title: '状态',
        align: 'center',
        copyable: true,
        render: (dom, entity) => {
          if (entity.status === 'COMPLETED') {
            return <Tag icon={<CheckCircleOutlined/>} color="success">{entity.status}</Tag>
          }
          if (entity.status === 'IN_PROGRESS') {
            return <Tag icon={<SyncOutlined spin/>} color="processing">{entity.status}</Tag>
          }
          if (entity.status === 'FAILED') {
            return <Tag icon={<CloseCircleOutlined/>} color="error">{entity.status}</Tag>
          }
          return <Tag icon={<MinusCircleOutlined/>} color="default">{entity.status}</Tag>
        },
      },
      {
        title: '耗时',
        align: 'center',
        copyable: true,
        render: (dom, entity) => {
          return entity.end_to_end_duration === null ? 'None' : parseMilliSecondStr(entity.end_to_end_duration);
        },
      },
      {
        title: '存储位置',
        align: 'center',
        copyable: true,
        dataIndex: 'external_path',
      },
      {
        title: '最后响应时间',
        align: 'center',
        dataIndex: 'latest_ack_timestamp',
        valueType: 'dateTime',
      },
      {
        title: '状态大小',
        align: 'center',
        render: (dom, entity) => {
          return entity.state_size === null ? 'None' : parseByteStr(entity.state_size);
        },
      },
      {
        title: '触发时间',
        align: 'center',
        valueType: 'dateTime',
        dataIndex: 'trigger_timestamp',
      },
      {
        title: '操作',
        align: 'center',
        render: (dom, entity) => {
          return <>
            {entity.status === 'COMPLETED' ?
              <Button onClick={() => recoveryCheckPoint(entity)}>此处恢复</Button> : undefined}
          </>
        },
      },
    ];


    return (
      <>
        <ProTable<CheckPointsDetailInfo>
          columns={columns}
          style={{width: '100%'}}
          dataSource={checkPointsList}
          onDataSourceChange={(dataSource) => {
            actionRef.current?.reload();
          }}
          actionRef={actionRef}
          rowKey="id"
          pagination={{
            pageSize: 10,
          }}
          toolBarRender={false}
          dateFormatter="string"
          search={false}
          size="small"
        />
      </>
    )
  }


  const getConfiguration = (checkpointsConfig: any) => {
    let checkpointsConfigInfo = JsonParseObject(checkpointsConfig)
    return (
      <>
        {JSON.stringify(job?.jobHistory?.checkpointsConfig).includes("errors") ?
          <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/> :
          <Descriptions bordered size="small" column={1}>
            <Descriptions.Item label="Checkpointing Mode">
              <Tag color="blue" title={"Checkpointing Mode"}>
                {checkpointsConfigInfo.mode.toUpperCase()}
              </Tag>
            </Descriptions.Item>

            <Descriptions.Item label="Checkpoint Storage">
              <Tag color="blue" title={"Checkpoint Storage"}>
                {checkpointsConfigInfo.checkpoint_storage ? checkpointsConfigInfo.checkpoint_storage : 'Disabled'}
              </Tag>
            </Descriptions.Item>

            <Descriptions.Item label="State Backend">
              <Tag color="blue" title={"State Backend"}>
                {checkpointsConfigInfo.state_backend ? checkpointsConfigInfo.state_backend : 'Disabled'}
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

            <Descriptions.Item label="Minimum Pause Between Checkpoints">
              <Tag color="blue" title={"Minimum Pause Between Checkpoints"}>
                {checkpointsConfigInfo.min_pause}
              </Tag>
            </Descriptions.Item>

            <Descriptions.Item label="Maximum Concurrent Checkpoints">
              <Tag color="blue" title={"Maximum Concurrent Checkpoints"}>
                {checkpointsConfigInfo.max_concurrent}
              </Tag>
            </Descriptions.Item>

            <Descriptions.Item label="Unaligned Checkpoints ">
              <Tag color="blue" title={"Unaligned Checkpoints"}>
                {checkpointsConfigInfo.unaligned_checkpoints ? 'Enabled' : 'Disabled'}
              </Tag>
            </Descriptions.Item>

            <Descriptions.Item label="Persist Checkpoints Externally Enabled">
              <Tag color="blue" title={"Persist Checkpoints Externally Enabled"}>
                {JsonParseObject(checkpointsConfigInfo.externalization).enabled ? 'Enabled' : 'Disabled'}
              </Tag>
            </Descriptions.Item>
            {JsonParseObject(checkpointsConfigInfo.externalization).enabled && (
              <Descriptions.Item label="Delete On Cancellation">
                <Tag color="blue" title={"Delete On Cancellation"}>
                  {JsonParseObject(checkpointsConfigInfo.externalization).delete_on_cancellation ? 'Enabled' : 'Disabled'}
                </Tag>
              </Descriptions.Item>
            )}


            <Descriptions.Item label="Tolerable Failed Checkpoints">
              <Tag color="blue" title={"Tolerable Failed Checkpoints"}>
                {checkpointsConfigInfo.tolerable_failed_checkpoints}
              </Tag>
            </Descriptions.Item>
          </Descriptions>
        }
      </>
    )
  }


  function getSavePoint() {
    const url = '/api/savepoints';

    const columns: ProColumns<SavePointTableListItem>[] = [
      {
        title: 'ID',
        align: 'center',
        dataIndex: 'id',
        hideInTable: true,
      },
      {
        title: '任务ID',
        align: 'center',
        dataIndex: 'taskId',
        hideInTable: true,
      },
      {
        title: '名称',
        align: 'center',
        dataIndex: 'name',
      },
      {
        title: '类型',
        align: 'center',
        dataIndex: 'type',
      },
      {
        title: '存储位置',
        align: 'center',
        copyable: true,
        dataIndex: 'path',
      },
      {
        title: '触发时间',
        align: 'center',
        valueType: 'dateTime',
        dataIndex: 'createTime',
      },
    ];


    return (
      <>
        <ProTable<SavePointTableListItem>
          columns={columns}
          style={{width: '100%'}}
          request={(params, sorter, filter) => queryData(url, {
            taskId: job?.instance.taskId, ...params,
            sorter,
            filter
          })}
          actionRef={actionRef}
          rowKey="id"
          pagination={{
            pageSize: 10,
          }}
          search={false}
          size="small"
        />
      </>
    )
  }

  return (<>
    {(job?.jobHistory?.checkpoints || job?.jobHistory?.checkpointsConfig) &&
    <Tabs defaultActiveKey="overview" size="small" tabPosition="top" style={{
      border: "1px solid #f0f0f0",
    }}>
      <TabPane tab={<span>&nbsp; Overview &nbsp;</span>} key="overview">
        {!JSON.stringify(job?.jobHistory?.checkpoints).includes("errors") ?
          getOverview(JsonParseObject(job?.jobHistory?.checkpoints)) :
          <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>}
      </TabPane>

      <TabPane tab={<span>&nbsp; History &nbsp;</span>} key="history">
        {getHistory(JsonParseObject(job?.jobHistory?.checkpoints))}
      </TabPane>

      <TabPane tab={<span>&nbsp; Summary &nbsp;</span>} key="summary">
        {!JSON.stringify(job?.jobHistory?.checkpoints).includes("errors") ?
          getSummary(JsonParseObject(job?.jobHistory?.checkpoints)) :
          <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>
        }
      </TabPane>

      <TabPane tab={<span>&nbsp; Configuration &nbsp;</span>} key="configuration">
        {!JSON.stringify(job?.jobHistory?.checkpointsConfig).includes("errors") ?
          getConfiguration(JsonParseObject(job?.jobHistory?.checkpointsConfig)) :
          <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>
        }
      </TabPane>

      <TabPane tab={<span>&nbsp; SavePoint &nbsp;</span>} key="savepoint">
        {getSavePoint()}
      </TabPane>
    </Tabs>}
  </>)
};
export default CheckPoints;

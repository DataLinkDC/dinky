import {Button, Descriptions, Empty, Modal, Tabs, Tag} from 'antd';
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

const {TabPane} = Tabs;

const CheckPoints = (props: any) => {

  const {job} = props;
  const actionRef = useRef<ActionType>();
  const getOverview = () => {
    return (
      <>
        {(job?.jobHistory?.checkpoints) ?
          <Descriptions bordered size="small" column={1}>
            <Descriptions.Item label="CheckPoint Counts">
              <Tag color="blue" title={"Total"}>
                <RocketOutlined/> Total: {job?.jobHistory?.checkpoints['counts']['total']}
              </Tag>
              <Tag color="red" title={"Failed"}>
                <CloseCircleOutlined/> Failed: {job?.jobHistory?.checkpoints['counts']['failed']}
              </Tag>
              <Tag color="cyan" title={"Restored"}>
                <ExclamationCircleOutlined/> Restored: {job?.jobHistory?.checkpoints['counts']['restored']}
              </Tag>
              <Tag color="green" title={"Completed"}>
                <CheckCircleOutlined/> Completed: {job?.jobHistory?.checkpoints['counts']['completed']}
              </Tag>
              <Tag color="orange" title={"In Progress"}>
                <SyncOutlined spin/> In Progress: {job?.jobHistory?.checkpoints['counts']['in_progress']}
              </Tag>
            </Descriptions.Item>

            <Descriptions.Item label="Latest Completed CheckPoint">
              <Tag color="green" title={"Latest Completed CheckPoint"}>
                {job?.jobHistory?.checkpoints['latest']['completed'] === null ? 'None' :
                  job?.jobHistory?.checkpoints['latest']['completed']['external_path']
                }
              </Tag>
            </Descriptions.Item>

            <Descriptions.Item label="Latest Failed CheckPoint">
              <Tag color="red" title={"Latest Failed CheckPoint"}>
                {job?.jobHistory?.checkpoints['latest']['failed'] === null ? 'None' :
                  job?.jobHistory?.checkpoints['latest']['failed']['external_path']
                }
              </Tag>
            </Descriptions.Item>

            <Descriptions.Item label="Latest Restored">
              <Tag color="cyan" title={"Latest Restored"}>
                {job?.jobHistory?.checkpoints['latest']['restored'] === null ? 'None' :
                  job?.jobHistory?.checkpoints['latest']['restored']['external_path']}
              </Tag>
            </Descriptions.Item>

            <Descriptions.Item label="Latest Savepoint">
              <Tag color="purple" title={"Latest Savepoint"}>
                {job?.jobHistory?.checkpoints['latest']['savepoint'] === null ? 'None' :
                  job?.jobHistory?.checkpoints['latest']['savepoint']['external_path']
                }
              </Tag>
            </Descriptions.Item>

          </Descriptions>
          : <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>
        }
      </>
    )
  }

  const getSummary = () => {


    return (
      <>
        {(job?.jobHistory?.checkpoints) ?
          <Descriptions bordered size="small" column={1}>
            <Descriptions.Item label="End to End Duration">
              <Tag color="blue" title={"Max"}>
                <RocketOutlined/> Max: {parseSecondStr(job?.jobHistory?.checkpoints['summary']['end_to_end_duration']['max'])}
              </Tag>
              <Tag color="green" title={"Min"}>
                <RocketOutlined/> Min: {parseSecondStr(job?.jobHistory?.checkpoints['summary']['end_to_end_duration']['min'])}
              </Tag>
              <Tag color="orange" title={"Avg"}>
                <RocketOutlined/> Avg: {parseSecondStr(job?.jobHistory?.checkpoints['summary']['end_to_end_duration']['avg'])}
              </Tag>
            </Descriptions.Item>

            <Descriptions.Item label="Checkpointed Data Size">
              <Tag color="blue" title={"Max"}>
                <RocketOutlined/> Max: {parseByteStr(job?.jobHistory?.checkpoints['summary']['state_size']['max'])}
              </Tag>
              <Tag color="green" title={"Min"}>
                <RocketOutlined/> Min: {parseByteStr(job?.jobHistory?.checkpoints['summary']['state_size']['min'])}
              </Tag>
              <Tag color="orange" title={"Avg"}>
                <RocketOutlined/> Avg: {parseByteStr(job?.jobHistory?.checkpoints['summary']['state_size']['avg'])}
              </Tag>
            </Descriptions.Item>

            <Descriptions.Item label="Processed (persisted) in-flight data">
              <Tag color="blue" title={"Max"}>
                <RocketOutlined/> Max: {job?.jobHistory?.checkpoints['summary']['processed_data']['max']}
              </Tag>
              <Tag color="green" title={"Min"}>
                <RocketOutlined/> Min: {job?.jobHistory?.checkpoints['summary']['processed_data']['min']}
              </Tag>
              <Tag color="orange" title={"Avg"}>
                <RocketOutlined/> Avg: {job?.jobHistory?.checkpoints['summary']['processed_data']['avg']}
              </Tag>
            </Descriptions.Item>

            <Descriptions.Item label="Persisted data">
              <Tag color="blue" title={"Max"}>
                <RocketOutlined/> Max: {job?.jobHistory?.checkpoints['summary']['persisted_data']['max']}
              </Tag>
              <Tag color="green" title={"Min"}>
                <RocketOutlined/> Min: {job?.jobHistory?.checkpoints['summary']['persisted_data']['min']}
              </Tag>
              <Tag color="orange" title={"Avg"}>
                <RocketOutlined/> Avg: {job?.jobHistory?.checkpoints['summary']['persisted_data']['avg']}
              </Tag>
            </Descriptions.Item>

            <Descriptions.Item label="Alignment Buffered">
              <Tag color="blue" title={"Max"}>
                <RocketOutlined/> Max: {job?.jobHistory?.checkpoints['summary']['alignment_buffered']['max']}
              </Tag>
              <Tag color="green" title={"Min"}>
                <RocketOutlined/> Min: {job?.jobHistory?.checkpoints['summary']['alignment_buffered']['min']}
              </Tag>
              <Tag color="orange" title={"Avg"}>
                <RocketOutlined/> Avg: {job?.jobHistory?.checkpoints['summary']['alignment_buffered']['avg']}
              </Tag>
            </Descriptions.Item>
          </Descriptions>
          : <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>
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
        // TODO: handleRecoveryCheckPoint
        // await handleRecoveryCheckPoint('api/task/recoveryCheckPoint', [row]);
        actionRef.current?.reload?.();
      }
    });
  }

  const getHistory = () => {

    const checkPointsList: CheckPointsDetailInfo[] = [];
    job?.jobHistory?.checkpoints['history']?.forEach((entity: CheckPointsDetailInfo) => {
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
              <Button disabled title="暂不可用" onClick={() => recoveryCheckPoint(entity)}>此处恢复</Button> : undefined}
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


  const getConfigraution = () => {

    return (
      <>
        {(job?.jobHistory?.checkpointsConfig) ?
          <Descriptions bordered size="small" column={1}>
            <Descriptions.Item label="Checkpointing Mode">
              <Tag color="blue" title={"Checkpointing Mode"}>
                {job?.jobHistory?.checkpointsConfig['mode'].toUpperCase()}
              </Tag>
            </Descriptions.Item>

            <Descriptions.Item label="Checkpoint Storage">
              <Tag color="blue" title={"Checkpoint Storage"}>
                {job?.jobHistory?.checkpointsConfig['checkpoint_storage'] ? job?.jobHistory?.checkpointsConfig['checkpoint_storage'] : 'Disabled'}
              </Tag>
            </Descriptions.Item>

            <Descriptions.Item label="State Backend">
              <Tag color="blue" title={"State Backend"}>
                {job?.jobHistory?.checkpointsConfig['state_backend'] ? job?.jobHistory?.checkpointsConfig['state_backend'] : 'Disabled'}
              </Tag>
            </Descriptions.Item>

            <Descriptions.Item label="Interval">
              <Tag color="blue" title={"Interval"}>
                {job?.jobHistory?.checkpointsConfig['interval']}
              </Tag>
            </Descriptions.Item>

            <Descriptions.Item label="Timeout">
              <Tag color="blue" title={"Timeout"}>
                {(job?.jobHistory?.checkpointsConfig['timeout'])}
              </Tag>
            </Descriptions.Item>

            <Descriptions.Item label="Minimum Pause Between Checkpoints">
              <Tag color="blue" title={"Minimum Pause Between Checkpoints"}>
                {(job?.jobHistory?.checkpointsConfig['min_pause'])}
              </Tag>
            </Descriptions.Item>

            <Descriptions.Item label="Maximum Concurrent Checkpoints">
              <Tag color="blue" title={"Maximum Concurrent Checkpoints"}>
                {job?.jobHistory?.checkpointsConfig['max_concurrent']}
              </Tag>
            </Descriptions.Item>

            <Descriptions.Item label="Unaligned Checkpoints ">
              <Tag color="blue" title={"Unaligned Checkpoints"}>
                {job?.jobHistory?.checkpointsConfig['unaligned_checkpoints'] ? 'Enabled' : 'Disabled'}
              </Tag>
            </Descriptions.Item>

            <Descriptions.Item label="Persist Checkpoints Externally Enabled">
              <Tag color="blue" title={"Persist Checkpoints Externally Enabled"}>
                {job?.jobHistory?.checkpointsConfig['externalization']['enabled'] ? 'Enabled' : 'Disabled'}
              </Tag>
            </Descriptions.Item>
            {job?.jobHistory?.checkpointsConfig['externalization']['enabled'] && (
              <Descriptions.Item label="Delete On Cancellation">
                <Tag color="blue" title={"Delete On Cancellation"}>
                  {job?.jobHistory?.checkpointsConfig['externalization']['delete_on_cancellation'] ? 'Enabled' : 'Disabled'}
                </Tag>
              </Descriptions.Item>
            )}


            <Descriptions.Item label="Tolerable Failed Checkpoints">
              <Tag color="blue" title={"Tolerable Failed Checkpoints"}>
                {job?.jobHistory?.checkpointsConfig['tolerable_failed_checkpoints']}
              </Tag>
            </Descriptions.Item>
          </Descriptions>
          : <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>
        }
      </>
    )
  }


  return (<>
    <Tabs defaultActiveKey="overview" size="small" tabPosition="top" style={{
      border: "1px solid #f0f0f0",
    }}>
      <TabPane tab={<span>&nbsp; Overview &nbsp;</span>} key="overview">
        {getOverview()}
      </TabPane>

      <TabPane tab={<span>&nbsp; History &nbsp;</span>} key="history">
        {getHistory()}
      </TabPane>

      <TabPane tab={<span>&nbsp; Summary &nbsp;</span>} key="summary">
        {getSummary()}
      </TabPane>

      <TabPane tab={<span>&nbsp; Configraution &nbsp;</span>} key="configraution">
        {getConfigraution()}
      </TabPane>
    </Tabs>
  </>)
};

export default CheckPoints;

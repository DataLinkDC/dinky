import {StateType} from "@/pages/FlinkSqlStudio/model";
import {connect} from "umi";
import {Button, Tag, Space,Typography, Divider, Badge,Drawer,} from 'antd';
import {MessageOutlined,ClusterOutlined,FireOutlined,ReloadOutlined} from "@ant-design/icons";
import { LightFilter, ProFormDatePicker } from '@ant-design/pro-form';
import ProList from '@ant-design/pro-list';
import request from 'umi-request';
import {handleRemove, queryData} from "@/components/Common/crud";
import ProDescriptions from '@ant-design/pro-descriptions';
import {useRef, useState} from "react";
import {DocumentTableListItem} from "@/pages/Document/data";
import ProForm, {
  ModalForm,
  ProFormText,
  ProFormDateRangePicker,
  ProFormSelect,
} from '@ant-design/pro-form';
import styles from "./index.less";
import {ActionType} from "@ant-design/pro-table";
import {showJobData} from "@/components/Studio/StudioEvent/DQL";


const { Title, Paragraph, Text, Link } = Typography;

type HistoryItem = {
  id: number;
  clusterId: number;
  clusterAlias: string;
  session: string;
  jobId: string;
  jobName: string;
  jobManagerAddress: string;
  statusText: string;
  status: number;
  statement: string;
  error: string;
  result: string;
  config: string;
  startTime: string;
  endTime: string;
  taskId: number;
  taskAlias: string;
};

type HistoryConfig={
  useSession:boolean;
  session:string;
  useRemote:boolean;
  clusterId:number;
  host:string;
  useResult:boolean;
  maxRowNum:number;
  taskId:number;
  jobName:string;
  useSqlFragment:boolean;
  checkpoint:number;
  parallelism:number;
  savePointPath:string;
};
const url = '/api/history';
const StudioHistory = (props: any) => {

  const {current,refs,dispatch} = props;
  const [modalVisit, setModalVisit] = useState(false);
  const [row, setRow] = useState<HistoryItem>();
  const [config,setConfig] = useState<HistoryConfig>();
  const [type,setType] = useState<number>();

  const showDetail=(row:HistoryItem,type:number)=>{
    setRow(row);
    setModalVisit(true);
    setType(type);
    setConfig(JSON.parse(row.config));
  };

  const removeHistory=async (row:HistoryItem)=>{
    await handleRemove(url,[row]);
    // refs.current?.reloadAndRest?.();
    refs.history?.current?.reload();
  };

  return (
    <>
      <ProList<HistoryItem>
        actionRef={refs.history}
      toolBarRender={() => {
      return [
       // <Button key="3" type="primary"  icon={<ReloadOutlined />}/>,
      ];
    }}
      search={{
      filterType: 'light',
    }}
      rowKey="id"
      headerTitle="执行历史"
      request={(params, sorter, filter) => queryData(url,{...params, sorter:{id:'descend'}, filter})}
      pagination={{
      pageSize: 5,
    }}
      showActions="hover"
      metas={{
      title: {
        dataIndex: 'jobId',
        title: 'JobId',
        render: (_, row) => {
          return (
            <Space size={0}>
              <Tag color="blue" key={row.jobId}>
                <FireOutlined /> {row.jobId}
              </Tag>
            </Space>
          );
        },
      },
      description: {
        search: false,
        render:(_, row)=>{
          let jobConfig =  JSON.parse(row.config);
          return (<Paragraph>
            <blockquote>
              <Link href={`http://${jobConfig.host}`} target="_blank">
              [{jobConfig.session}:{row.jobManagerAddress}]
              </Link>
              <Divider type="vertical"/>开始于：{row.startTime}
              <Divider type="vertical"/>完成于：{row.endTime}
            </blockquote>
          </Paragraph>)
        }
        },
      subTitle: {
        render: (_, row) => {
          return (
            <Space size={0}>
              {row.jobName?(
                <Tag color="gray" key={row.jobName}>
                  {row.jobName}
                </Tag>
              ):''}
              {row.session?(
                <Tag color="orange" key={row.session}>
                  <MessageOutlined /> {row.session}
                </Tag>
              ):''}
              {row.clusterAlias?(
                <Tag color="green" key={row.clusterAlias}>
                  <ClusterOutlined /> {row.clusterAlias}
                </Tag>
              ):(<Tag color="blue" key={row.clusterAlias}>
                <ClusterOutlined /> 本地环境
              </Tag>)}
              {(row.status==2) ?
                (<><Badge status="success"/><Text type="success">SUCCESS</Text></>):
                (row.status==1) ?
                  <><Badge status="error"/><Text type="secondary">RUNNING</Text></> :
                  (row.status==3) ?
                    <><Badge status="error"/><Text type="danger">FAILED</Text></> :
                    (row.status==4) ?
                      <><Badge status="error"/><Text type="warning">CANCEL</Text></> :
                      (row.status==0) ?
                        <><Badge status="error"/><Text type="warning">INITIALIZE</Text></> :
                        <><Badge status="success"/><Text type="danger">UNKNOWEN</Text></>}
            </Space>
          );
        },
        search: false,
      },
      actions: {
        render: (text, row) => [
          <a key="config" onClick={()=>{showDetail(row,1)}}>
            执行配置
          </a>,
          <a key="statement" onClick={()=>{showDetail(row,2)}}>
            FlinkSql语句
          </a>,
          <a key="result" onClick={()=>{showJobData(row.jobId,dispatch)}}>
            预览数据
          </a>,
          <a key="error" onClick={()=>{showDetail(row,4)}}>
            异常信息
          </a>,
          <a key="delete" onClick={()=>{removeHistory(row)}}>
            删除
          </a>,
        ],
        search: false,
      },
      jobName: {
        dataIndex: 'jobName',
        title: '作业名',
      },
      clusterId: {
        dataIndex: 'clusterId',
        title: '执行方式',
      },
      session: {
        dataIndex: 'session',
        title: '共享会话',
      },
      status: {
        // 自己扩展的字段，主要用于筛选，不在列表中显示
        title: '状态',
        valueType: 'select',
        valueEnum: {
          ALL: {text: '全部', status: 'ALL'},
          INITIALIZE: {
            text: '初始化',
            status: 'INITIALIZE',
          },
          RUNNING: {
            text: '运行中',
            status: 'RUNNING',
          },
          SUCCESS: {
            text: '成功',
            status: 'SUCCESS',
          },
          FAILED: {
            text: '失败',
            status: 'FAILED',
          },
          CANCEL: {
            text: '停止',
            status: 'CANCEL',
          },
        },
      },
      startTime: {
        dataIndex: 'startTime',
        title: '开始时间',
        valueType: 'dateTimeRange',
      },
      endTime: {
        dataIndex: 'endTime',
        title: '完成时间',
        valueType: 'dateTimeRange',
      },
    }}
      options={{
      search: false,
      setting:false
    }}
      />
        <ModalForm
          // title="新建表单"
          visible={modalVisit}
          onFinish={async () => {
            setRow(undefined);
            setType(undefined);
            setConfig(undefined);
          }}
          onVisibleChange={setModalVisit}
          submitter={{
            submitButtonProps: {
              style: {
                display: 'none',
              },
            },
          }}
          >
          {type==1 && (
            <ProDescriptions
              column={2}
              title='执行配置'
            >
              <ProDescriptions.Item span={2} label="JobId" >
                {row.jobId}
              </ProDescriptions.Item>
              <ProDescriptions.Item label="共享会话" >
                {config.useSession?'启用':'禁用'}
              </ProDescriptions.Item>
              <ProDescriptions.Item label="会话 Key">
                {config.session}
              </ProDescriptions.Item>
              <ProDescriptions.Item label="执行方式" >
                {config.useRemote?'远程':'本地'}
              </ProDescriptions.Item>
              <ProDescriptions.Item label="集群ID">
                {config.clusterId}
              </ProDescriptions.Item>
              <ProDescriptions.Item label="预览结果" >
                {config.useResult?'启用':'禁用'}
              </ProDescriptions.Item>
              <ProDescriptions.Item label="最大行数">
                {config.maxRowNum}
              </ProDescriptions.Item>
              <ProDescriptions.Item span={2} label="JobManagerAddress">
                {row.jobManagerAddress}
              </ProDescriptions.Item>
              <ProDescriptions.Item label="作业ID">
                {config.taskId}
              </ProDescriptions.Item>
              <ProDescriptions.Item label="作业名">
                {config.jobName}
              </ProDescriptions.Item>
              <ProDescriptions.Item label="片段机制">
                {config.useSqlFragment?'启用':'禁用'}
              </ProDescriptions.Item>
              <ProDescriptions.Item label="并行度">
                {config.parallelism}
              </ProDescriptions.Item>
              <ProDescriptions.Item label="CheckPoint">
                {config.checkpoint}
              </ProDescriptions.Item>
              <ProDescriptions.Item label="SavePointPath">
                {config.savePointPath}
              </ProDescriptions.Item>
            </ProDescriptions>
          )}
          {type==2 && (
            <ProDescriptions
              column={1}
              title='FlinkSql 语句'
            >
              <ProDescriptions.Item label="JobId" >
                {row.jobId}
              </ProDescriptions.Item>
              <ProDescriptions.Item>
                <pre className={styles.code}>{row.statement}</pre>
              </ProDescriptions.Item>
            </ProDescriptions>
          )}
          {type==4 && (
            <ProDescriptions
              column={1}
              title='异常信息'
            >
              <ProDescriptions.Item label="JobId" >
                {row.jobId}
              </ProDescriptions.Item>
              <ProDescriptions.Item >
                <pre className={styles.code}>{row.error}</pre>
              </ProDescriptions.Item>
            </ProDescriptions>
          )}
          </ModalForm>
    </>
  );
};

export default connect(({Studio}: {Studio: StateType}) => ({
  current: Studio.current,
  refs: Studio.refs,
}))(StudioHistory);

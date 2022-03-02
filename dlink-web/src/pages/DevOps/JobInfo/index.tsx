import React, {useEffect, useState} from 'react';
import {history, useLocation} from 'umi';
import {
  EllipsisOutlined, RedoOutlined,
  FireOutlined, ClusterOutlined, RocketOutlined
} from '@ant-design/icons';
import {Button, Dropdown, Menu, Tag, Space, Typography} from 'antd';
import {PageContainer} from '@ant-design/pro-layout';
import ProCard from '@ant-design/pro-card';
import {JobInfoDetail} from "@/pages/DevOps/data";
import {getJobInfoDetail, refreshJobInfoDetail} from "@/pages/DevOps/service";
import moment from "moment";
import BaseInfo from "@/pages/DevOps/JobInfo/BaseInfo";
import Config from "@/pages/DevOps/JobInfo/Config";
import JobStatus from "@/components/Common/JobStatus";

const {Link} = Typography;

const JobInfo = (props: any) => {

  const params = useLocation();
  const {} = props;
  const id = params.query.id;
  const [job, setJob] = useState<JobInfoDetail>();
  const [time, setTime] = useState(() => Date.now());
  const [tabKey, setTabKey] = useState<string>('base');

  const handleGetJobInfoDetail = () => {
    const res = getJobInfoDetail(id);
    res.then((result) => {
      setJob(result.datas);
      setTime(Date.now());
    });
  };

  useEffect(() => {
    handleGetJobInfoDetail();
    let dataPolling = setInterval(handleGetJobInfoDetail, 3000);
    return () => {
      clearInterval(dataPolling);
    };
  }, []);

  const handleRefreshJobInfoDetail = () => {
    const res = refreshJobInfoDetail(id);
    res.then((result) => {
      setJob(result.datas);
      setTime(Date.now());
    });
  };

  const handleBack = () => {
    history.goBack();
  };

  return (
    <PageContainer
      header={{
        title: `${job?.instance?.name}`,
        ghost: true,
        extra: [
          <Button key="back" type="dashed" onClick={handleBack}>返回</Button>,
          <Button key="refresh" icon={<RedoOutlined/>} onClick={handleRefreshJobInfoDetail}/>,
          <Button key="flinkwebui">
            <Link href={`http://${job?.history?.jobManagerAddress}/#/job/${job?.instance?.jid}/overview`} target="_blank">
              FlinkWebUI
            </Link></Button>,
          <Button key="autorestart" type="primary">智能重启</Button>,
          <Button key="autostop" type="primary" danger>智能停止</Button>,
          <Dropdown
            key="dropdown"
            trigger={['click']}
            overlay={
              <Menu>
                <Menu.Item key="1">普通停止</Menu.Item>
                <Menu.Item key="2">SavePoint停止</Menu.Item>
                <Menu.Item key="3">SavePoint暂停</Menu.Item>
              </Menu>
            }
          >
            <Button key="4" style={{padding: '0 8px'}}>
              <EllipsisOutlined/>
            </Button>
          </Dropdown>,
        ],
      }}
      content={<>
        <Space size={0}>
          {job?.instance?.jid ? (
            <Tag color="blue" key={job?.instance?.jid}>
              <FireOutlined/> {job?.instance?.jid}
            </Tag>
          ) : undefined}
          <JobStatus status={job?.instance?.status}/>
          {job?.history?.type ? (
            <Tag color="blue" key={job?.history?.type}>
              <RocketOutlined/> {job?.history?.type}
            </Tag>
          ) : undefined}
          {job?.cluster?.alias ? (
            <Tag color="green" key={job?.cluster?.alias}>
              <ClusterOutlined/> {job?.cluster?.alias}
            </Tag>
          ) : (<Tag color="green" key='local'>
            <ClusterOutlined/> 本地环境
          </Tag>)}
        </Space>
      </>}
      tabBarExtraContent={`上次更新时间：${moment(time).format('HH:mm:ss')}`}
      tabList={[
        {
          tab: '作业总览',
          key: 'base',
          closable: false,
        },
        {
          tab: '配置信息',
          key: 'config',
          closable: false,
        },
        {
          tab: 'FlinkSQL',
          key: 'flinksql',
          closable: false,
        },
        {
          tab: '集群信息',
          key: 'cluster',
          closable: false,
        },
        {
          tab: '作业快照',
          key: 'snapshot',
          closable: false,
        },
        {
          tab: '告警记录',
          key: 'alert',
          closable: false,
        },
      ]}
      onTabChange={(key) => {
        setTabKey(key);
      }}
    >
      <ProCard>
        {tabKey === 'base' ? <BaseInfo job={job}/> : undefined}
        {tabKey === 'config' ? <Config job={job}/> : undefined}
      </ProCard>
    </PageContainer>
  );
};

export default JobInfo;

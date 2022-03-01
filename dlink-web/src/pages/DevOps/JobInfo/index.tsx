import React, {useEffect, useState} from 'react';
import { history, useLocation } from 'umi';
import { EllipsisOutlined, CheckCircleOutlined,SyncOutlined,CloseCircleOutlined,MinusCircleOutlined,ClockCircleOutlined,
  FireOutlined,ClusterOutlined,RocketOutlined} from '@ant-design/icons';
import { Button, Dropdown, Menu, Tag, Space } from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import ProCard from '@ant-design/pro-card';
import {JobInfoDetail, StatusCount} from "@/pages/DevOps/data";
import {getJobInfoDetail, getStatusCount} from "@/pages/DevOps/service";
import moment from "moment";

const JobInfo = (props:any) => {

  const params = useLocation();
  const { } = props;
  const id = params.query.id;
  const [job, setJob] = useState<JobInfoDetail>();
  const [time, setTime] = useState(() => Date.now());

  const refreshJobInfoDetail = () => {
    const res = getJobInfoDetail(id);
    res.then((result)=>{
      setJob(result.datas);
      setTime(Date.now());
    });
  };

  useEffect(() => {
    refreshJobInfoDetail();
    let dataPolling = setInterval(refreshJobInfoDetail,3000);
    return () => {
      clearInterval(dataPolling);
    };
  }, []);

  const handleBack = () => {
    history.goBack();
  };

  return (
    <PageContainer
      header={{
        title: `${job?.instance.name}`,
        ghost: true,
        extra: [
          <Button key="back" type="dashed" onClick={handleBack}>返回</Button>,
          <Button key="flinkwebui">FlinkWebUI</Button>,
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
            <Button key="4" style={{ padding: '0 8px' }}>
              <EllipsisOutlined />
            </Button>
          </Dropdown>,
        ],
      }}
      content={<>
        <Space size={0}>
          {job?.instance.jid?(
            <Tag color="blue" key={job?.instance.jid}>
              <FireOutlined /> {job?.instance.jid}
            </Tag>
          ):undefined}
          {(job?.instance.status == 'FINISHED') ?
            (<Tag icon={<CheckCircleOutlined />} color="success">
              FINISHED
            </Tag>) :
            (job?.instance.status == 'RUNNING') ?
              (<Tag icon={<SyncOutlined spin />} color="processing">
                RUNNING
              </Tag>) :
              (job?.instance.status == 'FAILED') ?
                (<Tag icon={<CloseCircleOutlined />} color="error">
                  FAILED
                </Tag>) :
                (job?.instance.status == 'CANCELED') ?
                  (<Tag icon={<MinusCircleOutlined />} color="default">
                    CANCELED
                  </Tag>) :
                  (job?.instance.status == 'INITIALIZING') ?
                    (<Tag icon={<ClockCircleOutlined />} color="default">
                      INITIALIZING
                    </Tag>) :(job?.instance.status == 'RESTARTING') ?
                      (<Tag icon={<ClockCircleOutlined />} color="default">
                        RESTARTING
                      </Tag>) :
                      (<Tag color="default">
                        UNKNOWEN
                      </Tag>)
          }
          {job?.history.type?(
            <Tag color="blue" key={job?.history.type}>
              <RocketOutlined /> {job?.history.type}
            </Tag>
          ):undefined}
          {job?.cluster.alias?(
            <Tag color="green" key={job?.cluster.alias}>
              <ClusterOutlined /> {job?.cluster.alias}
            </Tag>
          ):(<Tag color="green" key='local'>
            <ClusterOutlined /> 本地环境
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
    >
      <ProCard direction="column" ghost gutter={[0, 16]}>
        <ProCard style={{ height: 200 }} />
        <ProCard gutter={16} ghost style={{ height: 200 }}>
          <ProCard colSpan={16} />
          <ProCard colSpan={8} />
        </ProCard>
      </ProCard>
    </PageContainer>
  );
};

export default JobInfo;

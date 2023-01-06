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


import {useEffect, useState} from 'react';
import {history, useLocation} from 'umi';
import {ClusterOutlined, EllipsisOutlined, FireOutlined, RedoOutlined, RocketOutlined} from '@ant-design/icons';
import {Button, Dropdown, Empty, Menu, message, Modal, Space, Tag, Typography} from 'antd';
import {PageContainer} from '@ant-design/pro-layout';
import ProCard from '@ant-design/pro-card';
import {JobInfoDetail} from "@/pages/DevOps/data";
import {getJobInfoDetail, refreshJobInfoDetail} from "@/pages/DevOps/service";
import moment from "moment";
import BaseInfo from "@/pages/DevOps/JobInfo/BaseInfo";
import Config from "@/pages/DevOps/JobInfo/Config";
import JobStatus, {isStatusDone} from "@/components/Common/JobStatus";
import {cancelJob, offLineTask, restartJob} from "@/components/Studio/StudioEvent/DDL";
import {CODE} from "@/components/Common/crud";
import JobLifeCycle, {JOB_LIFE_CYCLE} from "@/components/Common/JobLifeCycle";
import Exception from "@/pages/DevOps/JobInfo/Exception";
import FlinkSQL from "@/pages/DevOps/JobInfo/FlinkSQL";
import Alert from "@/pages/DevOps/JobInfo/Alert";
import DataMap from "@/pages/DevOps/JobInfo/DataMap";
import CheckPoints from "@/pages/DevOps/JobInfo/CheckPoints";
import FlinkClusterInfo from "@/pages/DevOps/JobInfo/FlinkClusterInfo";
import TaskVersionInfo from "@/pages/DevOps/JobInfo/Version";
import {l} from "@/utils/intl";


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

  const handleSavepoint = (key: string) => {
    if (key == 'canceljob') {
      Modal.confirm({
        title: l('pages.devops.jobinfo.stopJob'),
        content: l('pages.devops.jobinfo.stopJobConfirm'),
        okText: l('button.confirm'),
        cancelText: l('button.cancel'),
        onOk: async () => {
          if (!job?.cluster?.id) return;
          const res = cancelJob(job?.cluster?.id, job?.instance?.jid);
          res.then((result) => {
            if (result.code == CODE.SUCCESS) {
              message.success(l('pages.devops.jobinfo.canceljob.success'));
              handleGetJobInfoDetail();
            } else {
              message.error(l('pages.devops.jobinfo.canceljob.failed'));
            }
          });
        }
      });
      return;
    }
    Modal.confirm({
      title: l('pages.devops.jobinfo.job.key','',{key:key}),
      content: l('pages.devops.jobinfo.job.keyConfirm','',{key:key}),
      okText: l('button.confirm'),
      cancelText: l('button.cancel'),
      onOk: async () => {
        if (!job?.cluster?.id) return;
        const res = offLineTask(job?.instance?.taskId, key);
        res.then((result) => {
          if (result.code == CODE.SUCCESS) {
            message.success(l('pages.devops.jobinfo.job.key.success','',{key: key}));
            handleGetJobInfoDetail();
          } else {
            message.error(l('pages.devops.jobinfo.job.key.failed','',{key: key}));
          }
        });
      }
    });
  };

  const handleRestart = () => {
    Modal.confirm({
      title: l('pages.devops.jobinfo.reonlineJob'),
      content: l('pages.devops.jobinfo.reonlineJobConfirm'),
      okText: l('button.confirm'),
      cancelText: l('button.cancel'),
      onOk: async () => {
        if (!job?.cluster?.id) return;
        const res = restartJob(job?.instance?.taskId, job?.instance?.step == JOB_LIFE_CYCLE.ONLINE);
        res.then((result) => {
          if (result.code == CODE.SUCCESS) {
            message.success(l('pages.devops.jobinfo.reonline.success'));
          } else {
            message.error(l('pages.devops.jobinfo.reonline.failed'));
          }
        });
      }
    });
  };

  const getButtons = () => {
    let buttons = [
      <Button key="back" type="dashed" onClick={handleBack}>{l('button.back')}</Button>,
    ];
    buttons.push(<Button key="refresh" icon={<RedoOutlined/>} onClick={handleRefreshJobInfoDetail}/>);
    buttons.push(<Button key="flinkwebui">
      <Link href={`http://${job?.history?.jobManagerAddress}/#/job/${job?.instance?.jid}/overview`} target="_blank">
        FlinkWebUI
      </Link></Button>);
    buttons.push(<Button key="autorestart" type="primary"
                         onClick={handleRestart}>{job?.instance?.step == 5 ? l('pages.devops.jobinfo.reonline') : l('pages.devops.jobinfo.restart')}</Button>);
    if (!isStatusDone(job?.instance?.status as string)) {
      buttons.push(<Button key="autostop" type="primary" danger onClick={() => {
        handleSavepoint('cancel')
      }}>{job?.instance?.step == 5 ? l('pages.devops.jobinfo.offline') : l('pages.devops.jobinfo.smart_stop')}</Button>);
      buttons.push(<Dropdown
        key="dropdown"
        trigger={['click']}
        overlay={
          <Menu onClick={({key}) => handleSavepoint(key)}>
            <Menu.Item key="trigger">{l('pages.devops.jobinfo.savepoint.trigger')}</Menu.Item>
            <Menu.Item key="stop">{l('pages.devops.jobinfo.savepoint.stop')}</Menu.Item>
            <Menu.Item key="cancel">{l('pages.devops.jobinfo.savepoint.cancel')}</Menu.Item>
            <Menu.Item key="canceljob">{l('pages.devops.jobinfo.savepoint.canceljob')}</Menu.Item>
          </Menu>
        }
      >
        <Button key="4" style={{padding: '0 8px'}}>
          <EllipsisOutlined/>
        </Button>
      </Dropdown>);
    }
    return buttons;
  }

  return (
    <PageContainer
      header={{
        title: (<><JobLifeCycle step={job?.instance?.step}/>{job?.instance?.name}</>),
        ghost: true,
        extra: getButtons(),
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
            <ClusterOutlined/> {l('pages.devops.jobinfo.localenv')}
          </Tag>)}
        </Space>
      </>}
      tabBarExtraContent={l('pages.devops.LastUpdateTime') +`：${moment(time).format('HH:mm:ss')}`}
      tabList={[
        {
          tab: l('pages.devops.jobinfo.overview'),
          key: 'base',
          closable: false,
        },
        {
          tab: l('pages.devops.jobinfo.cluster'),
          key: 'cluster',
          closable: false,
        },
        {
          tab: l('pages.devops.jobinfo.snapshot'),
          key: 'snapshot',
          closable: false,
        },
        {
          tab: l('pages.devops.jobinfo.exception'),
          key: 'exception',
          closable: false,
        },
        {
          tab: l('pages.devops.jobinfo.log'),
          key: 'log',
          closable: false,
        },
        {
          tab: l('pages.devops.jobinfo.optimize'),
          key: 'optimize',
          closable: false,
        },
        {
          tab: l('pages.devops.jobinfo.config'),
          key: 'config',
          closable: false,
        },
        {
          tab: l('pages.devops.jobinfo.flinksql'),
          key: 'flinksql',
          closable: false,
        },
        {
          tab: l('pages.devops.jobinfo.datamap'),
          key: 'datamap',
          closable: false,
        },
        {
          tab: l('pages.devops.jobinfo.olap'),
          key: 'olap',
          closable: false,
        },
        {
          tab: l('pages.devops.jobinfo.version'),
          key: 'version',
          closable: false,
        },
        {
          tab: l('pages.devops.jobinfo.alert'),
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
        {tabKey === 'cluster' ? <FlinkClusterInfo job={job}/> : undefined}
        {tabKey === 'snapshot' ? <CheckPoints job={job}/> : undefined}
        {tabKey === 'exception' ? <Exception job={job}/> : undefined}
        {tabKey === 'log' ? <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description={l('global.stay.tuned')}/> : undefined}
        {tabKey === 'optimize' ? <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description={l('global.stay.tuned')}/> : undefined}
        {tabKey === 'flinksql' ? <FlinkSQL job={job}/> : undefined}
        {tabKey === 'datamap' ? <DataMap job={job}/> : undefined}
        {tabKey === 'olap' ? <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description={l('global.stay.tuned')}/> : undefined}
        {tabKey === 'version' ? <TaskVersionInfo job={job}/> : undefined}
        {tabKey === 'alert' ? <Alert job={job}/> : undefined}
      </ProCard>
    </PageContainer>
  );
};

export default JobInfo;

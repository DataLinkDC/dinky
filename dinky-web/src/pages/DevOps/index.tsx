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


import type {StatisticProps} from '@ant-design/pro-card';
import ProCard, {StatisticCard} from '@ant-design/pro-card';
import JobInstanceTable from "./JobInstanceTable/index";
import {getStatusCount, queryOneClickOperatingTaskStatus} from "@/pages/DevOps/service";
import {useEffect, useState} from "react";
import {StatusCount} from "@/pages/DevOps/data";
import {JOB_STATUS} from "@/components/Common/JobStatus";
import {Switch} from "antd";
import {l} from "@/utils/intl";

const {Statistic} = StatisticCard;

const DevOps = () => {


  const [isHistory, setIsHistory] = useState<boolean>(false);

  const handleHistorySwicthChange = (checked: boolean) => {
    setIsHistory(checked);
  };

  const renderSwitch = () => {
    return (
      <Switch checkedChildren={l('pages.datastudio.label.history')}
              unCheckedChildren={l('pages.devops.lable.instance')}
              onChange={handleHistorySwicthChange}/>);
  };


  const statusCountDefault = [
    {key: '', title: renderSwitch(), value: 0, total: true},
    {key: JOB_STATUS.CREATED, status: 'default', title: l('pages.devops.jobstatus.CREATED'), value: 0},
    {key: JOB_STATUS.INITIALIZING, status: 'default', title: l('pages.devops.jobstatus.INITIALIZING'), value: 0},
    {key: JOB_STATUS.RUNNING, status: 'success', title: l('pages.devops.jobstatus.RUNNING'), value: 0},
    {key: JOB_STATUS.FINISHED, status: 'processing', title: l('pages.devops.jobstatus.FINISHED'), value: 0},
    {key: JOB_STATUS.FAILING, status: 'error', title: l('pages.devops.jobstatus.FAILING'), value: 0},
    {key: JOB_STATUS.FAILED, status: 'error', title: l('pages.devops.jobstatus.FAILED'), value: 0},
    {key: JOB_STATUS.SUSPENDED, status: 'warning', title: l('pages.devops.jobstatus.SUSPENDED'), value: 0},
    {key: JOB_STATUS.CANCELLING, status: 'warning', title: l('pages.devops.jobstatus.CANCELLING'), value: 0},
    {key: JOB_STATUS.CANCELED, status: 'warning', title: l('pages.devops.jobstatus.CANCELED'), value: 0},
    {key: JOB_STATUS.RESTARTING, status: 'default', title: l('pages.devops.jobstatus.RESTARTING'), value: 0},
    {key: JOB_STATUS.UNKNOWN, status: 'default', title: l('pages.devops.jobstatus.UNKNOWN'), value: 0},
  ];
  const [statusCount, setStatusCount] = useState<any[]>(statusCountDefault);
  const [statusHistoryCount, setStatusHistoryCount] = useState<any[]>(statusCountDefault);
  const [activeKey, setActiveKey] = useState<string>('');
  const [taskStatus, setTaskStatus] = useState<any>({});

  const refreshStatusCount = () => {
    const res = getStatusCount();
    const taskStatusRes = queryOneClickOperatingTaskStatus();
    taskStatusRes.then((result) => {
      setTaskStatus(result)
    })
    res.then((result) => {
      const statusHistoryCountData: StatusCount = result.datas.history;
      const historyItems: any = [
        {key: '', title: renderSwitch(), value: statusHistoryCountData.all, total: true},
        {
          key: JOB_STATUS.CREATED,
          status: 'default',
          title: l('pages.devops.jobstatus.CREATED'),
          value: statusHistoryCountData.created
        },
        {
          key: JOB_STATUS.INITIALIZING,
          status: 'default',
          title: l('pages.devops.jobstatus.INITIALIZING'),
          value: statusHistoryCountData.initializing
        },
        {
          key: JOB_STATUS.RUNNING,
          status: 'success',
          title: l('pages.devops.jobstatus.RUNNING'),
          value: statusHistoryCountData.running
        },
        {
          key: JOB_STATUS.FINISHED,
          status: 'processing',
          title: l('pages.devops.jobstatus.FINISHED'),
          value: statusHistoryCountData.finished
        },
        {
          key: JOB_STATUS.FAILING,
          status: 'error',
          title: l('pages.devops.jobstatus.FAILING'),
          value: statusHistoryCountData.failing
        },
        {
          key: JOB_STATUS.FAILED,
          status: 'error',
          title: l('pages.devops.jobstatus.FAILED'),
          value: statusHistoryCountData.failed
        },
        {
          key: JOB_STATUS.SUSPENDED,
          status: 'warning',
          title: l('pages.devops.jobstatus.SUSPENDED'),
          value: statusHistoryCountData.suspended
        },
        {
          key: JOB_STATUS.CANCELLING,
          status: 'warning',
          title: l('pages.devops.jobstatus.CANCELLING'),
          value: statusHistoryCountData.cancelling
        },
        {
          key: JOB_STATUS.CANCELED,
          status: 'warning',
          title: l('pages.devops.jobstatus.CANCELED'),
          value: statusHistoryCountData.canceled
        },
        {
          key: JOB_STATUS.RESTARTING,
          status: 'default',
          title: l('pages.devops.jobstatus.RESTARTING'),
          value: statusHistoryCountData.restarting
        },
        {
          key: JOB_STATUS.UNKNOWN,
          status: 'default',
          title: l('pages.devops.jobstatus.UNKNOWN'),
          value: statusHistoryCountData.unknown
        },
      ];
      setStatusHistoryCount(historyItems);
      const statusCountData: StatusCount = result.datas.instance;
      const items: any = [
        {key: '', title: renderSwitch(), value: statusCountData.all, total: true},
        {
          key: JOB_STATUS.CREATED,
          status: 'default',
          title: l('pages.devops.jobstatus.CREATED'),
          value: statusCountData.created
        },
        {
          key: JOB_STATUS.INITIALIZING,
          status: 'default',
          title: l('pages.devops.jobstatus.INITIALIZING'),
          value: statusCountData.initializing
        },
        {
          key: JOB_STATUS.RUNNING,
          status: 'success',
          title: l('pages.devops.jobstatus.RUNNING'),
          value: statusCountData.running
        },
        {
          key: JOB_STATUS.FINISHED,
          status: 'processing',
          title: l('pages.devops.jobstatus.FINISHED'),
          value: statusCountData.finished
        },
        {
          key: JOB_STATUS.FAILING,
          status: 'error',
          title: l('pages.devops.jobstatus.FAILING'),
          value: statusCountData.failing
        },
        {
          key: JOB_STATUS.FAILED,
          status: 'error',
          title: l('pages.devops.jobstatus.FAILED'),
          value: statusCountData.failed
        },
        {
          key: JOB_STATUS.SUSPENDED,
          status: 'warning',
          title: l('pages.devops.jobstatus.SUSPENDED'),
          value: statusCountData.suspended
        },
        {
          key: JOB_STATUS.CANCELLING,
          status: 'warning',
          title: l('pages.devops.jobstatus.CANCELLING'),
          value: statusCountData.cancelling
        },
        {
          key: JOB_STATUS.CANCELED,
          status: 'warning',
          title: l('pages.devops.jobstatus.CANCELED'),
          value: statusCountData.canceled
        },
        {
          key: JOB_STATUS.RESTARTING,
          status: 'default',
          title: l('pages.devops.jobstatus.RESTARTING'),
          value: statusCountData.restarting
        },
        {
          key: JOB_STATUS.UNKNOWN,
          status: 'default',
          title: l('pages.devops.jobstatus.UNKNOWN'),
          value: statusCountData.unknown
        },
      ];
      setStatusCount(items);
    });
  };

  useEffect(() => {
    refreshStatusCount();
    let dataPolling = setInterval(refreshStatusCount, 3000);
    return () => {
      clearInterval(dataPolling);
    };
  }, []);

  return (
    <ProCard
      tabs={{
        onChange: (key) => {
          setActiveKey(key);
        },
      }}
    >
      {(isHistory ? statusHistoryCount : statusCount).map((item) => (
        <ProCard.TabPane
          style={{width: '100%'}}
          key={item.key}
          tab={
            <Statistic
              layout="vertical"
              title={item.title}
              value={item.value}
              status={item.status as StatisticProps['status']}
              style={{width: 80, borderRight: item.total ? '1px solid #f0f0f0' : undefined}}
            />
          }
        >
          <div
            style={{
              alignItems: 'center',
              justifyContent: 'center',
              backgroundColor: '#fafafa',
            }}
          >
            <JobInstanceTable taskStatus={taskStatus} status={item.key} activeKey={activeKey} isHistory={isHistory}/>
          </div>
        </ProCard.TabPane>
      ))}
    </ProCard>
  );
};

export default DevOps;

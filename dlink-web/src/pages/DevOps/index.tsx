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


import ProCard, { StatisticCard } from '@ant-design/pro-card';
import type { StatisticProps } from '@ant-design/pro-card';
import JobInstanceTable from "./JobInstanceTable/index";
import {getStatusCount,queryOneClickOperatingTaskStatus} from "@/pages/DevOps/service";
import {useEffect, useState} from "react";
import {StatusCount} from "@/pages/DevOps/data";
import {JOB_STATUS} from "@/components/Common/JobStatus";
import {Switch} from "antd";
import { useIntl, Link, history, FormattedMessage, SelectLang, useModel } from 'umi';

const { Statistic } = StatisticCard;

const DevOps = () => {

  const intl = useIntl();

  const [isHistory, setIsHistory] = useState<boolean>(false);

  const handleHistorySwicthChange = (checked: boolean) => {
    setIsHistory(checked);
  };

  const renderSwitch = () => {
    return (<Switch checkedChildren="历史" unCheckedChildren="实例" onChange={handleHistorySwicthChange}/>);
  };



  const statusCountDefault = [
    { key: '', title: renderSwitch(), value: 0, total: true },
    { key: JOB_STATUS.CREATED, status: 'default', title: '已创建', value: 0 },
    { key: JOB_STATUS.INITIALIZING, status: 'default', title: '初始化', value: 0 },
    { key: JOB_STATUS.RUNNING, status: 'success', title: '运行中', value: 0 },
    { key: JOB_STATUS.FINISHED, status: 'processing', title: '已完成', value: 0 },
    { key: JOB_STATUS.FAILING, status: 'error', title: '异常中', value: 0 },
    { key: JOB_STATUS.FAILED, status: 'error', title: '已异常', value: 0 },
    { key: JOB_STATUS.SUSPENDED, status: 'warning', title: '已暂停', value: 0 },
    { key: JOB_STATUS.CANCELLING, status: 'warning', title: '停止中', value: 0 },
    { key: JOB_STATUS.CANCELED, status: 'warning', title: '已停止', value: 0 },
    { key: JOB_STATUS.RESTARTING, status: 'default', title: '重启中', value: 0 },
    { key: JOB_STATUS.UNKNOWN, status: 'default', title: '未知', value: 0 },
  ];
  const [statusCount, setStatusCount] = useState<any[]>(statusCountDefault);
  const [statusHistoryCount, setStatusHistoryCount] = useState<any[]>(statusCountDefault);
  const [activeKey, setActiveKey] = useState<string>('');
  const [taskStatus, setTaskStatus] = useState<any>({});

  const refreshStatusCount = () => {
    const res = getStatusCount();
    const taskStatusRes = queryOneClickOperatingTaskStatus();
    taskStatusRes.then((result)=>{
      setTaskStatus(result)
    })
    res.then((result)=>{
      const statusHistoryCountData: StatusCount = result.datas.history;
      const historyItems: any = [
        { key: '', title: renderSwitch(), value: statusHistoryCountData.all, total: true },
        { key: JOB_STATUS.CREATED, status: 'default', title: intl.formatMessage({id: 'pages.devops.jobstatus.CREATED', defaultMessage: '已创建',}), value: statusHistoryCountData.created },
        { key: JOB_STATUS.INITIALIZING, status: 'default', title: intl.formatMessage({id: 'pages.devops.jobstatus.INITIALIZING', defaultMessage: '初始化',}), value: statusHistoryCountData.initializing },
        { key: JOB_STATUS.RUNNING, status: 'success', title: intl.formatMessage({id: 'pages.devops.jobstatus.RUNNING', defaultMessage: '运行中',}), value: statusHistoryCountData.running },
        { key: JOB_STATUS.FINISHED, status: 'processing', title: intl.formatMessage({id: 'pages.devops.jobstatus.FINISHED', defaultMessage: '已完成',}), value: statusHistoryCountData.finished },
        { key: JOB_STATUS.FAILING, status: 'error', title: intl.formatMessage({id: 'pages.devops.jobstatus.FAILING', defaultMessage: '异常中',}), value: statusHistoryCountData.failing },
        { key: JOB_STATUS.FAILED, status: 'error', title:  intl.formatMessage({id: 'pages.devops.jobstatus.FAILED', defaultMessage: '已异常',}), value: statusHistoryCountData.failed },
        { key: JOB_STATUS.SUSPENDED, status: 'warning', title: intl.formatMessage({id: 'pages.devops.jobstatus.SUSPENDED', defaultMessage: '已暂停',}), value: statusHistoryCountData.suspended },
        { key: JOB_STATUS.CANCELLING, status: 'warning', title: intl.formatMessage({id: 'pages.devops.jobstatus.CANCELLING', defaultMessage: '停止中',}), value: statusHistoryCountData.cancelling },
        { key: JOB_STATUS.CANCELED, status: 'warning', title: intl.formatMessage({id: 'pages.devops.jobstatus.CANCELED', defaultMessage: '停止',}), value: statusHistoryCountData.canceled },
        { key: JOB_STATUS.RESTARTING, status: 'default', title:  intl.formatMessage({id: 'pages.devops.jobstatus.RESTARTING', defaultMessage: '重启中',}), value: statusHistoryCountData.restarting },
        { key: JOB_STATUS.UNKNOWN, status: 'default', title:  intl.formatMessage({id: 'pages.devops.jobstatus.UNKNOWN', defaultMessage: '未知',}), value: statusHistoryCountData.unknown },
      ];
      setStatusHistoryCount(historyItems);
      const statusCountData: StatusCount = result.datas.instance;
      const items: any = [
        { key: '', title: renderSwitch(), value: statusCountData.all, total: true },
        { key: JOB_STATUS.CREATED, status: 'default', title: intl.formatMessage({id: 'pages.devops.jobstatus.CREATED', defaultMessage: '已创建',}), value: statusCountData.created },
        { key: JOB_STATUS.INITIALIZING, status: 'default', title:intl.formatMessage({id: 'pages.devops.jobstatus.INITIALIZING', defaultMessage: '初始化',}), value: statusCountData.initializing },
        { key: JOB_STATUS.RUNNING, status: 'success', title: intl.formatMessage({id: 'pages.devops.jobstatus.RUNNING', defaultMessage: '运行中',}), value: statusCountData.running },
        { key: JOB_STATUS.FINISHED, status: 'processing', title: intl.formatMessage({id: 'pages.devops.jobstatus.FINISHED', defaultMessage: '已完成',}), value: statusCountData.finished },
        { key: JOB_STATUS.FAILING, status: 'error', title: intl.formatMessage({id: 'pages.devops.jobstatus.FAILING', defaultMessage: '异常中',}), value: statusCountData.failing },
        { key: JOB_STATUS.FAILED, status: 'error', title: intl.formatMessage({id: 'pages.devops.jobstatus.FAILED', defaultMessage: '已异常',}), value: statusCountData.failed },
        { key: JOB_STATUS.SUSPENDED, status: 'warning', title: intl.formatMessage({id: 'pages.devops.jobstatus.SUSPENDED', defaultMessage: '已暂停',}), value: statusCountData.suspended },
        { key: JOB_STATUS.CANCELLING, status: 'warning', title: intl.formatMessage({id: 'pages.devops.jobstatus.CANCELLING', defaultMessage: '停止中',}), value: statusCountData.cancelling },
        { key: JOB_STATUS.CANCELED, status: 'warning', title: intl.formatMessage({id: 'pages.devops.jobstatus.CANCELED', defaultMessage: '停止',}), value: statusCountData.canceled },
        { key: JOB_STATUS.RESTARTING, status: 'default', title: intl.formatMessage({id: 'pages.devops.jobstatus.RESTARTING', defaultMessage: '重启中',}), value: statusCountData.restarting },
        { key: JOB_STATUS.UNKNOWN, status: 'default', title: intl.formatMessage({id: 'pages.devops.jobstatus.UNKNOWN', defaultMessage: '未知',}), value: statusCountData.unknown },
      ];
      setStatusCount(items);
    });
  };

  useEffect(() => {
    refreshStatusCount();
    let dataPolling = setInterval(refreshStatusCount,3000);
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
      {(isHistory?statusHistoryCount:statusCount).map((item) => (
        <ProCard.TabPane
          style={{ width: '100%' }}
          key={item.key}
          tab={
            <Statistic
              layout="vertical"
              title={item.title}
              value={item.value}
              status={item.status as StatisticProps['status']}
              style={{ width: 80, borderRight: item.total ? '1px solid #f0f0f0' : undefined }}
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

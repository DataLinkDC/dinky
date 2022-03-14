import ProCard, { StatisticCard } from '@ant-design/pro-card';
import type { StatisticProps } from '@ant-design/pro-card';
import JobInstanceTable from "./JobInstanceTable";
import {getStatusCount} from "@/pages/DevOps/service";
import {useEffect, useState} from "react";
import {StatusCount} from "@/pages/DevOps/data";
import {JOB_STATUS} from "@/components/Common/JobStatus";

import {Form, Switch} from "antd";

const { Statistic } = StatisticCard;

const DevOps = (props:any) => {

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
  const [activeKey, setActiveKey] = useState<string>('');

  const refreshStatusCount = () => {
    const res = getStatusCount();
    res.then((result)=>{
      const statusCountData: StatusCount = result.datas;
      const items: any = [
        { key: '', title: renderSwitch(), value: statusCountData.all, total: true },
        { key: JOB_STATUS.CREATED, status: 'default', title: '已创建', value: statusCountData.created },
        { key: JOB_STATUS.INITIALIZING, status: 'default', title: '初始化', value: statusCountData.initializing },
        { key: JOB_STATUS.RUNNING, status: 'success', title: '运行中', value: statusCountData.running },
        { key: JOB_STATUS.FINISHED, status: 'processing', title: '已完成', value: statusCountData.finished },
        { key: JOB_STATUS.FAILING, status: 'error', title: '异常中', value: statusCountData.failing },
        { key: JOB_STATUS.FAILED, status: 'error', title: '已异常', value: statusCountData.failed },
        { key: JOB_STATUS.SUSPENDED, status: 'warning', title: '已暂停', value: statusCountData.suspended },
        { key: JOB_STATUS.CANCELLING, status: 'warning', title: '停止中', value: statusCountData.cancelling },
        { key: JOB_STATUS.CANCELED, status: 'warning', title: '停止', value: statusCountData.canceled },
        { key: JOB_STATUS.RESTARTING, status: 'default', title: '重启中', value: statusCountData.restarting },
        { key: JOB_STATUS.UNKNOWN, status: 'default', title: '未知', value: statusCountData.unknown },
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
      {statusCount.map((item) => (
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
            <JobInstanceTable status={item.key} activeKey={activeKey} isHistory={isHistory}/>
          </div>
        </ProCard.TabPane>
      ))}
    </ProCard>
  );
};

export default DevOps;

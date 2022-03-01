import {Typography, Divider, Badge, Empty,Tag} from "antd";
import ProCard, { StatisticCard } from '@ant-design/pro-card';
import type { StatisticProps } from '@ant-design/pro-card';
import JobInstanceTable from "./JobInstanceTable";
import {getStatusCount} from "@/pages/DevOps/service";
import {useEffect, useState} from "react";
import {StatusCount} from "@/pages/DevOps/data";

const { Statistic } = StatisticCard;

const DevOps = (props:any) => {

  // const {current} = props;
  const statusCountDefault = [
    { key: '', title: '全部', value: 0, total: true },
    { key: 'INITIALIZING', status: 'default', title: '初始化', value: 0 },
    { key: 'RUNNING', status: 'success', title: '运行中', value: 0 },
    { key: 'FINISHED', status: 'processing', title: '已完成', value: 0 },
    { key: 'FAILED', status: 'error', title: '发生异常', value: 0 },
    { key: 'CANCELED', status: 'warning', title: '停止', value: 0 },
  ];
  const [statusCount, setStatusCount] = useState<any[]>(statusCountDefault);
  const [activeKey, setActiveKey] = useState<string>('');

  const refreshStatusCount = () => {
    const res = getStatusCount();
    res.then((result)=>{
      const statusCountData: StatusCount = result.datas;
      const items: any = [
        { key: '', title: '全部', value: statusCountData.all, total: true },
        { key: 'INITIALIZING', status: 'default', title: '初始化', value: statusCountData.initializing },
        { key: 'RUNNING', status: 'success', title: '运行中', value: statusCountData.running },
        { key: 'FINISHED', status: 'processing', title: '已完成', value: statusCountData.finished },
        { key: 'FAILED', status: 'error', title: '发生异常', value: statusCountData.failed },
        { key: 'CANCELED', status: 'warning', title: '停止', value: statusCountData.canceled },
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
              style={{ width: 120, borderRight: item.total ? '1px solid #f0f0f0' : undefined }}
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
            <JobInstanceTable status={item.key} activeKey={activeKey}/>
          </div>
        </ProCard.TabPane>
      ))}
    </ProCard>
  );
};

export default DevOps;

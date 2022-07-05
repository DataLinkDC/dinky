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
        title: '停止任务',
        content: `确定只停止该作业，不进行 SavePoint 操作吗？`,
        okText: '确认',
        cancelText: '取消',
        onOk: async () => {
          if (!job?.cluster?.id) return;
          const res = cancelJob(job?.cluster?.id, job?.instance?.jid);
          res.then((result) => {
            if (result.code == CODE.SUCCESS) {
              message.success(key + "成功");
              handleGetJobInfoDetail();
            } else {
              message.error(key + "失败");
            }
          });
        }
      });
      return;
    }
    Modal.confirm({
      title: key + '任务',
      content: `确定${key}该作业吗？`,
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        if (!job?.cluster?.id) return;
        const res = offLineTask(job?.instance?.taskId, key);
        res.then((result) => {
          if (result.code == CODE.SUCCESS) {
            message.success(key + "成功");
            handleGetJobInfoDetail();
          } else {
            message.error(key + "失败");
          }
        });
      }
    });
  };

  const handleRestart = () => {
    Modal.confirm({
      title: '重新上线任务',
      content: `确定重新上线该作业吗？`,
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        if (!job?.cluster?.id) return;
        const res = restartJob(job?.instance?.taskId, job?.instance?.step == JOB_LIFE_CYCLE.ONLINE);
        res.then((result) => {
          if (result.code == CODE.SUCCESS) {
            message.success("重新上线成功");
          } else {
            message.error("重新上线失败");
          }
        });
      }
    });
  };

  const getButtons = () => {
    let buttons = [
      <Button key="back" type="dashed" onClick={handleBack}>返回</Button>,
    ];
    buttons.push(<Button key="refresh" icon={<RedoOutlined/>} onClick={handleRefreshJobInfoDetail}/>);
    buttons.push(<Button key="flinkwebui">
      <Link href={`http://${job?.history?.jobManagerAddress}/#/job/${job?.instance?.jid}/overview`} target="_blank">
        FlinkWebUI
      </Link></Button>);
    buttons.push(<Button key="autorestart" type="primary"
                         onClick={handleRestart}>重新{job?.instance?.step == 5 ? '上线' : '启动'}</Button>);
    if (!isStatusDone(job?.instance?.status as string)) {
      buttons.push(<Button key="autostop" type="primary" danger onClick={() => {
        handleSavepoint('cancel')
      }}>{job?.instance?.step == 5 ? '下线' : '智能停止'}</Button>);
      buttons.push(<Dropdown
        key="dropdown"
        trigger={['click']}
        overlay={
          <Menu onClick={({key}) => handleSavepoint(key)}>
            <Menu.Item key="trigger">SavePoint触发</Menu.Item>
            <Menu.Item key="stop">SavePoint暂停</Menu.Item>
            <Menu.Item key="cancel">SavePoint停止</Menu.Item>
            <Menu.Item key="canceljob">普通停止</Menu.Item>
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
          tab: '异常信息',
          key: 'exception',
          closable: false,
        },
        {
          tab: '作业日志',
          key: 'log',
          closable: false,
        },
        {
          tab: '自动调优',
          key: 'optimize',
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
          tab: '数据地图',
          key: 'datamap',
          closable: false,
        },
        {
          tab: '即席查询',
          key: 'olap',
          closable: false,
        },
        {
          tab: '历史版本',
          key: 'version',
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
        {tabKey === 'cluster' ? <FlinkClusterInfo job={job}/> : undefined}
        {tabKey === 'snapshot' ? <CheckPoints job={job}/> : undefined}
        {tabKey === 'exception' ? <Exception job={job}/> : undefined}
        {tabKey === 'log' ? <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/> : undefined}
        {tabKey === 'optimize' ? <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/> : undefined}
        {tabKey === 'flinksql' ? <FlinkSQL job={job}/> : undefined}
        {tabKey === 'datamap' ? <DataMap job={job}/> : undefined}
        {tabKey === 'olap' ? <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/> : undefined}
        {tabKey === 'version' ? <TaskVersionInfo job={job} /> : undefined}
        {tabKey === 'alert' ? <Alert job={job}/> : undefined}
      </ProCard>
    </PageContainer>
  );
};

export default JobInfo;

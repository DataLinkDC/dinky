import React, {useRef} from 'react';
import {Jobs} from "@/types/DevOps/data";
import {l} from "@/utils/intl";
import {Link} from 'umi';
import {Button, Dropdown, Menu, message, Modal, Space} from "antd";
import {EllipsisOutlined} from '@ant-design/icons';
import {isStatusDone} from "@/pages/DevOps/function";
import {useRequest} from "@@/exports";
import {API_CONSTANTS} from "@/services/constants";
import {JOB_LIFE_CYCLE} from "@/pages/DevOps/constants";

type OperatorProps = {
  jobDetail: Jobs.JobInfoDetail;
};

const operatorType = {
  RESTART_JOB: "restart",
  CANCEL_JOB: "canceljob",
  SAVEPOINT_CANCEL: "cancel",
  SAVEPOINT_TRIGGER: "trigger",
  SAVEPOINT_STOP: "stop",
}
const JobOperator = (props: OperatorProps) => {

  const {jobDetail} = props
  const webUri = `http://${jobDetail?.history?.jobManagerAddress}/#/job/${jobDetail?.instance?.jid}/overview`;

  const handleJobOperator = (key: string) => {
    Modal.confirm({
      title: l('devops.jobinfo.job.key', '', {key: key}),
      content: l('devops.jobinfo.job.keyConfirm', '', {key: key}),
      okText: l('button.confirm'),
      cancelText: l('button.cancel'),
      onOk: async () => {
        if (key == operatorType.CANCEL_JOB) {
          useRequest({
            url: API_CONSTANTS.CANCEL_JOB,
            params: {clusterId: jobDetail?.cluster?.id, jobId: jobDetail?.instance?.jid}
          });
        } else if (key == operatorType.RESTART_JOB) {
          useRequest({
            url: API_CONSTANTS.RESTART_TASK,
            params: {id: jobDetail?.instance?.taskId, isOnLine: jobDetail?.instance?.step == JOB_LIFE_CYCLE.ONLINE}
          });
        } else {
          useRequest({url: API_CONSTANTS.OFFLINE_TASK, params: {id: jobDetail?.instance?.taskId, type: key}});
        }
        message.success(l('devops.jobinfo.job.key.success', '', {key: key}));
      }
    });
  }

  return (
    <Space>

      <Button key="flinkwebui">
        <Link to={webUri} target="_blank">FlinkWebUI</Link>
      </Button>

      <Button key="autorestart" type="primary" onClick={() => handleJobOperator(operatorType.RESTART_JOB)}>
        {jobDetail?.instance?.step == 5 ? l('devops.jobinfo.reonline') : l('devops.jobinfo.restart')}
      </Button>

      {isStatusDone(jobDetail?.instance?.status as string) ? <></> :
        <>
          <Button key="autostop" type="primary" onClick={() => handleJobOperator(operatorType.SAVEPOINT_STOP)} danger>
            {jobDetail?.instance?.step == 5 ? l('devops.jobinfo.offline') : l('devops.jobinfo.smart_stop')}
          </Button>
          <Dropdown
            key="dropdown"
            trigger={['click']}
            overlay={
              <Menu onClick={({key}) => handleJobOperator(key)}>
                <Menu.Item key={operatorType.SAVEPOINT_TRIGGER}>{l('devops.jobinfo.savepoint.trigger')}</Menu.Item>
                <Menu.Item key={operatorType.SAVEPOINT_STOP}>{l('devops.jobinfo.savepoint.stop')}</Menu.Item>
                <Menu.Item key={operatorType.SAVEPOINT_CANCEL}>{l('devops.jobinfo.savepoint.cancel')}</Menu.Item>
                <Menu.Item key={operatorType.CANCEL_JOB}>{l('devops.jobinfo.savepoint.canceljob')}</Menu.Item>
              </Menu>
            }
          >
            <Button key="4" style={{padding: '0 8px'}}>
              <EllipsisOutlined/>
            </Button>
          </Dropdown>
        </>
      }

    </Space>
  )
}
export default JobOperator;

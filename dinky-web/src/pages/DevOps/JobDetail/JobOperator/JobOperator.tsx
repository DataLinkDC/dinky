import { cancelTask } from '@/pages/DataStudio/HeaderContainer/service';
import { JOB_LIFE_CYCLE } from '@/pages/DevOps/constants';
import { isStatusDone } from '@/pages/DevOps/function';
import { JobProps } from '@/pages/DevOps/JobDetail/data';
import { getData, postAll } from '@/services/api';
import { API_CONSTANTS } from '@/services/endpoints';
import { l } from '@/utils/intl';
import { EllipsisOutlined } from '@ant-design/icons';
import { Button, Dropdown, message, Modal, Space } from 'antd';

const operatorType = {
  RESTART_JOB: 'restart',
  CANCEL_JOB: 'canceljob',
  SAVEPOINT_CANCEL: 'cancel',
  SAVEPOINT_TRIGGER: 'trigger',
  SAVEPOINT_STOP: 'stop'
};
const JobOperator = (props: JobProps) => {
  const { jobDetail } = props;
  const webUri = `/api/flink/${jobDetail?.history?.jobManagerAddress}/#/job/running/${jobDetail?.instance?.jid}/overview`;

  const handleJobOperator = (key: string) => {
    Modal.confirm({
      title: l('devops.jobinfo.job.key', '', { key: key }),
      content: l('devops.jobinfo.job.keyConfirm', '', { key: key }),
      okText: l('button.confirm'),
      cancelText: l('button.cancel'),
      onOk: async () => {
        if (key == operatorType.CANCEL_JOB) {
          postAll(API_CONSTANTS.CANCEL_JOB, {
            clusterId: jobDetail?.cluster?.id,
            jobId: jobDetail?.instance?.jid
          });
        } else if (key == operatorType.RESTART_JOB) {
          getData(API_CONSTANTS.RESTART_TASK, {
            id: jobDetail?.instance?.taskId,
            isOnLine: jobDetail?.instance?.step == JOB_LIFE_CYCLE.ONLINE
          });
        } else {
          cancelTask('', jobDetail?.instance?.taskId);
        }
        message.success(l('devops.jobinfo.job.key.success', '', { key: key }));
      }
    });
  };

  return (
    <Space>
      <Button key='flinkwebui' href={webUri} target={'_blank'}>
        FlinkWebUI
      </Button>

      <Button
        key='autorestart'
        type='primary'
        onClick={() => handleJobOperator(operatorType.RESTART_JOB)}
      >
        {jobDetail?.instance?.step == 5
          ? l('devops.jobinfo.reonline')
          : l('devops.jobinfo.restart')}
      </Button>

      {isStatusDone(jobDetail?.instance?.status as string) ? (
        <></>
      ) : (
        <>
          <Button
            key='autostop'
            type='primary'
            onClick={() => handleJobOperator(operatorType.SAVEPOINT_STOP)}
            danger
          >
            {jobDetail?.instance?.step == 5
              ? l('devops.jobinfo.offline')
              : l('devops.jobinfo.smart_stop')}
          </Button>
          <Dropdown
            key='dropdown'
            trigger={['click']}
            menu={{
              onClick: (e) => handleJobOperator(e.key as string),
              items: [
                {
                  key: operatorType.SAVEPOINT_TRIGGER,
                  label: l('devops.jobinfo.savepoint.trigger')
                },
                {
                  key: operatorType.SAVEPOINT_STOP,
                  label: l('devops.jobinfo.savepoint.stop')
                },
                {
                  key: operatorType.SAVEPOINT_CANCEL,
                  label: l('devops.jobinfo.savepoint.cancel')
                }
              ]
            }}
          >
            <Button key='4' style={{ padding: '0 8px' }}>
              <EllipsisOutlined />
            </Button>
          </Dropdown>
        </>
      )}
    </Space>
  );
};
export default JobOperator;

import {Tag} from 'antd';
import {
  CheckCircleOutlined,
  ClockCircleOutlined,
  CloseCircleOutlined,
  MinusCircleOutlined,
  SyncOutlined,
  QuestionCircleOutlined
} from "@ant-design/icons";

export type JobStatusFormProps = {
  status: string|undefined;
};

export const JOB_STATUS = {
  FINISHED:'FINISHED',
  RUNNING:'RUNNING',
  FAILED:'FAILED',
  CANCELED:'CANCELED',
  INITIALIZING:'INITIALIZING',
  RESTARTING:'RESTARTING',
  CREATED:'CREATED',
  FAILING:'FAILING',
  SUSPENDED:'SUSPENDED',
  CANCELLING:'CANCELLING',
  UNKNOWN:'UNKNOWN',
};

export function isStatusDone(type: string){
  if(!type){
    return true;
  }
  switch (type) {
    case JOB_STATUS.FAILED:
    case JOB_STATUS.CANCELED:
    case JOB_STATUS.FINISHED:
    case JOB_STATUS.UNKNOWN:
      return true;
    default:
      return false;
  }
};

const JobStatus = (props: JobStatusFormProps) => {

  const {status} = props;

  return (<>
    { (status === 'FINISHED') ?
      (<Tag icon={<CheckCircleOutlined/>} color="blue">
        FINISHED
      </Tag>) : (status === 'RUNNING') ?
        (<Tag icon={<SyncOutlined spin/>} color="green">
          RUNNING
        </Tag>) : (status === 'FAILED') ?
          (<Tag icon={<CloseCircleOutlined/>} color="error">
            FAILED
          </Tag>) : (status === 'CANCELED') ?
            (<Tag icon={<MinusCircleOutlined/>} color="orange">
              CANCELED
            </Tag>) : (status === 'INITIALIZING') ?
              (<Tag icon={<ClockCircleOutlined/>} color="default">
                INITIALIZING
              </Tag>) : (status === 'RESTARTING') ?
                (<Tag icon={<ClockCircleOutlined/>} color="default">
                  RESTARTING
                </Tag>) :
                (<Tag icon={<QuestionCircleOutlined />} color="default">
                  UNKNOWEN
                </Tag>)
    }
  </>)
};

export default JobStatus;

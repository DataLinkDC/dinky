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

const JobStatus = (props: JobStatusFormProps) => {

  const {status} = props;

  return (<>
    { (status === 'FINISHED') ?
      (<Tag icon={<CheckCircleOutlined/>} color="success">
        FINISHED
      </Tag>) : (status === 'RUNNING') ?
        (<Tag icon={<SyncOutlined spin/>} color="processing">
          RUNNING
        </Tag>) : (status === 'FAILED') ?
          (<Tag icon={<CloseCircleOutlined/>} color="error">
            FAILED
          </Tag>) : (status === 'CANCELED') ?
            (<Tag icon={<MinusCircleOutlined/>} color="default">
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

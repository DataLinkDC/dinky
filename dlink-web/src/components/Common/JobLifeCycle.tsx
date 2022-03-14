import {Tag} from 'antd';
import {
  EditOutlined,
  CameraOutlined,
  CarryOutOutlined,
} from "@ant-design/icons";

export type JobLifeCycleFormProps = {
  step: number|undefined;
};

export const JOB_LIFE_CYCLE = {
  UNKNOWN: 0,
  CREATE: 1,
  DEVELOP: 2,
  DEBUG: 3,
  RELEASE: 4,
  ONLINE: 5,
  CANCEL: 6,
};

export const isDeletedTask = (taskStep: number) => {
  if (taskStep && taskStep === JOB_LIFE_CYCLE.CANCEL) {
    return true;
  }
  return false;
};

const JobLifeCycle = (props: JobLifeCycleFormProps) => {

  const {step} = props;

  const renderJobLifeCycle = () => {
    switch (step){
      case JOB_LIFE_CYCLE.DEVELOP:
        return (<Tag icon={<EditOutlined />} color="default">开发中</Tag>);
      case JOB_LIFE_CYCLE.RELEASE:
        return (<Tag icon={<CameraOutlined />} color="green">已发布</Tag>);
      case JOB_LIFE_CYCLE.ONLINE:
        return (<Tag icon={<CarryOutOutlined />} color="blue">已上线</Tag>);
      default:
        return undefined;
    }
  };

  return (<>{renderJobLifeCycle()}</>)
};

export default JobLifeCycle;

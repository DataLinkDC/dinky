import { Jobs } from '@/types/DevOps/data';

export type JobProps = {
  jobDetail: Jobs.JobInfoDetail;
};

export type JobMetricsItem = {
  id: number;
  taskId: number;
  vertices: string;
  metrics: string;
  position: string;
  showType: string;
  showSize: string;
  title: string;
  createTime: string;
  updateTime: string;
};

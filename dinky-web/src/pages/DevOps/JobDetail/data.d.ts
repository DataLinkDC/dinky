import { Jobs } from '@/types/DevOps/data';

export type JobProps = {
  jobDetail: Jobs.JobInfoDetail;
};

export type JobVertice = {
  id: string,
  name: string,
  maxParallelism: number,
  parallelism: number,
  status: string,
  duration: number,
  tasks: any,
  metrics: any
}

import { JobProps } from '@/pages/DevOps/JobDetail/data';

const JobOperatorGraph = (props: JobProps) => {
  const { jobDetail } = props;
  return (
    <iframe
      width="100%"
      height={window.innerHeight - 200}
      style={{ border: 0 }}
      src={`/api/flink/web/${jobDetail?.history?.jobManagerAddress}/#/job/running/${jobDetail?.instance?.jid}/overview`}
    />
  );
};

export default JobOperatorGraph;

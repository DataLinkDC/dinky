import FlinkDag from '@/components/FlinkDag';
import { Jobs } from '@/types/DevOps/data';
import { Empty } from 'antd';
import { StateType } from 'rmc-input-number';
import { connect } from 'umi';

const FlinkGraph = (props: { data: Jobs.JobPlan }) => {
  const { data } = props;

  const job = {
    plan: data
  } as Jobs.Job;

  return (
    <>
      {data ? (
        <div style={{ width: '50wh', height: '50vh' }}>
          <FlinkDag job={job} onlyPlan={true} />
        </div>
      ) : (
        <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
      )}
    </>
  );
};

export default connect(({}: { Studio: StateType }) => ({}))(FlinkGraph);

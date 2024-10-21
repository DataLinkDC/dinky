import {Jobs} from "@/types/DevOps/data";
import FlinkDag from "@/components/Flink/FlinkDag";
import {Empty} from "antd";

export default  (props: { data: Jobs.JobPlan }) => {
  const { data } = props;

  const job = {
    plan: data
  } as Jobs.Job;

  return (
    <>
      {data ? (
        <div style={{ width: '100%', height: '100%'  }}>
          <FlinkDag job={job} onlyPlan={true} />
        </div>
      ) : (
        <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
      )}
    </>
  );
};

import CodeShow from "@/components/Common/CodeShow";

const FlinkSQL = (props: any) => {

  const {job} = props;

  return (<>
    <CodeShow code={job.history?.statement} language='sql' height='500px'/>
  </>)
};

export default FlinkSQL;

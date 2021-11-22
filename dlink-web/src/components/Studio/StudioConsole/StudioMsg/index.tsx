import {Typography, Divider, Badge, Empty,Tag} from "antd";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {connect} from "umi";
import { FireOutlined } from '@ant-design/icons';

const { Title, Paragraph, Text, Link } = Typography;

const StudioMsg = (props:any) => {

  const {current} = props;

  return (
    <Typography>
      {current.console.result.jobConfig?(<Paragraph>
        <blockquote><Link href={`http://${current.console.result.jobConfig.address}`} target="_blank">
          [{current.console.result.jobConfig.session}:{current.console.result.jobConfig.address}]
        </Link> <Divider type="vertical"/>{current.console.result.startTime}
          <Divider type="vertical"/>{current.console.result.endTime}
          <Divider type="vertical"/>
          {!(current.console.result.status=='SUCCESS') ? <><Badge status="error"/><Text type="danger">Error</Text></> :
            <><Badge status="success"/><Text type="success">Success</Text></>}
          <Divider type="vertical"/>
          {current.console.result.jobConfig.jobName&&<Text code>{current.console.result.jobConfig.jobName}</Text>}
          {current.console.result.jobId&&
          (<>
            <Divider type="vertical"/>
            <Tag color="blue" key={current.console.result.jobId}>
            <FireOutlined /> {current.console.result.jobId}
          </Tag>
            </>)}
        </blockquote>
        {current.console.result.statement && (<pre style={{height: '100px'}}>{current.console.result.statement}</pre>)}
        {current.console.result.error && (<pre style={{height: '100px'}}>{current.console.result.error}</pre>)}
      </Paragraph>):<Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
      }
    </Typography>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  current: Studio.current,
}))(StudioMsg);

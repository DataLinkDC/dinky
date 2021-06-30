import {Typography, Divider, Badge, Empty} from "antd";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {connect} from "umi";

const { Title, Paragraph, Text, Link } = Typography;

const StudioMsg = (props:any) => {

  const {current} = props;

  return (
    <Typography>
      {current.console.result.map((item,index)=> {
        if(index==0) {
          return (<Paragraph>
            <blockquote><Link href={`http://${item.jobConfig.host}`} target="_blank">
              [{item.jobConfig.sessionKey}:{item.jobConfig.host}]
            </Link> <Divider type="vertical"/>{item.startTime}
             <Divider type="vertical"/>{item.endTime}
              <Divider type="vertical"/>
              {!(item.status=='SUCCESS') ? <><Badge status="error"/><Text type="danger">Error</Text></> :
                <><Badge status="success"/><Text type="success">Success</Text></>}
              <Divider type="vertical"/>
              {item.jobConfig.jobName&&<Text code>{item.jobConfig.jobName}</Text>}
              {item.jobId&&<Text code>{item.jobId}</Text>}
              </blockquote>
            {item.statement && (<pre style={{height: '100px'}}>{item.statement}</pre>)}
            {item.error && (<pre style={{height: '100px'}}>{item.error}</pre>)}
          </Paragraph>)
        }else{
          return '';
        }
      })}
      {current.console.result.length==0?<Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />:''}
    </Typography>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  current: Studio.current,
}))(StudioMsg);

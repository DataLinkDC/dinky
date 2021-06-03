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
            <blockquote><Link href={`http://${item.flinkHost}:${item.flinkPort}`} target="_blank">
              [{item.sessionId}:{item.flinkHost}:{item.flinkPort}]
            </Link> <Divider type="vertical"/>{item.finishDate}
              <Divider type="vertical"/>
              {!item.success ? <><Badge status="error"/><Text type="danger">Error</Text></> :
                <><Badge status="success"/><Text type="success">Success</Text></>}
              <Divider type="vertical"/>
              {item.jobId&&<Text code>{item.jobId}</Text>}
              <Text keyboard>{item.time}ms</Text></blockquote>
            {item.statement && (<pre style={{height: '100px'}}>{item.statement}</pre>)}
            {item.msg ? item.msg : ''}
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

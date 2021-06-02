import { Typography,Divider,Badge,Select,Tag,Form} from "antd";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {connect} from "umi";

const { Option } = Select;
const { Title, Paragraph, Text, Link } = Typography;


const StudioTable = (props:any) => {

  const {current} = props;

  return (
    <Typography>
      <Form.Item label="当前执行记录" tooltip="选择最近的执行记录，仅包含成功的记录">
      <Select
        //mode="multiple"
        style={{ width: '100%' }}
        placeholder="选择最近的执行记录"
        defaultValue={[0]}
        optionLabelProp="label"
      >
        {current.console.result.map((item,index)=> {
          if(item.success) {
            let tag = (<><Tag color="processing">{item.finishDate}</Tag>
              <Text underline>[{item.sessionId}:{item.flinkHost}:{item.flinkPort}]</Text>
              <Text keyboard>{item.time}ms</Text>
               {item.statement.substring(0,20)}</>);
            return (<Option value={index} label={tag}>
              {tag}
            </Option>)
          }
        })}
      </Select>
      </Form.Item>
    </Typography>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  current: Studio.current,
}))(StudioTable);

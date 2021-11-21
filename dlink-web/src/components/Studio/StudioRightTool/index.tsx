import {Tabs, Empty, Form} from "antd";
import {SettingOutlined,ScheduleOutlined,AuditOutlined} from "@ant-design/icons";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {connect} from "umi";
import styles from "./index.less";
import StudioConfig from "./StudioConfig";
import StudioSetting from "./StudioSetting";
import StudioSavePoint from "./StudioSavePoint";


const { TabPane } = Tabs;



const StudioRightTool = (props:any) => {
  // const [form] = Form.useForm();
  const {form} = props;
  return (
    <Tabs defaultActiveKey="1" size="small" tabPosition="right"  style={{ height: "100%",border: "1px solid #f0f0f0"}}>
      <TabPane tab={<span><SettingOutlined /> 作业配置</span>} key="StudioSetting" >
        <StudioSetting form={form} />
      </TabPane>
      <TabPane tab={<span><ScheduleOutlined /> 执行配置</span>} key="StudioConfig" >
        <StudioConfig form={form}/>
      </TabPane>
      <TabPane tab={<span><ScheduleOutlined /> 保存点</span>} key="3" >
        <StudioSavePoint />
      </TabPane>
      <TabPane tab={<span><AuditOutlined /> 审计</span>} key="4" >
        <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
      </TabPane>
    </Tabs>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  sql: Studio.sql,
}))(StudioRightTool);

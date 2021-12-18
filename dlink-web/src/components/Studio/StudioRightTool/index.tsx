import {Tabs, Empty, Form} from "antd";
import {SettingOutlined,ScheduleOutlined,AuditOutlined} from "@ant-design/icons";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {connect} from "umi";
import styles from "./index.less";
import StudioConfig from "./StudioConfig";
import StudioSetting from "./StudioSetting";
import StudioSavePoint from "./StudioSavePoint";
import StudioSqlConfig from "./StudioSqlConfig";
import {DIALECT} from "@/components/Studio/conf";

const { TabPane } = Tabs;



const StudioRightTool = (props:any) => {

  const {current,form,toolHeight} = props;

  const renderSqlContent = () => {
    return (<>
      <TabPane tab={<span><ScheduleOutlined /> 执行配置</span>} key="StudioConfig" >
        <StudioSqlConfig form={form}/>
      </TabPane>
      </>)
  };

  const renderFlinkSqlContent = () => {
    return (<><TabPane tab={<span><SettingOutlined /> 作业配置</span>} key="StudioSetting" >
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
      </TabPane></>)
  };

  return (
    <Tabs defaultActiveKey="1" size="small" tabPosition="right"  style={{ height: toolHeight}}>
      {current.task.dialect === DIALECT.SQL ? renderSqlContent(): renderFlinkSqlContent()}
    </Tabs>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  sql: Studio.sql,
  toolHeight: Studio.toolHeight,
  current: Studio.current,
}))(StudioRightTool);

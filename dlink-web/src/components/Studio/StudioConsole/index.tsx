import { Tabs,Empty } from "antd";
import {CodeOutlined, TableOutlined,RadarChartOutlined,CalendarOutlined,FileSearchOutlined,DesktopOutlined
  ,FunctionOutlined,ApartmentOutlined} from "@ant-design/icons";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {connect} from "umi";
import styles from "./index.less";
import StudioMsg from "./StudioMsg";
import StudioTable from "./StudioTable";
import StudioHistory from "./StudioHistory";
import StudioFX from "./StudioFX";

const { TabPane } = Tabs;



const StudioConsole = (props:any) => {

  return (
    <Tabs defaultActiveKey="StudioMsg" size="small">
      <TabPane
        tab={
          <span>
          <CodeOutlined />
          信息
        </span>
        }
        key="StudioMsg"
      >
        <StudioMsg />
      </TabPane>
      <TabPane
        tab={
          <span>
          <TableOutlined />
          结果
        </span>
        }
        key="StudioTable"
      >
        <StudioTable />
      </TabPane>
      <TabPane
        tab={
          <span>
          <RadarChartOutlined />
          指标
        </span>
        }
        key="StudioMetrics"
      >
        <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
      </TabPane>
      <TabPane
        tab={
          <span>
          <ApartmentOutlined />
          血缘
        </span>
        }
        key="StudioConsanguinity"
      >
        <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
      </TabPane>
      <TabPane
        tab={
          <span>
          <DesktopOutlined />
          进程
        </span>
        }
        key="StudioProcess"
      >
        <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
      </TabPane>
      <TabPane
        tab={
          <span>
          <CalendarOutlined />
          历史
        </span>
        }
        key="StudioHistory"
      >
        <StudioHistory />
      </TabPane>
      <TabPane
        tab={
          <span>
          <FunctionOutlined />
          函数
        </span>
        }
        key="StudioFX"
      >
        <StudioFX />
      </TabPane>
      <TabPane
        tab={
          <span>
          <FileSearchOutlined />
          文档
        </span>
        }
        key="StudioDocument"
      >
        <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
      </TabPane>
    </Tabs>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  sql: Studio.sql,
}))(StudioConsole);

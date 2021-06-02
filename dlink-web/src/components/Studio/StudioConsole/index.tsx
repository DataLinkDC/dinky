import { Tabs,Empty } from "antd";
import {CodeOutlined, TableOutlined,RadarChartOutlined,CalendarOutlined,FileSearchOutlined,DesktopOutlined
  ,FunctionOutlined} from "@ant-design/icons";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {connect} from "umi";
import styles from "./index.less";
import StudioMsg from "./StudioMsg";
import StudioTable from "./StudioTable";

const { TabPane } = Tabs;



const StudioConsole = (props:any) => {

  return (
    <Tabs defaultActiveKey="1" size="small">
      <TabPane
        tab={
          <span>
          <CodeOutlined />
          信息
        </span>
        }
        key="1"
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
        key="2"
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
        key="3"
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
        key="4"
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
        key="5"
      >
        <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
      </TabPane>
      <TabPane
        tab={
          <span>
          <FunctionOutlined />
          函数
        </span>
        }
        key="6"
      >
        <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
      </TabPane>
      <TabPane
        tab={
          <span>
          <FileSearchOutlined />
          文档
        </span>
        }
        key="7"
      >
        <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
      </TabPane>
    </Tabs>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  sql: Studio.sql,
}))(StudioConsole);

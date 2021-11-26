import {Tabs, Empty} from "antd";
import {
  CodeOutlined, TableOutlined, RadarChartOutlined, CalendarOutlined, FileSearchOutlined, DesktopOutlined
  , FunctionOutlined, ApartmentOutlined
} from "@ant-design/icons";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {connect} from "umi";
import styles from "./index.less";
import StudioMsg from "./StudioMsg";
import StudioTable from "./StudioTable";
import StudioHistory from "./StudioHistory";
import StudioFX from "./StudioFX";
import StudioCA from "./StudioCA";
import StudioProcess from "./StudioProcess";
import {Scrollbars} from 'react-custom-scrollbars';

const {TabPane} = Tabs;

const StudioConsole = (props: any) => {

  const {height} = props;
  let consoleHeight = (height - 37.6);
  return (
    <Tabs defaultActiveKey="StudioMsg" size="small" tabPosition="top" style={{
      border: "1px solid #f0f0f0", height: height, margin: "0 32px"
    }}>
      <TabPane
        tab={
          <span>
          <CodeOutlined/>
          信息
        </span>
        }
        key="StudioMsg"
      >
        <Scrollbars style={{height: consoleHeight}}>
          <StudioMsg/>
        </Scrollbars>
      </TabPane>
      <TabPane
        tab={
          <span>
          <TableOutlined/>
          结果
        </span>
        }
        key="StudioTable"
      >
        <Scrollbars style={{height: consoleHeight}}>
          <StudioTable/>
        </Scrollbars>
      </TabPane>
      <TabPane
        tab={
          <span>
          <RadarChartOutlined/>
          指标
        </span>
        }
        key="StudioMetrics"
      >
        <Scrollbars style={{height: consoleHeight}}>
          <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>
        </Scrollbars>
      </TabPane>
      <TabPane
        tab={
          <span>
          <ApartmentOutlined/>
          血缘
        </span>
        }
        key="StudioConsanguinity"
      >
        <Scrollbars style={{height: consoleHeight}}>
          <StudioCA/>
        </Scrollbars>
      </TabPane>
      <TabPane
        tab={
          <span>
          <DesktopOutlined/>
          进程
        </span>
        }
        key="StudioProcess"
      >
        <Scrollbars style={{height: consoleHeight}}>
          <StudioProcess/>
        </Scrollbars>
      </TabPane>
      <TabPane
        tab={
          <span>
          <CalendarOutlined/>
          历史
        </span>
        }
        key="StudioHistory"
      >
        <Scrollbars style={{height: consoleHeight}}>
          <StudioHistory/>
        </Scrollbars>
      </TabPane>
      <TabPane
        tab={
          <span>
          <FunctionOutlined/>
          函数
        </span>
        }
        key="StudioFX"
      >
        <Scrollbars style={{height: consoleHeight}}>
          <StudioFX/>
        </Scrollbars>
      </TabPane>
      <TabPane
        tab={
          <span>
          <FileSearchOutlined/>
          文档
        </span>
        }
        key="StudioDocument"
      >
        <Scrollbars style={{height: consoleHeight}}>
          <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>
        </Scrollbars>
      </TabPane>
    </Tabs>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  sql: Studio.sql,
}))(StudioConsole);

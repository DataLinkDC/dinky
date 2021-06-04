import React, {useEffect, useState} from "react";
import {connect} from "umi";
import styles from './index.less';
import {BarsOutlined,SettingOutlined,AuditOutlined,ScheduleOutlined,AppstoreOutlined,ApiOutlined,DashboardOutlined,
  FireOutlined} from "@ant-design/icons";

import StudioMenu from "./StudioMenu";
import {Row, Col, Card, Empty, Tabs, Form,BackTop} from "antd";
import StudioTree from "./StudioTree";
import StudioTabs from "./StudioTabs";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import StudioConsole from "./StudioConsole";
import StudioSetting from "./StudioSetting";
import StudioEdit from "./StudioEdit";
import StudioConnector from "./StudioConnector";

const {TabPane} = Tabs;

type StudioProps = {
  sql: StateType['sql'];
};
const Studio: React.FC<StudioProps> = ({sql}) => {

  const [console, setConsole] = useState<boolean>(false);
  const [sqls, setSqls] = useState<String>();
  const [form] = Form.useForm();

  useEffect(() => {
    setSqls(sql);
  }, [sql]);

  return (
    <div>
      <StudioMenu form={form}/>
      <Card bordered={false} className={styles.card} size="small">
        <Row>
          <Col span={4}>
            <Tabs defaultActiveKey="1" size="small">
              <TabPane tab={<span><BarsOutlined/>目录</span>} key="1" >
                <StudioTree/>
              </TabPane>
              <TabPane tab={<span><AppstoreOutlined />元数据</span>} key="2" >
                <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
              </TabPane>
            </Tabs>
          </Col>
          <Col span={16}>
            <StudioTabs/>
            <StudioEdit/>
            <StudioConsole/>
          </Col>
          <Col span={4}>
            <Tabs defaultActiveKey="1" size="small">
              <TabPane tab={<span><SettingOutlined />配置</span>} key="1" >
                <StudioSetting form={form} />
              </TabPane>
              <TabPane tab={<span><ScheduleOutlined />详情</span>} key="2" >
                <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
              </TabPane>
              <TabPane tab={<span><AuditOutlined />审计</span>} key="3" >
                <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
              </TabPane>
            </Tabs>
            <Tabs defaultActiveKey="1" size="small">
              <TabPane tab={<span>&nbsp;<ApiOutlined />连接器</span>} key="1" >
                <StudioConnector />
              </TabPane>
              <TabPane tab={<span>&nbsp;<DashboardOutlined />总览</span>} key="2" >
                <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
              </TabPane>
              <TabPane tab={<span>&nbsp;<FireOutlined />任务</span>} key="3" >
                <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
              </TabPane>
            </Tabs>
          </Col>
        </Row>
      </Card>
      <BackTop />
    </div>
  )
};


/*function mapStateToProps(state) {
  // 这个state是所有model层的state，这里只用到其中一个，所以state.testPage把命名空间为testPage这个model层的state数据取出来
  // es6语法解构赋值
  debugger;
  const { data } = state.Studio;
  // 这里return出去的数据，会变成此组件的props，在组件可以通过props.num取到。props变化了，会重新触发render方法，界面也就更新了。
  return {
    data,
  };
}*/

// export default connect(mapStateToProps)(Studio);

export default connect(({Studio}: { Studio: StateType }) => ({
  current: Studio.current,
  catalogue: Studio.catalogue,
  sql: Studio.sql,
  cluster: Studio.cluster,
  tabs: Studio.tabs,
}))(Studio);

// export default Studio;

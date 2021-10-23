import React from "react";
import {connect} from "umi";
import styles from './index.less';

import {} from "@ant-design/icons";

import StudioMenu from "./StudioMenu";
import {Row, Col, Card, Form,BackTop} from "antd";
import StudioTabs from "./StudioTabs";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import StudioConsole from "./StudioConsole";
import StudioLeftTool from "./StudioLeftTool";
import StudioRightTool from "./StudioRightTool";
import {listSession, showCluster, showDataBase,getFillAllByVersion} from "@/components/Studio/StudioEvent/DDL";

type StudioProps = {
  rightClickMenu:StateType['rightClickMenu'];
  dispatch:any;
};

const Studio: React.FC<StudioProps> = (props) => {

  const {rightClickMenu,dispatch} = props;
  const [form] = Form.useForm();
  getFillAllByVersion('',dispatch);
  showCluster(dispatch);
  showDataBase(dispatch);
  listSession(dispatch);

  const onClick=()=>{
    if(rightClickMenu){
      dispatch&&dispatch({
        type: "Studio/showRightClickMenu",
        payload: false,
      });
    }
  };

  return (
    <div onClick={onClick} style={{'margin':'-24px'}}>
      <StudioMenu form={form}/>
      <Card bordered={false} className={styles.card} size="small" id="studio_card">
        <Row>
          <Col span={4} className={styles["vertical-tabs"]}>
            <StudioLeftTool/>
          </Col>
          <Col span={16}>
            <StudioTabs/>
          </Col>
          <Col span={4} className={styles["vertical-tabs"]}>
            <StudioRightTool form={form}/>
          </Col>
        </Row>
        <Row>
          <Col span={24}>
            <StudioConsole/>
          </Col>
        </Row>
      </Card>
      <BackTop />
    </div>
  )
};

export default connect(({Studio}: { Studio: StateType }) => ({
  rightClickMenu: Studio.rightClickMenu,
}))(Studio);

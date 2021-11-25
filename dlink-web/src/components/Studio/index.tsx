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
import {
  listSession, showCluster, showDataBase, getFillAllByVersion,
  showClusterConfiguration, showSessionCluster, showJars
} from "@/components/Studio/StudioEvent/DDL";
import {loadSettings} from "@/pages/Settings/function";
import DraggleLayout from "@/components/DraggleLayout";

type StudioProps = {
  rightClickMenu:StateType['rightClickMenu'];
  dispatch:any;
};

const Studio: React.FC<StudioProps> = (props) => {

  const {rightClickMenu,dispatch} = props;
  const [form] = Form.useForm();
  loadSettings(dispatch);
  getFillAllByVersion('',dispatch);
  showCluster(dispatch);
  showSessionCluster(dispatch);
  showClusterConfiguration(dispatch);
  showDataBase(dispatch);
  listSession(dispatch);
  showJars(dispatch);

  const onClick=()=>{
    if(rightClickMenu){
      dispatch&&dispatch({
        type: "Studio/showRightClickMenu",
        payload: false,
      });
    }
  };

  return (
    // <div onClick={onClick} style={{'margin':'-24px'}}>
    //   <StudioMenu form={form}/>
    //   <Card bordered={false} className={styles.card} size="small" id="studio_card">
    //     <Row>
    //       <Col span={4} className={styles["vertical-tabs"]}>
    //         <StudioLeftTool/>
    //       </Col>
    //       <Col span={16}>
    //         <StudioTabs/>
    //       </Col>
    //       <Col span={4} className={styles["vertical-tabs"]}>
    //         <StudioRightTool form={form}/>
    //       </Col>
    //     </Row>
    //     <Row>
    //       <Col span={24}>
    //         <StudioConsole/>
    //       </Col>
    //     </Row>
    //   </Card>
    //   <BackTop />
    // </div>
    <div onClick={onClick} style={{'margin':'-24px'}}>
      <StudioMenu form={form}/>
      <Card bordered={false} className={styles.card} size="small" id="studio_card">
        <Row>
          <Col>
            <DraggleLayout
              containerWidth={1100}
              containerHeight={1220}
              min={50}
              max={600}
              initLeftWidth={200}
              handler={
                <div
                  style={{
                    width: 4,
                    height: '100%',
                    background: 'rgb(77, 81, 100)',
                  }}
                />
              }
            >
              <StudioLeftTool span={4} className={styles["vertical-tabs"]} style={{
                backgroundColor: `rgb(36, 205, 208)`,
                color: `#fff`,
                height: '100%',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
              }}/>
              <StudioTabs
                style={{
                  backgroundColor: `rgb(116, 140, 253)`,
                  color: `#fff`,
                  height: '100%',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                }}
              />
            </DraggleLayout>
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

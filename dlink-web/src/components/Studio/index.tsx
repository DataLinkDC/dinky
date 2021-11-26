import React, {useEffect, useRef, useState, useCallback} from "react";
import {connect} from "umi";
import styles from './index.less';
import {} from "@ant-design/icons";
import StudioMenu from "./StudioMenu";
import {Row, Col, Card, Form, BackTop} from "antd";
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
import DraggleVerticalLayout from "@/components/DraggleLayout/DraggleVerticalLayout";

type StudioProps = {
  rightClickMenu: StateType['rightClickMenu'];
  dispatch: any;
};

const Studio: React.FC<StudioProps> = (props) => {

  const {rightClickMenu, toolHeight, toolWidth, dispatch} = props;
  const [form] = Form.useForm();
  const VIEW = {
    rightToolWidth: 300,
    leftToolWidth: 300,
    marginTop: 116,
    topHeight: 35.6,
    bottomHeight: 153.6,
  };
  const [size, setSize] = useState({
    width: document.documentElement.clientWidth - 1,
    height: document.documentElement.clientHeight,
  });
  const onResize = useCallback(() => {
    setSize({
      width: document.documentElement.clientWidth - 1,
      height: document.documentElement.clientHeight,
    })
  }, []);

  useEffect(() => {
    window.addEventListener('resize', onResize);
    onResize();
    return () => {
      window.removeEventListener('resize', onResize);
    };
  }, [onResize]);

  loadSettings(dispatch);
  getFillAllByVersion('', dispatch);
  showCluster(dispatch);
  showSessionCluster(dispatch);
  showClusterConfiguration(dispatch);
  showDataBase(dispatch);
  listSession(dispatch);
  showJars(dispatch);

  const onClick = () => {
    if (rightClickMenu) {
      dispatch && dispatch({
        type: "Studio/showRightClickMenu",
        payload: false,
      });
    }
  };

  return (
    <div onClick={onClick} style={{'margin': '-24px'}}>
      <StudioMenu form={form}/>
      <Card bordered={false} className={styles.card} size="small" id="studio_card" style={{marginBottom: 0}}>
        <DraggleVerticalLayout
          containerWidth={size.width}
          containerHeight={(size.height - VIEW.marginTop)}
          min={(VIEW.topHeight)}
          max={(size.height - VIEW.bottomHeight)}
          initTopHeight={VIEW.topHeight}
          handler={
            <div
              style={{
                height: 4,
                width: '100%',
                background: 'rgb(240, 240, 240)',
              }}
            />
          }
        >
          <Row>
            <DraggleLayout
              containerWidth={size.width - VIEW.rightToolWidth}
              containerHeight={toolHeight}
              min={VIEW.leftToolWidth}
              max={size.width * (1 / 2)}
              initLeftWidth={VIEW.leftToolWidth}
              handler={
                <div
                  style={{
                    width: 4,
                    height: '100%',
                    background: 'rgb(240, 240, 240)',
                  }}
                />
              }
            >
              <Col className={styles["vertical-tabs"]}>
                <StudioLeftTool style={{
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                }}/>
              </Col>
              <Col>
                <StudioTabs width={size.width - VIEW.rightToolWidth - toolWidth}/>
              </Col>
            </DraggleLayout>
            <Col id='StudioRightTool' style={{width: VIEW.rightToolWidth}} className={styles["vertical-tabs"]}>
              <StudioRightTool form={form}/>
            </Col>
          </Row>
          <Row>
            <Col span={24}>
              <StudioConsole height={size.height - toolHeight - VIEW.marginTop}/>
            </Col>
          </Row>
        </DraggleVerticalLayout>
      </Card>
      <BackTop/>
    </div>
  )
};

export default connect(({Studio}: { Studio: StateType }) => ({
  rightClickMenu: Studio.rightClickMenu,
  toolHeight: Studio.toolHeight,
  toolWidth: Studio.toolWidth,
}))(Studio);

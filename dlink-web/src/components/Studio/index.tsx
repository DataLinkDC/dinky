import React, {useEffect, useRef, useState,useCallback} from "react";
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
  const VIEW = {
    rightToolWidth:300,
    leftToolWidth:300,
    marginTop:114,
  };
  const [height, setHeight] = useState<number>();
  const [size, setSize] = useState({
    width: document.documentElement.clientWidth,
    height: document.documentElement.clientHeight,
  });
  const onResize = useCallback(() => {
    setSize({
      width: document.documentElement.clientWidth,
      height: document.documentElement.clientHeight,
    })
  }, []);
  // const width = document.querySelector('body').offsetWidth;
  // const height = document.querySelector('body').offsetHeight*(1/2);
  /*const minWidth = document.querySelector('body').offsetWidth*(1/6);
  const maxWidth = document.querySelector('body').offsetWidth*(1/2);*/
 /* const resize=()=>{
    debugger;
    setWidth(document.querySelector('body').offsetWidth);
    setHeight(document.querySelector('body').offsetHeight);
    console.log(width);
    console.log(height);
  };*/
  useEffect(() => {
    window.addEventListener('resize', onResize);
    onResize();
    return () => {
      window.removeEventListener('resize', onResize);
    };
  }, [onResize]);

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
    <div onClick={onClick} style={{'margin':'-24px'}}>
      <StudioMenu form={form}/>
      <Card bordered={false} className={styles.card} size="small" id="studio_card">
        <Row>
            <DraggleLayout
              containerWidth={size.width-VIEW.rightToolWidth}
              containerHeight={(size.height-VIEW.marginTop)/2}
              min={VIEW.leftToolWidth}
              max={size.width*(1/2)}
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
                <StudioLeftTool className={styles["vertical-tabs"]} style={{
                  height: (size.height-VIEW.marginTop),
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                }}/>
              </Col>
              <Col
                style={{
                  height: ((size.height-VIEW.marginTop)),
                }}>
              <StudioTabs
                style={{
                  height: ((size.height-VIEW.marginTop)/2),
                }}
              />
              <StudioConsole
                style={{
                  height: ((size.height-VIEW.marginTop)/2),
                }}
              />
              </Col>
            </DraggleLayout>
          <Col id='StudioRightTool' style={{width:VIEW.rightToolWidth,height:(size.height-VIEW.marginTop)}} className={styles["vertical-tabs"]}>
            <StudioRightTool form={form}/>
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

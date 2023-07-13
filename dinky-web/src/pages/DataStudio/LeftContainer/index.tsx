import {Tabs} from "antd";
import MovableSidebar from "@/components/Sidebar/MovableSidebar";
import React from "react";
import {connect} from "@@/exports";
import {StateType, VIEW} from "@/pages/DataStudio/model";
import {AppstoreAddOutlined, DatabaseOutlined, TableOutlined} from "@ant-design/icons";
import MetaData from "@/pages/DataStudio/LeftContainer/MetaData";
import {LeftSide} from "@/pages/DataStudio/route";
import {l} from "@/utils/intl";

export type LeftContainerProps = {
  size:number
}
const LeftContainer:React.FC<LeftContainerProps> = (props:any) => {
  const {dispatch,size} = props;

  return(
    <MovableSidebar
      contentHeight={props.toolContentHeight}
      onResize={(event: any, direction: any, elementRef: { offsetWidth: any; }, delta: any) => {
        dispatch({
          type: 'Studio/updateLeftWidth',
          payload: elementRef.offsetWidth,
        })
      }}
      title={props.leftContainer.selectKey===''?"":l(props.leftContainer.selectKey)}
      handlerMinimize={() => {
        dispatch({
          type: 'Studio/updateSelectLeftKey',
          payload: "",
        })
      }}
      visible={props.leftContainer.selectKey !== ""}
      defaultSize={{
        width: props.leftContainer.width,
        height: props.leftContainer.height
      }}
      minWidth={200}
      maxWidth={size.width - 2 * VIEW.sideWidth - props.rightContainer.width - 200}
      enable={{right: true}}
    >
      <Tabs activeKey={props.leftContainer.selectKey} items={LeftSide}
            tabBarStyle={{display: "none"}}
      />
    </MovableSidebar>
  )
}

export default connect(({Studio}: { Studio: StateType }) => ({
  leftContainer: Studio.leftContainer,
  rightContainer: Studio.rightContainer,
  toolContentHeight: Studio.toolContentHeight,
}))(LeftContainer);


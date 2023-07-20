/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

import {StateType, VIEW} from "@/pages/DataStudio/model";
import MovableSidebar from "@/components/Sidebar/MovableSidebar";
import {l} from "@/utils/intl";
import {Button, Space, Tabs} from "antd";
import {CloseSquareOutlined, CopyOutlined, QuestionOutlined} from "@ant-design/icons";
import React from "react";
import {connect} from "@@/exports";
import {mapDispatchToProps} from "@/pages/DataStudio/function";
import {LeftSide, RightSide} from "@/pages/DataStudio/route";

export type RightContainerProps = {
  size:number
  bottomHeight:number
}
const RightContainer:React.FC<RightContainerProps> = (prop:any) => {
  const {size,leftContainer,rightContainer,bottomHeight,updateRightWidth,updateSelectRightKey}=prop
  const maxWidth= size.width - 2 * VIEW.leftToolWidth - leftContainer.width - 600
  return (
    <MovableSidebar
      contentHeight={size.contentHeight - VIEW.midMargin - bottomHeight}
      onResize={(event: any, direction: any, elementRef: {
        offsetWidth: any;
      }) => updateRightWidth(elementRef.offsetWidth)}
      title={<h5>{l(rightContainer.selectKey)}</h5>}
      handlerMinimize={() => updateSelectRightKey("")}
      handlerMaxsize={() => updateRightWidth(maxWidth)}
      visible={rightContainer.selectKey !== ""}
      defaultSize={{width: rightContainer.width, height: rightContainer.height}}
      minWidth={300}
      maxWidth={maxWidth}
      enable={{left: true}}
      style={{borderInlineStart: "1px solid #E0E2E5"}}
    >
      <Tabs activeKey={rightContainer.selectKey} items={RightSide} tabBarStyle={{display: "none"}}/>

    </MovableSidebar>
  )
};

export default connect(({Studio}: { Studio: StateType }) => ({
  leftContainer: Studio.leftContainer,
  rightContainer: Studio.rightContainer,
  bottomContainer: Studio.bottomContainer,
  activeBreadcrumbTitle: Studio.tabs.activeBreadcrumbTitle,
  tabs: Studio.tabs,
}), mapDispatchToProps)(RightContainer);

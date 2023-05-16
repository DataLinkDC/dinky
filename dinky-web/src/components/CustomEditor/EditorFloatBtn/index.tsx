/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React from "react";
import {FloatButton, Space} from "antd";
import {l} from "@/utils/intl";
import {DownCircleFilled, StopFilled, SyncOutlined, UpCircleFilled} from "@ant-design/icons";
import {PlayCircleFilled} from "@ant-design/icons/lib/icons";

/**
 * props
 */
type EditFloatBtnProps = {
  refreshLogCallback?: () => void; // refresh log callback
  autoRefresh?: boolean; // auto refresh flag
  stopping?: boolean; // stop auto refresh flag
  loading?: boolean; // loading flag
  handleSyncLog?: () => void; // sync log callback
  handleStopAutoRefresh?: () => void; // stop auto refresh callback
  handleStartAutoRefresh?: () => void; // start auto refresh callback
  handleBackTop?: () => void;  // back to top callback
  handleBackBottom?: () => void; // back to bottom callback
  handleUpScroll?: () => void; // up scroll callback
  handleDownScroll?: () => void; // down scroll callback
}
const EditorFloatBtn: React.FC<EditFloatBtnProps> = (props) => {

  /**
   * init props
   */
  const {
    refreshLogCallback,
    autoRefresh,
    stopping,
    loading,
    handleSyncLog,
    handleStopAutoRefresh,
    handleStartAutoRefresh,
    handleBackTop,
    handleBackBottom,
    handleUpScroll,
    handleDownScroll
  } = props;


  /**
   * render
   */
  return <>
    <FloatButton.Group
      shape="circle"
      className={"float-button"}
    >
      <Space direction={"vertical"}>
        {/* if refreshLogCallback is not null, then show sync button , auto refresh button and stop auto refresh button  */}
        {refreshLogCallback &&
          <>
            <FloatButton
              tooltip={l("button.refresh")}
              icon={<SyncOutlined spin={loading}/>}
              onClick={handleSyncLog}
            />

            {autoRefresh ?
              <FloatButton
                icon={<StopFilled spin={stopping}/>}
                tooltip={l("button.stopRefresh")}
                onClick={handleStopAutoRefresh}
              /> :
              <FloatButton
                icon={<PlayCircleFilled/>}
                tooltip={l("button.startRefresh")}
                onClick={handleStartAutoRefresh}
              />
            }
          </>
        }
        {/* back top */}
        <FloatButton.BackTop
          tooltip={l("button.backTop")}
          onClick={handleBackTop}
          visibilityHeight={0}
        />
        {/* go bottom */}
        <FloatButton.BackTop
          className={"back-bottom"}
          tooltip={l("button.backBottom")}
          onClick={handleBackBottom}
          visibilityHeight={0}
        />
        {/* scroll up */}
        <FloatButton
          icon={<UpCircleFilled/>}
          tooltip={l("button.upScroll")}
          onClick={handleUpScroll}
        />
        {/* scroll down */}
        <FloatButton
          icon={<DownCircleFilled/>}
          tooltip={l("button.downScroll")}
          onClick={handleDownScroll}
        />
      </Space>
    </FloatButton.Group>
  </>;

};

export default EditorFloatBtn;

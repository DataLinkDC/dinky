/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import { CircleBtn } from '@/components/CallBackButton/CircleBtn';
import { l } from '@/utils/intl';
import {
  CloudDownloadOutlined,
  DownCircleFilled,
  EnterOutlined,
  StopFilled,
  SyncOutlined,
  UpCircleFilled,
  VerticalAlignBottomOutlined,
  VerticalAlignTopOutlined
} from '@ant-design/icons';
import { PlayCircleFilled } from '@ant-design/icons/lib/icons';
import { Space } from 'antd';
import React from 'react';

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
  handleBackTop?: () => void; // back to top callback
  handleBackBottom?: () => void; // back to bottom callback
  handleUpScroll?: () => void; // up scroll callback
  handleDownScroll?: () => void; // down scroll callback
  handleDownloadLog?: () => string; // download log callback
  handleWrap?: () => void; // wrap callback
};
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
    handleDownScroll,
    handleDownloadLog,
    handleWrap
  } = props;

  /**
   * render
   */
  return (
    <>
      <Space direction={'vertical'} align={'center'} size={2}>
        {refreshLogCallback && (
          <>
            <CircleBtn
              icon={<SyncOutlined spin={loading} />}
              onClick={handleSyncLog}
              title={l('button.refresh')}
            />
            {autoRefresh ? (
              <CircleBtn
                icon={<StopFilled spin={stopping} />}
                onClick={handleStopAutoRefresh}
                title={l('button.stopRefresh')}
              />
            ) : (
              <CircleBtn
                icon={<PlayCircleFilled spin={loading} />}
                onClick={handleStartAutoRefresh}
                title={l('button.startRefresh')}
              />
            )}
          </>
        )}
        <CircleBtn
          icon={<VerticalAlignTopOutlined />}
          onClick={handleBackTop}
          title={l('button.backTop')}
        />
        <CircleBtn
          icon={<VerticalAlignBottomOutlined />}
          onClick={handleBackBottom}
          title={l('button.backBottom')}
        />
        <CircleBtn
          icon={<UpCircleFilled />}
          onClick={handleUpScroll}
          title={l('button.upScroll')}
        />
        <CircleBtn
          icon={<DownCircleFilled />}
          onClick={handleDownScroll}
          title={l('button.downScroll')}
        />
        <CircleBtn
          icon={<CloudDownloadOutlined />}
          href={handleDownloadLog ? handleDownloadLog() : '123 '}
          title={'Download'}
        />
        <CircleBtn
          icon={<EnterOutlined />}
          onClick={() => handleWrap && handleWrap()}
          title={l('button.wrap')}
        />
      </Space>
    </>
  );
};

export default EditorFloatBtn;

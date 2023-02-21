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

import { VERSION } from '@/components/Version/Version';
import { l } from '@/utils/intl';
import { FullscreenOutlined, GlobalOutlined } from '@ant-design/icons';
import { useEmotionCss } from '@ant-design/use-emotion-css';
import { SelectLang, useModel } from '@umijs/max';
import { Space, Tooltip } from 'antd';
import React, { useState } from 'react';
import screenfull from 'screenfull';
import Avatar from './AvatarDropdown';

export type SiderTheme = 'light' | 'dark';

const GlobalHeaderRight: React.FC = () => {
  const [fullScreen, setFullScreen] = useState(false);

  const actionClassName = useEmotionCss(({ token }) => {
    return {
      display: 'flex',
      float: 'right',
      height: '48px',
      marginLeft: 'auto',
      overflow: 'hidden',
      cursor: 'pointer',
      padding: '0 12px',
      color: '#fff',
      borderRadius: token.borderRadius,
      '&:hover': {
        backgroundColor: token.colorBgTextHover,
      },
    };
  });

  const fullScreenClassName = useEmotionCss(({ token }) => {
    return {
      display: 'flex',
      float: 'right',
      height: '48px',
      marginLeft: 'auto',
      overflow: 'hidden',
      cursor: 'pointer',
      padding: '0 12px',
      borderRadius: token.borderRadius,
      color: 'red',
      '&:hover': {
        backgroundColor: token.colorPrimary,
      },
    };
  });

  const { initialState } = useModel('@@initialState');

  if (!initialState || !initialState.settings) {
    return null;
  }

  const screenFull = () => {
    setFullScreen(screenfull.isFullscreen);
    if (screenfull.isEnabled) {
      screenfull.toggle();
    }
  };

  return (
    <>
      <Tooltip
        placement="bottom"
        title={<span>{fullScreen ? l('global.fullScreen') : l('global.fullScreen.exit')}</span>}
      >
        <FullscreenOutlined
          style={{ color: 'white' }}
          className={fullScreenClassName}
          onClick={screenFull}
        />
      </Tooltip>
      <Avatar menu={true} />
      <SelectLang icon={<GlobalOutlined />} className={actionClassName} />
      <Space className={actionClassName}>{l('menu.version', '', { version: VERSION })}</Space>
    </>
  );
};
export default GlobalHeaderRight;

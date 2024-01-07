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

import { ThemeCloud, ThemeStar } from '@/components/ThemeSvg/ThemeSvg';
import { queryDataByParams } from '@/services/BusinessCrud';
import { LANGUAGE_KEY, LANGUAGE_ZH, STORY_LANGUAGE } from '@/services/constants';
import { API_CONSTANTS } from '@/services/endpoints';
import { THEME } from '@/types/Public/data';
import { useLocalStorage } from '@/utils/hook/useLocalStorage';
import { l } from '@/utils/intl';
import { FullscreenExitOutlined, FullscreenOutlined, GlobalOutlined } from '@ant-design/icons';
import { SelectLang, useModel } from '@umijs/max';
import { Space, Switch, Tooltip } from 'antd';
import React, { useEffect, useState } from 'react';
import useCookie from 'react-use-cookie';
import screenfull from 'screenfull';
import Avatar from './AvatarDropdown';

const GlobalHeaderRight: React.FC = () => {
  /**
   * status
   */
  const [fullScreen, setFullScreen] = useState(true);
  const { initialState, setInitialState } = useModel('@@initialState');
  const [theme, setTheme] = useLocalStorage(THEME.NAV_THEME, initialState?.settings?.navTheme);
  const [language, setLanguage] = useLocalStorage(LANGUAGE_KEY, LANGUAGE_ZH);
  const [langCache, setLangCache] = useCookie(STORY_LANGUAGE, language);
  const [serviceVersion, setServiceVersion] = useState<string>('');

  useEffect(() => {
    setLangCache(language);
    (async () =>
      await setInitialState((initialStateType: any) => ({
        ...initialStateType,
        locale: language,
        settings: {
          ...initialStateType?.settings,
          navTheme: theme,
          token: {
            ...initialStateType?.settings?.token,
            sider: {
              ...initialStateType?.settings?.token?.sider,
              colorMenuBackground: theme === THEME.dark ? '#000' : '#fff'
            }
          }
        }
      })))();
  }, [theme, language]);

  useEffect(() => {
    queryDataByParams<string>(API_CONSTANTS.GET_SERVICE_VERSION).then((res) => {
      if (res) setServiceVersion(res);
    });
  }, []);

  if (!initialState || !initialState.settings) {
    return null;
  }

  /**
   * css
   */
  const actionClassName: any = {
    display: 'flex',
    float: 'right',
    justifyContent: 'center',
    alignItems: 'center',
    height: '48px',
    marginLeft: 'auto',
    overflow: 'hidden',
    cursor: 'pointer',
    padding: '0 9px',
    color: '#fff',
    '&:hover': {
      backgroundColor: '#fff'
    }
  };

  /**
   * full screen css
   */
  const fullScreenClassName = {
    display: 'flex',
    float: 'right',
    height: '48px',
    marginLeft: 'auto',
    overflow: 'hidden',
    cursor: 'pointer',
    padding: '0 12px',
    color: '#fff',
    '&:hover': {
      backgroundColor: '#fff'
    }
  };

  /**
   * full screen or exit full screen
   */
  const screenFull = () => {
    setFullScreen(screenfull.isFullscreen);
    if (screenfull.isEnabled) {
      (async () => await screenfull.toggle())();
    }
  };

  const fullScreenProps = {
    style: fullScreenClassName
  };

  const menuVersion = l('menu.version', '', { version: serviceVersion });
  return (
    <>
      <Tooltip
        placement='bottom'
        title={<span>{fullScreen ? l('global.fullScreen') : l('global.fullScreen.exit')}</span>}
      >
        {fullScreen ? (
          <FullscreenOutlined {...(fullScreenProps as any)} onClick={screenFull} />
        ) : (
          <FullscreenExitOutlined {...(fullScreenProps as any)} onClick={screenFull} />
        )}
      </Tooltip>
      <Tooltip placement='bottom' title={<span>{menuVersion}</span>}>
        <Space style={actionClassName}>{menuVersion}</Space>
      </Tooltip>
      <SelectLang icon={<GlobalOutlined />} style={actionClassName} />
      <Switch
        key={'themeSwitch'}
        checked={theme === THEME.dark}
        checkedChildren={<ThemeCloud />}
        unCheckedChildren={<ThemeStar />}
        onChange={(value) => setTheme(value ? THEME.dark : THEME.light)}
      />
      <Avatar />
    </>
  );
};

export default GlobalHeaderRight;

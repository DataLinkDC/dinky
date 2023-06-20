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

import {LANGUAGE_KEY, LANGUAGE_ZH, STORY_LANGUAGE, VERSION} from '@/services/constants';
import {l} from "@/utils/intl";
import useCookie from 'react-use-cookie';
import {FullscreenExitOutlined, FullscreenOutlined, GlobalOutlined} from "@ant-design/icons";
import {useEmotionCss} from "@ant-design/use-emotion-css";
import {SelectLang, useModel} from "@umijs/max";
import {Space, Switch, Tooltip} from "antd";
import React, {useEffect, useState} from "react";
import screenfull from "screenfull";
import Avatar from "./AvatarDropdown";
import {ThemeCloud, ThemeStar} from "@/components/ThemeSvg/ThemeSvg";
import {THEME} from "@/types/Public/data";
import {useLocalStorage} from "@/utils/hook/useLocalStorage";


const GlobalHeaderRight: React.FC = () => {
  /**
   * status
   */
  const [fullScreen, setFullScreen] = useState(true);
  const {initialState, setInitialState} = useModel("@@initialState");
  const [theme, setTheme] = useLocalStorage(THEME.NAV_THEME, initialState?.settings?.navTheme);
  const [language, setLanguage] = useLocalStorage(LANGUAGE_KEY, LANGUAGE_ZH);
  const [langCache, setLangCache] = useCookie(STORY_LANGUAGE, language);

  useEffect(() => {
    (async () => await setInitialState((initialStateType) => ({
      ...initialStateType,
      locale: language,
      settings: {
        ...initialStateType?.settings,
        navTheme: theme,
        colorMenuBackground: (theme === THEME.dark ? "transparent" : "#fff")
      }
    })))();
  }, [theme, language]);

  function changeHandler(value: boolean) {
    setTheme(value ? THEME.dark : THEME.light)
    setLangCache(STORY_LANGUAGE, language);
  }

  if (!initialState || !initialState.settings) {
    return null;
  }

  /**
   * css
   */
  const actionClassName = useEmotionCss(({token}) => {
    return {
      display: "flex",
      float: "right",
      justifyContent: "center",
      alignItems: "center",
      height: "48px",
      marginLeft: "auto",
      overflow: "hidden",
      cursor: "pointer",
      padding: "0 9px",
      color: "#fff",
      borderRadius: token.borderRadius,
      "&:hover": {
        backgroundColor: token.colorBgTextHover,
      },
    };
  });

  /**
   * full screen css
   */
  const fullScreenClassName = useEmotionCss(({token}) => {
    return {
      display: "flex",
      float: "right",
      height: "48px",
      marginLeft: "auto",
      overflow: "hidden",
      cursor: "pointer",
      padding: "0 12px",
      borderRadius: token.borderRadius,
      color: "red",
      "&:hover": {
        backgroundColor: token.colorPrimary,
      },
    };
  });


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
    style: {color: "white"},
    className: fullScreenClassName
  };

  const menuVersion = l("menu.version", "", {version: VERSION});
  return (
    <>
      <Tooltip placement="bottom"
               title={<span>{fullScreen ? l("global.fullScreen") : l("global.fullScreen.exit")}</span>}>
        {fullScreen ? <FullscreenOutlined {...fullScreenProps} onClick={screenFull}/> :
          <FullscreenExitOutlined {...fullScreenProps} onClick={screenFull}/>}
      </Tooltip>
      <Avatar/>
      <Tooltip placement="bottom" title={<span>{menuVersion}</span>}>
        <Space className={actionClassName}>{menuVersion}</Space>
      </Tooltip>
      <SelectLang icon={<GlobalOutlined/>} className={actionClassName}/>
      <Switch
        key={"themeSwitch"}
        checked={theme === THEME.dark}
        checkedChildren={<ThemeCloud/>}
        unCheckedChildren={<ThemeStar/>}
        onChange={changeHandler}/>
    </>
  );
};


export default GlobalHeaderRight;

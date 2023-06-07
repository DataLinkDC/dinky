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

import {STORY_LANGUAGE, VERSION} from '@/services/constants';
import {
  getLocalStorageLanguage, getValueFromLocalStorage,
  parseJsonStr,
  setCookieByKey,
  setKeyToLocalStorage,
  setTenantStorageAndCookie
} from '@/utils/function';
import {l} from "@/utils/intl";
import {
  FullscreenExitOutlined,
  FullscreenOutlined,
  GlobalOutlined
} from "@ant-design/icons";
import {ActionType} from "@ant-design/pro-components";
import {useEmotionCss} from "@ant-design/use-emotion-css";
import {SelectLang, useModel} from "@umijs/max";
import {Modal, Select, Space, Switch, Tooltip} from "antd";
import {OptionType} from "dayjs";
import React, {useEffect, useRef, useState} from "react";
import screenfull from "screenfull";
import Avatar from "./AvatarDropdown";
import {ThemeCloud, ThemeStar} from "@/components/ThemeSvg/ThemeSvg";
import {chooseTenantSubmit} from "@/services/BusinessCrud";
import {ErrorNotification, SuccessNotification} from "@/utils/messages";
import {THEME} from "@/types/Public/data";
import cookies from 'js-cookie';


const GlobalHeaderRight: React.FC = () => {
  /**
   * status
   */
  const [fullScreen, setFullScreen] = useState(true);
  const [themeChecked, setThemeChecked] = useState(false);
  const {initialState, setInitialState} = useModel("@@initialState");
  const {currentUser,settings} = initialState || {};

  /**
   * init render theme status
   */
  useEffect(() => {
    const theme :any = getValueFromLocalStorage(THEME.NAV_THEME) !== undefined ? getValueFromLocalStorage(THEME.NAV_THEME) :  settings?.navTheme
    setThemeChecked(theme === THEME.dark ? true : false);
    setInitialState((preInitialState) => {
      return {
        ...preInitialState,
        settings: {
          ...initialState?.settings, navTheme: theme
        }
      };
    });

  }, []);

  useEffect(() => {
    const lang = getLocalStorageLanguage();
    if (lang) {
      setCookieByKey(STORY_LANGUAGE, lang);
      setInitialState((s) => ({
        ...s,
        locale: lang,
      }));
    }
  }, [initialState?.settings?.locale]);


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
   *
   * @param option
   */
  const tenantHandleChange = (option: OptionType) => {
    const result = parseJsonStr(option as string);
    const tenantId = result.value;

    Modal.confirm({
      title: l("menu.account.checkTenant"),
      content: l("menu.account.checkTenantConfirm", "", {tenantCode: result.children}),
      okText: l("button.confirm"),
      cancelText: l("button.cancel"),
      onOk: async () => {
        const result = await chooseTenantSubmit({tenantId});
        setTenantStorageAndCookie(tenantId);
        if (result.code === 0) {
          SuccessNotification(result.msg);
        } else {
          ErrorNotification(result.msg);
        }
        // trigger global refresh, such as reload page
        window.location.reload();
      },
    });
  };

  /**
   * full screen or exit full screen
   */
  const screenFull = () => {
    setFullScreen(screenfull.isFullscreen);
    if (screenfull.isEnabled) {
      screenfull.toggle();
    }
  };

  /**
   * generate tenant list card
   */
  const genTenantListForm = () => {
    const tenants: any[] = [];
    currentUser?.tenantList?.forEach((item) => {
      tenants.push(
        <Select.Option key={item.id} value={item.id}>
          {item.tenantCode}
        </Select.Option>,
      );
    });
    return tenants;
  };

  const fullScreenProps = {
    style: {color: "white"},
    className: fullScreenClassName
  };

  return (
    <>
      <Tooltip placement="bottom"
               title={<span>{fullScreen ? l("global.fullScreen") : l("global.fullScreen.exit")}</span>}>
        {fullScreen ? <FullscreenOutlined {...fullScreenProps} onClick={screenFull}/> :
          <FullscreenExitOutlined {...fullScreenProps} onClick={screenFull}/>}
      </Tooltip>
      <Avatar menu={true}/>
      <>
        <span className={actionClassName}>{l("menu.tenants")}</span>
        <Select
          className={actionClassName}
          style={{width: "18vh"}}
          value={currentUser?.currentTenant?.tenantCode?.toString()}
          defaultValue={currentUser?.currentTenant?.tenantCode?.toString() || ""}
          onChange={(value, option) => {
            tenantHandleChange(option as OptionType);
          }}
        >
          {genTenantListForm()}
        </Select>
      </>

      <Tooltip
        placement="bottom"
        title={<span>{l("menu.version", "", {version: VERSION})}</span>}
      >
        <Space className={actionClassName}>{l("menu.version", "", {version: VERSION})}</Space>
      </Tooltip>

      <SelectLang icon={<GlobalOutlined/>} className={actionClassName}/>
      <Switch
        key={"themeSwitch"}
        checked={themeChecked}
        checkedChildren={<ThemeCloud/>}
        unCheckedChildren={<ThemeStar/>}
        onChange={(value) => {
          setKeyToLocalStorage(THEME.NAV_THEME, !value ? THEME.light : THEME.dark);
          setInitialState((preInitialState :any) => {
            return {
              ...preInitialState,
              settings: {
                ...settings, navTheme: !value ? THEME.light : THEME.dark,colorMenuBackground: (getValueFromLocalStorage(THEME.NAV_THEME) === THEME.dark ? "transparent" : "#fff")
              }
            };
          });
          setThemeChecked(value);
        }}/>
    </>
  );
};


export default GlobalHeaderRight;

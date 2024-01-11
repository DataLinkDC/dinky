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

import { theme } from 'antd';
import React, {useEffect} from 'react';
import {THEME} from "@/types/Public/data";
import { SelectLang, useModel } from '@umijs/max';
import {useLocalStorage} from "@/utils/hook/useLocalStorage";
import {LANGUAGE_EN, LANGUAGE_KEY, LANGUAGE_ZH, STORY_LANGUAGE} from "@/services/constants";
import useCookie from "react-use-cookie";

export type TitleProps = {
  children?: React.ReactNode;
};
const { useToken } = theme;

const Title: React.FC<TitleProps> = (props) => {
  const { token } = useToken();
  const { initialState, setInitialState } = useModel('@@initialState');
  const [theme2, setTheme] = useLocalStorage(THEME.NAV_THEME, initialState?.settings?.navTheme);
  const [language, setLanguage] = useLocalStorage(LANGUAGE_KEY, LANGUAGE_ZH);
  const [langCache, setLangCache] = useCookie(STORY_LANGUAGE, language);

  useEffect(() => {
    setLangCache(language);
    (async () =>
        await setInitialState((initialStateType: any) => ({
          ...initialStateType,
          locale: language,
          settings: {
            ...initialStateType?.settings,
            navTheme: theme2,
            token: {
              ...initialStateType?.settings?.token,
              sider: {
                ...initialStateType?.settings?.token?.sider,
                colorMenuBackground: theme2 === THEME.dark ? '#000' : '#fff'
              }
            }
          }
        })))();
  }, [theme2, language]);

  window.onmessage=function(event){
    if(event.data.type == "setMode"){
      setTheme(event.data.mode == "light" ? THEME.light: THEME.dark);
    }else if(event.data.type == "setLocale"){
      setLanguage(event.data.locale);
    }
  }

  return (
    <span className={'title'} style={{ color: token.colorText }}>
      {props.children}
    </span>
  );
};
export default Title;

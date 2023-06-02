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

import {useEmotionCss} from "@ant-design/use-emotion-css";
import {SelectLang, useModel} from '@@/exports';
import React, {useEffect} from 'react';
import {getLocalStorageLanguage, setCookieByKey} from '@/utils/function';
import {STORY_LANGUAGE} from '@/services/constants';

const LangSwitch = () => {

  const {initialState, setInitialState} = useModel("@@initialState");

  useEffect(() => {
    const lang = getLocalStorageLanguage();
    if (lang) {
      setCookieByKey(STORY_LANGUAGE, lang);
      setInitialState((s) => ({
        ...s,
        locale: lang,
      }));
    }
  }, [initialState]);


  const langClassName = useEmotionCss(({token}) => {
    return {
      width: 42,
      lineHeight: "42px",
      position: "absolute",
      top: 10,
      right: 10,
      borderRadius: token.borderRadius,
      zIndex: 9999,
      ":hover": {
        backgroundColor: token.colorBgTextHover,
      },
    };
  });
  return <div className={langClassName}>
    {SelectLang && <SelectLang/>}
  </div>
    ;
};

export default LangSwitch;

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

import { ENABLE_MODEL_TIP, SERVER_VERSION } from '@/services/constants';
import {
  getValueFromLocalStorage,
  hasKeyofLocalStorage,
  setKeyToLocalStorage,
  setLocalThemeToStorage
} from '@/utils/function';
import { WarningMessageAsync } from '@/utils/messages';
import { history } from '@@/core/history';
import { queryDataByParams } from '@/services/BusinessCrud';
import { API_CONSTANTS } from '@/services/endpoints';

/** This method will redirect to the location of the redirect parameter */
export const gotoRedirectUrl = () => {
  if (!history) return;
  setTimeout(() => {
    const urlParams = new URL(window.location.href).searchParams;
    history.replace(urlParams.get('redirect') || '/');
  }, 10);
};

export const redirectToLogin = (tipMsg: string) => {
  //todo: Using modal box prompts, but currently it will pop up repeatedly because the interface is called every time, so there will be repeated pop ups
  WarningMessageAsync(tipMsg);
  window.location.href = '/#/user/login';
};

export const initSomeThing = () => {
  //  initialize setting theme
  setLocalThemeToStorage();
  queryDataByParams<string>(API_CONSTANTS.GET_SERVICE_VERSION, { isExternalCall: false }).then(
    (result) => {
      if (result && result != getValueFromLocalStorage(SERVER_VERSION)) {
        console.log('current version:', getValueFromLocalStorage(SERVER_VERSION));
        console.log('update server version:', result);
        setKeyToLocalStorage(SERVER_VERSION, result);
        console.log('clean dva cache');
        window.localStorage.removeItem('persist:root');
      }
    }
  );

  // Retrieve the key for enabling message prompts from the local storage, and if not, set it accordingly
  if (hasKeyofLocalStorage(ENABLE_MODEL_TIP)) {
    setKeyToLocalStorage(ENABLE_MODEL_TIP, 'false');
  }
};

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

import { ENABLE_MODEL_TIP } from '@/services/constants';
import {
  hasKeyofLocalStorage,
  setKeyToLocalStorage,
  setLocalThemeToStorage
} from '@/utils/function';
import { WarningMessageAsync } from '@/utils/messages';
import { history } from '@@/core/history';

/** This method will redirect to the location of the redirect parameter */
export const gotoRedirectUrl = () => {
  if (!history) return;
  setTimeout(() => {
    const urlParams = new URL(window.location.href).searchParams;
    history.replace(urlParams.get('redirect') || '/');
  }, 10);
};

export const redirectToLogin = (tipMsg: string) => {
  //todo: 使用模态框提示, 但是目前会重复弹出,原因是接口每次都会调用，所以会出现重复弹出
  WarningMessageAsync(tipMsg);
  window.location.href = '/#/user/login';
};

export const initSomeThing = () => {
  //  initialize setting theme
  setLocalThemeToStorage();
  // Retrieve the key for enabling message prompts from the local storage, and if not, set it accordingly
  if (hasKeyofLocalStorage(ENABLE_MODEL_TIP)) {
    setKeyToLocalStorage(ENABLE_MODEL_TIP, 'false');
  }
};

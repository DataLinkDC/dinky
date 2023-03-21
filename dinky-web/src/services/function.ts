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

import {TENANT_ID} from '@/services/constants';
import cookies from 'js-cookie';
import {message} from "antd";

/**
 * PUT tenantId TO localStorage & cookies
 * @param tenantId
 */
export function setTenantStorageAndCookie(tenantId: number) {
  // save as localStorage
  localStorage.setItem(TENANT_ID, tenantId.toString());
  // save as cookies
  cookies.set(TENANT_ID, tenantId.toString(), {path: '/'});
}

/**
 * get tenant id
 * @param tenantId
 */
export function getTenantByLocalStorage() {
  return localStorage.getItem(TENANT_ID);
}

/**
 * parseJsonStr
 * @param jsonStr
 */
export function parseJsonStr(jsonStr: string) {
  return JSON.parse(JSON.stringify(jsonStr));
}

/**
 * show message tips of response
 * @param code
 * @param msg
 */
export const showMsgTips = ({code, msg}: { code: number, msg: string }) => {
  if (code === NetWork.RESPONSE_CODE.SUCCESS) {
    message.success(msg);
  } else {
    message.warning(msg);
  }
}

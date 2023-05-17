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

import {
  addOrUpdateData, putData, getDataByRequestBody, getInfoById,
  postAll,
  removeById, updateDataByParams, getData,
} from "@/services/api";
import {l} from "@/utils/intl";
import {API_CONSTANTS, METHOD_CONSTANTS, RESPONSE_CODE} from "@/services/constants";
import {request} from "@@/exports";
import {
  LoadingMessageAsync, SuccessMessage, WarningMessage,
} from "@/utils/messages";


const APPLICATION_JSON = "application/json";

// ================================ About Account ================================
/**
 * get current user
 * @param options
 */
export async function currentUser(options?: { [key: string]: any }) {
  return request<API.Result>(API_CONSTANTS.CURRENT_USER, {
    method: METHOD_CONSTANTS.GET,
    ...(options || {}),
  });
}

/**
 * user logout
 * @param options
 */
export async function outLogin(options?: { [key: string]: any }) {
  return request<Record<string, any>>(API_CONSTANTS.LOGOUT, {
    method: METHOD_CONSTANTS.DELETE,
    ...(options || {}),
  });
}

/**
 * user login
 * @param body
 * @param options
 */
export async function login(body: API.LoginParams, options?: { [key: string]: any }) {
  return request<API.Result>(API_CONSTANTS.LOGIN, {
    method: METHOD_CONSTANTS.POST,
    headers: {
      CONTENT_TYPE: APPLICATION_JSON,
    },
    data: body,
    ...(options || {}),
  });
}

/**
 * choose tenant
 * @param params
 */
export function chooseTenantSubmit(params: { tenantId: number }) {
  return request<API.Result>(API_CONSTANTS.CHOOSE_TENANT, {
    method: METHOD_CONSTANTS.POST,
    params: {
      ...(params || {}),
    },
  });
}


// ================================ About crud ================================
/**
 * add or update data
 * @param url
 * @param params
 */
export const handleAddOrUpdate = async (url: string, params: any) => {
  const tipsTitle = params.id ? l("app.request.update") : l("app.request.add");
  await LoadingMessageAsync(l("app.request.running") + tipsTitle);
  try {
    const {code, msg} = await addOrUpdateData(url, {...params});
    if (code === RESPONSE_CODE.SUCCESS) {
      SuccessMessage(msg);
    } else {
      WarningMessage(msg);
    }
    return true;
  } catch (error) {
    return false;
  }
};

/**
 * delete by id
 * @param url
 * @param id
 */
export const handleRemoveById = async (url: string, id: number) => {
  await LoadingMessageAsync(l("app.request.delete"));
  try {
    const {code, msg} = await removeById(url, {id});
    if (code === RESPONSE_CODE.SUCCESS) {
      SuccessMessage(msg);
    } else {
      WarningMessage(msg);
    }
    return true;
  } catch (error) {
    return false;
  }
};
/**
 * update enabled status
 * @param url
 * @param params
 */
export const updateEnabled = async (url: string, params: any) => {
  await LoadingMessageAsync(l("app.request.update"));
  try {
    const {code, msg} = await updateDataByParams(url, {...params});
    if (code === RESPONSE_CODE.SUCCESS) {
      SuccessMessage(msg);
    } else {
      WarningMessage(msg);
    }
    return true;
  } catch (error) {
    return false;
  }
};

export const handleOption = async (url: string, title: string, param: any) => {
  await LoadingMessageAsync(l("app.request.running") + title);
  try {
    const {code, msg} = await postAll(url, param);
    if (code === RESPONSE_CODE.SUCCESS) {
      SuccessMessage(msg);
    } else {
      WarningMessage(msg);
    }
    return true;
  } catch (error) {
    return false;
  }
};

export const handleData = async (url: string, id: any) => {
  try {
    const {code, datas} = await getInfoById(url, id);
    if (code === RESPONSE_CODE.SUCCESS) {
      return datas;
    } else {
      return false;
    }
  } catch (error) {
    return false;
  }
};


export const handlePutData = async (url: string, fields: any) => {
  const tipsTitle = fields.id ? l("app.request.update") : l("app.request.add");
  await LoadingMessageAsync(l("app.request.running") + tipsTitle);
  try {
    const {code, msg} = await postAll(url, {...fields});
    if (code === RESPONSE_CODE.SUCCESS) {
      SuccessMessage(msg);
    } else {
      WarningMessage(msg);
    }
    return true;
  } catch (error) {
    return false;
  }
};


export const getDataByParams = async (url: string, params?: any) => {
  try {
    const {datas} = await getDataByRequestBody(url, params);
    return datas;
  } catch (error) {
    return false;
  }
};

export const queryDataByParams = async (url: string, params?: any) => {
  try {
    const {datas} = await getData(url, params);
    return datas;
  } catch (error) {
    return false;
  }
};


export const handlePutDataByParams = async (url: string, title: string, params: any) => {
  await LoadingMessageAsync(l("app.request.running") + title);
  try {
    const {code, msg} = await putData(url, {...params});
    if (code === RESPONSE_CODE.SUCCESS) {
      SuccessMessage(msg);
      return true;
    } else {
      WarningMessage(msg);
      return false;
    }
  } catch (error) {
    return false;
  }
};

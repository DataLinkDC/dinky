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

import {
  addOrUpdateData,
  getData,
  getDataByRequestBody,
  getInfoById,
  postAll,
  putData,
  putDataJson,
  removeById,
  updateDataByParams
} from '@/services/api';
import { METHOD_CONSTANTS, RESPONSE_CODE } from '@/services/constants';
import { API_CONSTANTS } from '@/services/endpoints';
import { l } from '@/utils/intl';
import { LoadingMessageAsync, SuccessMessage, WarningMessage } from '@/utils/messages';
import { request } from '@@/exports';
import { API } from './data';

const APPLICATION_JSON = 'application/json';

/**
 * user logout
 * @param options
 */
export async function outLogin(options?: { [key: string]: any }) {
  return request<Record<string, any>>(API_CONSTANTS.LOGOUT, {
    method: METHOD_CONSTANTS.DELETE,
    ...(options ?? {})
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
      CONTENT_TYPE: APPLICATION_JSON
    },
    data: body,
    ...(options ?? {})
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
      ...(params || {})
    }
  });
}

// ================================ About crud ================================
/**
 * add or update data
 * @param url
 * @param params
 * @param beforeCallBack
 * @param afterCallBack
 */
export const handleAddOrUpdate = async (
  url: string,
  params: any,
  beforeCallBack?: () => void,
  afterCallBack?: () => void
) => {
  const tipsTitle = params.id ? l('app.request.update') : l('app.request.add');
  await LoadingMessageAsync(l('app.request.running') + tipsTitle);
  try {
    beforeCallBack?.();
    const { code, msg } = await addOrUpdateData(url, { ...params });
    if (code === RESPONSE_CODE.SUCCESS) {
      await SuccessMessage(msg);
      afterCallBack?.();
    } else {
      await WarningMessage(msg);
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
 * @param afterCallBack
 */
export const handleRemoveById = async (url: string, id: number, afterCallBack?: () => void) => {
  await LoadingMessageAsync(l('app.request.delete'));
  try {
    const { code, msg } = await removeById(url, { id });
    if (code === RESPONSE_CODE.SUCCESS) {
      await SuccessMessage(msg);
      afterCallBack?.();
    } else {
      await WarningMessage(msg);
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
export const updateDataByParam = async (url: string, params: any) => {
  await LoadingMessageAsync(l('app.request.running') + l('app.request.update'));
  try {
    const { code, msg } = await updateDataByParams(url, { ...params });
    if (code === RESPONSE_CODE.SUCCESS) {
      await SuccessMessage(msg);
    } else {
      await WarningMessage(msg);
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
  await LoadingMessageAsync(l('app.request.update'));
  try {
    const { code, msg } = await updateDataByParams(url, { ...params });
    if (code === RESPONSE_CODE.SUCCESS) {
      await SuccessMessage(msg);
    } else {
      await WarningMessage(msg);
    }
    return true;
  } catch (error) {
    return false;
  }
};

export const handleOption = async (
  url: string,
  title: string,
  param: any,
  afterCallBack?: () => void
) => {
  await LoadingMessageAsync(l('app.request.running') + title);
  try {
    const result = await postAll(url, param);
    if (result.code === RESPONSE_CODE.SUCCESS) {
      SuccessMessage(result.msg);
      afterCallBack?.();
      return result;
    }
    WarningMessage(result.msg);
    return undefined;
  } catch (error) {
    return undefined;
  }
};

export const handleGetOption = async (url: string, title: string, param: any) => {
  await LoadingMessageAsync(l('app.request.running') + title);
  try {
    const result = await getData(url, param);
    if (result.code === RESPONSE_CODE.SUCCESS) {
      SuccessMessage(result.msg);
      return result;
    }
    WarningMessage(result.msg);
    return undefined;
  } catch (error) {
    return undefined;
  }
};

export const handleGetOptionWithoutMsg = async (url: string, param: any) => {
  try {
    const result = await getData(url, param);
    if (result.code === RESPONSE_CODE.SUCCESS) {
      return result;
    }
    WarningMessage(result.msg);
    return undefined;
  } catch (error) {
    return undefined;
  }
};

export const handleData = async (url: string, id: any) => {
  try {
    const { code, data } = await getInfoById(url, id);
    if (code === RESPONSE_CODE.SUCCESS) {
      return data;
    }
    return undefined;
  } catch (error) {
    return undefined;
  }
};

export const handlePutData = async (url: string, fields: any) => {
  const tipsTitle = fields.id ? l('app.request.update') : l('app.request.add');
  await LoadingMessageAsync(l('app.request.running') + tipsTitle);
  try {
    const { code, msg } = await postAll(url, { ...fields });
    if (code === RESPONSE_CODE.SUCCESS) {
      await SuccessMessage(msg);
    } else {
      await WarningMessage(msg);
    }
    return true;
  } catch (error) {
    return false;
  }
};

export const handlePutDataJson = async (url: string, fields: any) => {
  const tipsTitle = fields?.id ? l('app.request.update') : l('app.request.add');
  await LoadingMessageAsync(l('app.request.running') + tipsTitle);
  try {
    const { code, msg } = await putDataJson(url, { ...fields });
    if (code === RESPONSE_CODE.SUCCESS) {
      await SuccessMessage(msg);
    } else {
      await WarningMessage(msg);
    }
    return true;
  } catch (error) {
    return false;
  }
};

export const getDataByParams = async (url: string, params?: any) => {
  try {
    const { data } = await getDataByRequestBody(url, params);
    return data;
  } catch (error) {
    return undefined;
  }
};

export const queryDataByParams = async <T>(
  url: string,
  params?: any,
  beforeCallBack?: () => void,
  afterCallBack?: () => void
): Promise<T | undefined> => {
  try {
    beforeCallBack?.();
    const { data } = await getData(url, params);
    afterCallBack?.();
    return data;
  } catch (error) {
    return undefined;
  }
};

export const handlePutDataByParams = async (
  url: string,
  title: string,
  params: any,
  afterCallBack?: () => void
) => {
  await LoadingMessageAsync(l('app.request.running') + title);
  try {
    const result = await putData(url, { ...params });
    if (result.code === RESPONSE_CODE.SUCCESS) {
      await SuccessMessage(result.msg);
      afterCallBack?.();
      return result;
    }
    await WarningMessage(result.msg);
    return undefined;
  } catch (error) {
    return undefined;
  }
};

export const getDataByIdReturnResult = async (url: string, id: any) => {
  try {
    const result = await getInfoById(url, id);
    if (result.code === RESPONSE_CODE.SUCCESS) {
      await SuccessMessage(result.msg);
      return result;
    }
    await WarningMessage(result.msg);
    return undefined;
  } catch (error) {
    return undefined;
  }
};

export const getDataByParamsReturnResult = async (url: string, params?: any) => {
  try {
    const result = await getData(url, params);
    if (result.code === RESPONSE_CODE.SUCCESS) {
      return result;
    }
    return undefined;
  } catch (error) {
    return undefined;
  }
};

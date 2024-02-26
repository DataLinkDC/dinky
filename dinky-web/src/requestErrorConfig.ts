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
import { API_CONSTANTS } from '@/services/endpoints';
import { getValueFromLocalStorage } from '@/utils/function';
import { l } from '@/utils/intl';
import { ErrorNotification, WarningNotification } from '@/utils/messages';
import { history } from '@@/core/history';
import type { RequestOptions } from '@@/plugin-request/request';
import type { RequestConfig } from '@umijs/max';

// 错误处理方案： 错误类型
enum ErrorCode {
  SUCCESS = 0,
  ERROR = 1,
  EXCEPTION = 5,
  PARAMS_ERROR = 6,
  AUTHORIZE_ERROR = 7
}

// 与后端约定的响应数据格式
interface ResponseStructure {
  success: boolean;
  data?: any;
  code: number;
  msg: string;
}

const handleBizError = (result: ResponseStructure) => {
  const { msg, code, data } = result;

  switch (code) {
    case ErrorCode.SUCCESS:
      //don't deal with it, just be happy
      break;
    case ErrorCode.ERROR:
      WarningNotification(msg, l('app.response.error'));
      break;
    case ErrorCode.EXCEPTION:
      const valueFromLocalStorage = getValueFromLocalStorage(ENABLE_MODEL_TIP);
      if (valueFromLocalStorage === 'true') {
        ErrorNotification(data, l('app.response.exception'));
      }
      break;
    case ErrorCode.PARAMS_ERROR:
      ErrorNotification(msg, l('app.response.error'));
      break;
    case ErrorCode.AUTHORIZE_ERROR:
      ErrorNotification(msg, l('app.response.notlogin'));
      break;
  }
};

/**
 * @name 错误处理
 * pro 自带的错误处理， 可以在这里做自己的改动
 * @doc https://umijs.org/docs/max/request#配置
 */
export const errorConfig: RequestConfig = {
  // 错误处理： umi@3 的错误处理方案。
  errorConfig: {
    // 错误抛出
    errorThrower: (res: ResponseStructure) => {
      const { success, data, msg, code } = res as ResponseStructure;
      if (!success) {
        const error: any = new Error(msg);
        error.name = 'BizError';
        error.info = { msg, code, data };
        throw error; // 抛出自制的错误
      }
    },
    // 错误接收及处理
    errorHandler: (error: any, opts: any) => {
      if (opts?.skipErrorHandler) throw error;
      // 我们的 errorThrower 抛出的错误。
      if (error.name === 'BizError') {
        const errorInfo: ResponseStructure = error.info;
        if (errorInfo) {
          handleBizError(errorInfo);
        }
      } else if (error.response) {
        // 请求成功发出且服务器也响应了状态码，但状态代码超出了 2xx 的范围
        //认证错误，跳转登录页面
        if (error.response.status === 401) {
          history.push(API_CONSTANTS.LOGIN_PATH);
        } else {
          if (getValueFromLocalStorage(ENABLE_MODEL_TIP) == 'true') {
            ErrorNotification(error.message, error.code);
          }
        }
      } else if (error.request) {
        // 请求已经成功发起，但没有收到响应
        ErrorNotification(error.toString(), l('app.response.noresponse'));
      } else {
        // 发送请求时出了点问题
        ErrorNotification(error.toString(), l('app.request.failed'));
      }
    }
  },

  // 请求拦截器
  requestInterceptors: [
    (config: RequestOptions) => {
      // 拦截请求配置，进行个性化处理。
      const url = config?.url;
      return { ...config, url };
    }
  ],

  // 响应拦截器
  responseInterceptors: [
    (response) => {
      // 拦截响应数据，进行个性化处理
      // 不再需要异步处理读取返回体内容，可直接在data中读出，部分字段可在 config 中找到
      const { data = {} as any, config } = response;
      return response;
    }
  ]
};

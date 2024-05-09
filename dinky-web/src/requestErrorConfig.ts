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

import { redirectToLogin } from '@/pages/Other/Login/function';
import { ENABLE_MODEL_TIP } from '@/services/constants';
import { getValueFromLocalStorage } from '@/utils/function';
import { l } from '@/utils/intl';
import { ErrorNotification, WarningNotification } from '@/utils/messages';
import type { RequestOptions } from '@@/plugin-request/request';
import type { RequestConfig } from '@umijs/max';

// Error handling scheme: Error type
enum ErrorCode {
  SUCCESS = 0,
  ERROR = 1,
  EXCEPTION = 5,
  PARAMS_ERROR = 6,
  AUTHORIZE_ERROR = 7,
  SERVER_ERROR = 504,
  UNAUTHORIZED = 401
}

// Response data format agreed upon with the backend
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
  // Error handling: umi@3 Error handling plan for.
  errorConfig: {
    // Error thrown
    errorThrower: (res: ResponseStructure) => {
      const { success, data, msg, code } = res as ResponseStructure;
      if (!success) {
        const error: any = new Error(msg);
        error.name = 'BizError';
        error.info = { msg, code, data };
        throw error; // Throwing self-made errors
      }
    },
    // Error reception and handling
    errorHandler: (error: any, opts: any) => {
      if (opts?.skipErrorHandler) throw error;

      function processNotification(error: any, isEnableTips: string = 'false') {
        if (getValueFromLocalStorage(ENABLE_MODEL_TIP) == isEnableTips) {
          ErrorNotification(error.message, error.code);
        }
      }

      //The error thrown by our errorThrower.
      if (error.name === 'BizError') {
        const errorInfo: ResponseStructure = error.info;
        if (errorInfo) {
          handleBizError(errorInfo);
        }
      } else if (error.response) {
        //The request was successfully sent and the server also responded with a status code, but the status code exceeded the range of 2xx
        //Authentication error, redirect to login page
        if (error.response.status === ErrorCode.UNAUTHORIZED) {
          redirectToLogin(error.message);
        } else if (error.response.status === ErrorCode.SERVER_ERROR) {
          processNotification(error);
          //Note: when the server is not available or the network is disconnected, redirect to login page
          redirectToLogin(error.message);
        } else {
          processNotification(error, 'true');
        }
      } else if (error.request) {
        //The request has been successfully initiated, but no response has been received
        ErrorNotification(error.toString(), l('app.response.noresponse'));
      } else {
        // There was a problem sending the request
        ErrorNotification(error.toString(), l('app.request.failed'));
      }
    }
  },

  // request interceptor
  requestInterceptors: [
    (config: RequestOptions) => {
      // Intercept request configuration for personalized processing.
      const url = config?.url;
      return { ...config, url };
    }
  ],

  // Response interceptor
  responseInterceptors: [
    (response) => {
      //Intercept response data for personalized processing
      //No longer requires asynchronous processing to read the content of the return body, it can be directly read from data, and some fields can be found in config
      const { data = {} as any, config } = response;
      return response;
    }
  ]
};

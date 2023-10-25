/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

import useUmiRequest from '@ahooksjs/use-request';
import { request } from '@umijs/max';
import { Spin } from 'antd';
import { createContext, memo, useContext, useState } from 'react';

export interface RequestOptions {
  url: string;
  method: 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE';
  data?: T;
  params?: any;
  headers?: any;
}

export interface RequestContextType {
  useGet: (options: RequestOptions) => void;
}

const METHODS = ['Get', 'Post', 'Put', 'Patch', 'Delete'];

export const RequestContext = createContext<RequestContextType>(null!);

export const RequestProvider = memo(({ children }) => {
  const [appLoading, setAppLoading] = useState(false);
  const requestWithMethod = async (options: RequestOptions) => {
    setAppLoading(true);
    const { url, ...opts } = options;
    const { data } = await request(url, opts);
    setAppLoading(false);
    return data;
  };

  //TODO 封装请求方法
  const hooks = {};

  METHODS.forEach((method) => {
    hooks[`use${method}`] = (url, opts) =>
      useUmiRequest(
        () => {
          setAppLoading(true);
          return request(url, { ...opts, method });
        },
        {
          formatResult: (result) => result?.datas,
          requestMethod: (requestOptions: any) => {
            if (typeof requestOptions === 'string') {
              return request(requestOptions);
            }
            if (typeof requestOptions === 'object') {
              const { url, ...rest } = requestOptions;
              return request(url, rest);
            }
            throw new Error('request options error');
          },
          onSuccess: (data) => {
            setAppLoading(false);
          },
          onError: (err) => {
            setAppLoading(false);
          },
          onFinally: () => {
            setAppLoading(false);
          }
        }
      );
  });
  return (
    <RequestContext.Provider value={{ ...hooks, appLoading }}>
      <Spin tip='网络请求中...' spinning={appLoading}>
        {children}
      </Spin>
    </RequestContext.Provider>
  );
});

export const useService = () => useContext(RequestContext);

/****
 *
 *
  import { useService } from '../../hooks/useService';
  const {useGet,usePost,usePut,usePatch,useDelete} = useService();
  const {data} = useGet('/api/current',{params:{xxxx}})
  const {data} = usePost('/api/current',{data:{xxxx}})
  const {data} = usePut('/api/current',{data:{xxxx}})
  const {data} = usePut('/api/current',{data:{xxxx}})
  const {data} = usePatch('/api/current',{data:{xxxx}})
  const {data} = useDelete('/api/current',{data:{xxxx}})
  console.log(data)
 *
 *
 */

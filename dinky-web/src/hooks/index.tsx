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

import {useCallback} from 'react';
import {RequestConfig, useRequest} from '@umijs/max';
import {ErrorNotification, SuccessMessage} from '@/utils/messages';


const noop = () => {
};

/**
 *  return CRUDResult type
 */
interface CRUDResult<T> {
  data: T | undefined; // 表示请求返回的数据。
  error: Error | undefined; // 表示请求返回的错误。
  loading: boolean; // 表示请求是否正在进行中。
  params: any; // 表示请求参数。
  run: (config?: RequestConfig) => Promise<any>; // 可以手动触发请求。
  cancel: () => void; // 用于取消当前请求。
  refresh: () => Promise<any>; // 用于重新发起请求
  mutate: (newData: T | undefined) => void;  // 用于手动更新数据
  addData: (payload: any) => Promise<any>;  // 用于创建新数据/更新数据。
  getDataList: () => Promise<any>; // 用于读取数据。
  updateData: (id: number, payload: any) => Promise<any>; // 用于更新数据。
  removeDataById: (id: number) => Promise<any>; // 用于删除数据。
}

/**
 *  useCustomCRUD hook
 * @param url 请求的url
 * @param options 请求的配置
 */
const useCustomCRUD = (url: string, options = {}): CRUDResult<any> => {

  /**
   * @name BaseOptions
   * @note
   *     refreshDeps?: DependencyList; // 当前请求的依赖项
   *     manual?: boolean; // 手动刷新
   *     onSuccess?: (data: R, params: P) => void; // 请求成功时的回调
   *     onError?: (e: Error, params: P) => void; // 请求失败时的回调
   *     defaultLoading?: boolean; //默认加载
   *     loadingDelay?: number; // 加载延迟
   *     defaultParams?: P; // 默认参数
   *     pollingInterval?: number; // 轮询间隔
   *     pollingWhenHidden?: boolean; // 隐藏时是否轮询
   *     fetchKey?: (...args: P) => string; // 请求key
   *     paginated?: false; // 分页
   *     loadMore?: false; // 加载更多
   *     refreshOnWindowFocus?: boolean; // 窗口获取焦点时是否刷新
   *     focusTimespan?: number; // 焦点时间跨度
   *     cacheKey?: CachedKeyType; // 缓存键
   *     cacheTime?: number; // 缓存时间
   *     staleTime?: number; // 缓存时间
   *     debounceInterval?: number; // 防抖间隔时间
   *     throttleInterval?: number; //
   *     initialData?: R; // 初始数据
   *     requestMethod?: (service: any) => Promise<any>; // 请求方法
   *     getDataListy?: boolean; // 初始化状态
   *     throwOnError?: boolean;  // 抛出错误
   */

  const {data, error, loading, params, run, cancel, refresh, mutate} = useRequest(url, {
    ...options,
    manual: true,
    formatResult: (res: { data: any; }) => res.data,
    onSuccess: async (res) => {
      SuccessMessage(res.msg);
      return true;
    },
    onError: (e) => {
      ErrorNotification(e.message);
      return false;
    },
    throwOnError: true, // 错误时抛出 || when error is thrown and throwOnError is true
    refreshOnWindowFocus: true, // 窗口聚焦时 是否重新刷新 || when window focused and refreshOnWindowFocus is true
    ready: true, // 初始化状态 || when ready is true and initialData is undefined
    debounceInterval: 500, // 防抖间隔 || debounce interval
    throttleInterval: 500, // 节流间隔 || throttle interval
    initialData: {}, // 初始数据 || init data
    defaultLoading: true,// 默认加载 || default loading
    loadMore: true, // 加载更多  || load more
  });

  const addData = useCallback(
    async (payload: any) => {
      await run({
        ...options,
        method: 'POST',
        data: payload,
      });
    },
    [run, options]
  );

  const getDataList = useCallback(
    async () => {
      await run({
        ...options,
        method: 'GET',
      });
    },
    [run, options]
  );

  const updateData = useCallback(
    async (id: number, payload: any) => {
      await run({
        ...options,
        method: 'PUT',
        url: `${url}/${id}`,
        data: payload,
      });
    },
    [run, options, url]
  );

  const removeDataById = useCallback(
    async (id: number) => {
      await run({
        ...options,
        method: 'DELETE',
        url: `${url}/${id}`,
      });
    },
    [run, options, url]
  );

  return {
    data,
    error,
    loading,
    params,
    run,
    cancel: cancel || noop,
    refresh: refresh || (() => Promise.resolve()),
    mutate,
    addData,
    getDataList,
    updateData,
    removeDataById,
  };
};

export default useCustomCRUD;


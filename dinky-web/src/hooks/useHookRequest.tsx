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

import { Timeout } from '@antv/l7-layers/es/tile/interface';
import lodash from 'lodash';
import { SetStateAction, useCallback, useEffect, useRef, useState } from 'react';

interface UseRequestOptionsProps<TData extends { data: any }, TParams extends any[]> {
  /*
   * 手动开启
   */
  manual?: boolean;
  /*
   * 请求参数
   */
  defaultParams: TParams;
  /*
   * 轮询
   */
  pollingInterval?: number | null;
  /*
   * 准备，用于依赖请求
   */
  ready?: boolean;
  /*
   * 防抖
   */
  debounceInterval?: number;
  /*
   * 节流
   */
  throttleInterval?: number;
  /*
   * 延迟loading为true的时间
   */
  loadingDelay?: number;
  /*
   * 依赖
   */
  refreshDeps?: any[];
  /*
   * 请求成功回调
   */
  onSuccess?: (res: TData) => void;
}

function useHookRequest<TData extends { data: any }, TParams extends any[]>(
  service: (...args: TParams) => Promise<TData>,
  options: UseRequestOptionsProps<TData, TParams>
) {
  const [data, setData] = useState<SetStateAction<TData>>();
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>();
  const status = useRef<boolean>(false);
  const pollingIntervalTimer = useRef<Timeout>();

  const {
    manual = false,
    defaultParams,
    pollingInterval = null,
    ready = true,
    debounceInterval = null,
    throttleInterval = null,
    loadingDelay = null,
    refreshDeps = null,
    onSuccess = null
  } = options;

  useEffect(() => {
    !manual && ready && run(...defaultParams);
  }, [manual, ready, ...(Array.isArray(refreshDeps) ? refreshDeps : [])]);

  //  请求
  const run = (params?: TParams) => {
    if (!params) {
      params = defaultParams;
    }
    if (debounceInterval) {
      lodash.debounce(doRun, debounceInterval)(...params);
    } else if (throttleInterval) {
      lodash.throttle(doRun, throttleInterval)(...params);
    } else {
      doRun(...params);
    }
  };

  // useRequest业务逻辑
  const doRun = async (...params: TParams) => {
    let finish = false;
    try {
      //延迟显示loading，防止刷新时闪屏
      if (loadingDelay) {
        setTimeout(() => {
          !finish && setLoading(true);
        }, loadingDelay);
      } else {
        setLoading(true);
      }
      !status.current && (status.current = true);
      //定时刷新
      if (pollingInterval && status.current) {
        pollingIntervalTimer.current = setTimeout(() => {
          status.current && run(...defaultParams);
        }, pollingInterval);
      }
      const res: TData = await service(...params);
      setData(res.data);
      onSuccess && onSuccess(res.data);
    } catch (err) {
      err && setError(JSON.stringify(err));
    } finally {
      finish = true;
      setLoading(false);
    }
  };

  const cancel = () => {
    if (pollingIntervalTimer.current) {
      clearTimeout(pollingIntervalTimer.current);
      pollingIntervalTimer.current = null;
      status.current && (status.current = false);
    }
  };

  // 缓存
  const cachedData = useCallback(() => data, [data]);

  return { data, loading, error, run, cancel, cachedData };
}

export default useHookRequest;

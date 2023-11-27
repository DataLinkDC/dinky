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

import React from 'react';

export function useLocalStorage(key: string, initialValue: any) {
  const readValue = React.useCallback(() => {
    try {
      const item = window.localStorage.getItem(key);
      return item ?? initialValue;
    } catch (error) {
      console.warn(error);
      return initialValue;
    }
  }, [key, initialValue]);

  const [localState, setLocalState] = React.useState(readValue);

  const handleSetState = React.useCallback(
    (value: any) => {
      try {
        const nextState = typeof value === 'function' ? value(localState) : value;
        window.localStorage.setItem(key, nextState);
        setLocalState(nextState);
        window.dispatchEvent(new Event('local-storage'));
      } catch (e) {
        console.warn(e);
      }
    },
    [key, localState]
  );

  return [localState, handleSetState];
}

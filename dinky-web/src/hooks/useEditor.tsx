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
  createContext,
  Dispatch,
  FC,
  memo,
  SetStateAction,
  useContext,
  useEffect,
  useState
} from 'react';
import { useModel } from 'umi';
export interface FullScreenContextProps {
  fullscreen: boolean;
  setFullscreen: Dispatch<SetStateAction<boolean>>;
}

export const FullScreenContext = createContext<FullScreenContextProps>(
  {} as FullScreenContextProps
);

export const FullScreenProvider: FC = memo(({ children }: any) => {
  const [fullscreen, setFullscreen] = useState<boolean>(false);

  const { initialState, setInitialState } = useModel('@@initialState');

  useEffect(() => {
    setInitialState((s) => ({
      ...s,
      fullscreen
    }));
  }, [fullscreen]);
  return (
    <FullScreenContext.Provider value={{ fullscreen, setFullscreen }}>
      {children}
    </FullScreenContext.Provider>
  );
});

export const useEditor = () => useContext(FullScreenContext);

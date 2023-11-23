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

import { THEME } from '@/types/Public/data';
import { useEffect, useState } from 'react';

export type ThemeValue = {
  borderColor: string;
  footerColor: string;
};

const getThemeValue = (isDark: boolean): ThemeValue => {
  if (isDark) {
    return { borderColor: '#343434', footerColor: 'rgba(255, 255, 255, 0.18)' };
  } else {
    return { borderColor: 'rgba(5, 5, 5, 0.06)', footerColor: '#f4f4f4' };
  }
};

export default function useThemeValue() {
  const [theme, setTheme] = useState(localStorage.getItem(THEME.NAV_THEME));

  useEffect(() => {
    setTheme(localStorage.getItem(THEME.NAV_THEME));
  }, [localStorage.getItem(THEME.NAV_THEME)]);

  return getThemeValue(theme === THEME.dark);
}

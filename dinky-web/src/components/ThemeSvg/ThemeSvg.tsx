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

export const ThemeCloud = () => {
  return (
    <>
      <svg
        className='absolute left-[8px] top-[7px]'
        width='16'
        height='14'
        viewBox='0 0 89 77'
        fill='none'
        xmlns='http://www.w3.org/2000/svg'
      >
        <path
          d='M25 10L31.7523 28.2477L50 35L31.7523 41.7523L25 60L18.2477 41.7523L0 35L18.2477 28.2477L25 10Z'
          fill='#C6D0D1'
        />
        <path
          d='M71.5 42L76.2266 54.7734L89 59.5L76.2266 64.2266L71.5 77L66.7734 64.2266L54 59.5L66.7734 54.7734L71.5 42Z'
          fill='#C6D0D1'
        />
        <path
          d='M61 0L63.7009 7.29909L71 10L63.7009 12.7009L61 20L58.2991 12.7009L51 10L58.2991 7.29909L61 0Z'
          fill='#C6D0D1'
        />
      </svg>
    </>
  );
};

/**
 * ThemeStar is a svg icon  , it is used in the theme switcher of dark mode
 * @constructor
 */
export const ThemeStar = () => {
  return (
    <>
      <svg
        className='absolute right-[10px] top-[10px]'
        width='15'
        height='8'
        viewBox='0 0 104 54'
        fill='none'
        xmlns='http://www.w3.org/2000/svg'
      >
        <path
          d='M18.0258 11.2704C18.0258 5.34458 22.8296 0.540771 28.7554 0.540771H93.1331C99.0589 0.540771 103.863 5.34458 103.863 11.2704C103.863 17.1962 99.0589 22 93.1331 22H66.2146C63.3038 22 60.9442 24.3596 60.9442 27.2704V27.2704C60.9442 30.1811 63.3038 32.5408 66.2146 32.5408H75.1073C81.0331 32.5408 85.8369 37.3446 85.8369 43.2704C85.8369 49.1962 81.0331 54 75.1073 54H10.7296C4.80381 54 0 49.1962 0 43.2704C0 37.3446 4.80381 32.5408 10.7296 32.5408H44.7296C47.6404 32.5408 50 30.1811 50 27.2704V27.2704C50 24.3596 47.6404 22 44.7296 22H28.7554C22.8296 22 18.0258 17.1962 18.0258 11.2704Z'
          fill='white'
        />
      </svg>
    </>
  );
};

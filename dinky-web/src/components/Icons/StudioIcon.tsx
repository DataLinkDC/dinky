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

import Icon from '@ant-design/icons';

const defaultSvgSize = '100%';

const style = { overflow: 'hidden' };

export const SaveIcon = (props: any) => {
  const { size: sizeProps, style: styleProps } = props;
  const size = sizeProps || defaultSvgSize;
  return (
    <>
      <Icon
        style={{ ...style, ...styleProps }}
        component={() => (
          <svg
            className='icon'
            viewBox='0 0 1024 1024'
            version='1.1'
            xmlns='http://www.w3.org/2000/svg'
            width={size}
            height={size}
          >
            <path
              d='M523.73504 319.29344h-204.8c-16.896 0-30.72-13.824-30.72-30.72s13.824-30.72 30.72-30.72h204.8c16.896 0 30.72 13.824 30.72 30.72s-13.824 30.72-30.72 30.72zM605.65504 452.41344h-286.72c-16.896 0-30.72-13.824-30.72-30.72s13.824-30.72 30.72-30.72h286.72c16.896 0 30.72 13.824 30.72 30.72s-13.824 30.72-30.72 30.72z'
              fill='#FFFFFF'
            ></path>
            <path
              d='M658.176 146.57536H267.8784c-40.07936 0-72.58112 32.13312-72.58112 71.7824v587.29472c0 39.63904 32.49152 71.7824 72.58112 71.7824h488.2432c40.07936 0 72.58112-32.13312 72.58112-71.7824V303.18592L658.176 146.57536z'
              fill='#3889FF'
            ></path>
            <path
              d='M317.10208 195.29728h341.06368v146.176H317.10208zM268.38016 633.8048h487.23968v243.61984H268.38016z'
              fill='#FFFFFF'
            ></path>
            <path
              d='M598.71232 317.10208a25.6 25.6 0 0 1-25.6-25.6v-46.24384a25.6 25.6 0 1 1 51.2 0v46.24384a25.6 25.6 0 0 1-25.6 25.6zM640 736.2048h-256a25.6 25.6 0 1 1 0-51.2h256a25.6 25.6 0 1 1 0 51.2zM643.0208 829.17376h-256a25.6 25.6 0 1 1 0-51.2h256a25.6 25.6 0 0 1 0 51.2z'
              fill='#3889FF'
            ></path>
          </svg>
        )}
      />
    </>
  );
};

export const MaxIcon = (props: any) => {
  const { size: sizeProps, style: styleProps } = props;
  const size = sizeProps || defaultSvgSize;
  return (
    <>
      <Icon
        {...props}
        style={{ ...style, ...styleProps }}
        component={() => (
          <svg
            className='icon'
            viewBox='0 0 1024 1024'
            version='1.1'
            xmlns='http://www.w3.org/2000/svg'
            width={size}
            height={size}
          >
            <path
              d='M0 0m341.333333 0l341.333334 0q341.333333 0 341.333333 341.333333l0 341.333334q0 341.333333-341.333333 341.333333l-341.333334 0q-341.333333 0-341.333333-341.333333l0-341.333334q0-341.333333 341.333333-341.333333Z'
              fill='#3D7FFF'
            ></path>
            <path
              d='M483.242667 761.813333H293.162667l155.861333-154.026666c7.594667-7.509333 7.594667-22.528 0-30.037334-7.594667-11.264-22.784-11.264-34.218667 0L258.986667 731.733333v-202.88c0-11.264-11.392-22.528-22.784-22.528-11.434667 0-22.826667 11.264-22.826667 22.528v214.186667C213.333333 773.034667 236.16 810.666667 270.336 810.666667h212.906667c11.392 0 22.826667-11.264 22.826666-22.528 0-15.061333-11.434667-26.325333-22.826666-26.325334zM768.426667 213.333333h-224.298667c-11.434667 0-22.826667 11.264-22.826667 22.528s11.392 22.570667 22.826667 22.570667h190.08l-159.701333 154.026667c-7.594667 7.509333-7.594667 22.528 0 33.792 11.434667 7.509333 26.624 7.509333 34.218666 0l155.861334-154.026667v187.861333c0 11.264 11.434667 22.528 22.826666 22.528s22.826667-11.264 22.826667-22.528V273.450667c3.797333-37.546667-15.232-60.117333-41.813333-60.117334z'
              fill='#FFFFFF'
            ></path>
          </svg>
        )}
      />
    </>
  );
};

export const MinIcon = (props: any) => {
  const { size: sizeProps, style: styleProps } = props;
  const size = sizeProps || defaultSvgSize;
  return (
    <>
      <Icon
        {...props}
        style={{ ...style, ...styleProps }}
        component={() => (
          <svg
            className='icon'
            viewBox='0 0 1024 1024'
            version='1.1'
            xmlns='http://www.w3.org/2000/svg'
            width={size}
            height={size}
          >
            <path
              d='M0 0m204.8 0l614.4 0q204.8 0 204.8 204.8l0 614.4q0 204.8-204.8 204.8l-614.4 0q-204.8 0-204.8-204.8l0-614.4q0-204.8 204.8-204.8Z'
              fill='#008AFF'
            ></path>
            <path
              d='M217.6 448m40.96 0l506.88 0q40.96 0 40.96 40.96l0 20.48q0 40.96-40.96 40.96l-506.88 0q-40.96 0-40.96-40.96l0-20.48q0-40.96 40.96-40.96Z'
              fill='#FFFFFF'
            ></path>
          </svg>
        )}
      />
    </>
  );
};

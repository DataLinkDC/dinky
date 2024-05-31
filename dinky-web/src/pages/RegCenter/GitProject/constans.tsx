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

import { l } from '@/utils/intl';

/**
 * git project code language type enum
 */
export const GIT_PROJECT_CODE_TYPE_ENUM = {
  1: {
    text: 'Java'
  },
  2: {
    text: 'Python'
  }
};
/**
 * git project code language type filter
 */
export const GIT_PROJECT_CODE_TYPE = [
  {
    value: 1,
    text: 'Java'
  },
  {
    value: 2,
    text: 'Python'
  }
];

/**
 * git project clone type enum
 */
export const GIT_PROJECT_TYPE_ENUM = {
  1: {
    text: 'HTTP/HTTPS'
  },
  2: {
    text: 'SSH'
  }
};
/**
 * git project clone type filter
 */
export const GIT_PROJECT_TYPE = [
  {
    text: 'HTTP/HTTPS',
    value: 1
  },
  {
    text: 'SSH',
    value: 2
  }
];

/**
 * git project running status enum
 * 0-notStart 1-process 2-failed 3-success
 */
export const GIT_PROJECT_STATUS_ENUM = {
  0: {
    title: l('rc.gp.notBuild'),
    text: l('rc.gp.notBuild'),
    status: 'default'
  },
  1: {
    title: l('rc.gp.building'),
    text: l('rc.gp.building'),
    status: 'processing'
  },
  2: {
    title: l('rc.gp.buildFail'),
    text: l('rc.gp.buildFail'),
    status: 'error'
  },
  3: {
    title: l('rc.gp.buildSuccess'),
    text: l('rc.gp.buildSuccess'),
    status: 'success'
  }
};
/**
 * git project build status filter
 */
export const GIT_PROJECT_STATUS = [
  {
    value: 0,
    status: 'default',
    text: l('rc.gp.notBuild')
  },
  {
    value: 1,
    status: 'processing',
    text: l('rc.gp.building')
  },
  {
    value: 2,
    status: 'error',
    text: l('rc.gp.buildFail')
  },
  {
    value: 3,
    status: 'success',
    text: l('rc.gp.buildSuccess')
  }
];

export const CLONE_TYPES = [
  { label: 'http/https', value: 1 },
  { label: 'ssh', value: 2 }
];

/**
 * render branches tag color
 * @param item
 */
export const renderBranchesTagColor = (item: string) => {
  return item.includes('dev')
    ? 'processing'
    : item.includes('test')
      ? 'warning'
      : item.includes('release')
        ? 'success'
        : item.includes('master')
          ? 'success'
          : item.includes('main')
            ? 'success'
            : 'default';
};

/**
 * 区别于 java 和 Python 类型 | different from java and python
 * 构建 java 工程时:   步骤值映射如下: 0: 环境检查 1: 克隆项目 2: 编译构建 3: 获取产物 4: 分析 UDF 5: 完成; (when build java project, the step value is as follows: 0: environment check 1: clone project 2: compile and build 3: get artifact 4: analyze UDF 5: finish)
 * 构建 python 工程时: 步骤值映射如下: 0: 环境检查 1: 克隆项目 2: 获取产物 3: 分析 UDF 4: 完成;(when build python project, the step value is as follows: 0: environment check 1: clone project 2: get artifact 3: analyze UDF 4: finish)
 */
const GIT_PROJECT_BUILD_STEP_BASE = {
  0: {
    title: l('rc.gp.build.step.0'),
    text: l('rc.gp.build.step.0'),
    status: 'default'
  },
  1: {
    title: l('rc.gp.build.step.1'),
    text: l('rc.gp.build.step.1'),
    status: 'default'
  }
};
/**
 * git project build step enum
 */
export const GIT_PROJECT_BUILD_STEP_JAVA_ENUM = {
  ...GIT_PROJECT_BUILD_STEP_BASE,
  2: {
    title: l('rc.gp.build.step.2'),
    text: l('rc.gp.build.step.2'),
    status: 'processing'
  },
  3: {
    title: l('rc.gp.build.step.3'),
    text: l('rc.gp.build.step.3'),
    status: 'error'
  },
  4: {
    title: l('rc.gp.build.step.4'),
    text: l('rc.gp.build.step.4'),
    status: 'success'
  },
  5: {
    title: l('rc.gp.build.step.5'),
    text: l('rc.gp.build.step.5'),
    status: 'success'
  },
  6: {
    title: l('rc.gp.build.step.6'),
    text: l('rc.gp.build.step.6'),
    status: 'success'
  }
};

export const GIT_PROJECT_BUILD_STEP_PYTHON_ENUM = {
  ...GIT_PROJECT_BUILD_STEP_BASE,
  2: {
    title: l('rc.gp.build.step.2'),
    text: l('rc.gp.build.step.2'),
    status: 'processing'
  },
  3: {
    title: l('rc.gp.build.step.4'),
    text: l('rc.gp.build.step.4'),
    status: 'success'
  },
  4: {
    title: l('rc.gp.build.step.5'),
    text: l('rc.gp.build.step.5'),
    status: 'success'
  },
  5: {
    title: l('rc.gp.build.step.6'),
    text: l('rc.gp.build.step.6'),
    status: 'success'
  }
};

/**
 * git project build step filter
 */
export const GIT_PROJECT_BUILD_STEP = [
  {
    value: 0,
    status: 'default',
    text: l('rc.gp.build.step.0')
  },
  {
    value: 1,
    status: 'default',
    text: l('rc.gp.build.step.1')
  },
  {
    value: 2,
    status: 'processing',
    text: l('rc.gp.build.step.2')
  },
  {
    value: 3,
    status: 'error',
    text: l('rc.gp.build.step.3')
  },
  {
    value: 4,
    status: 'success',
    text: l('rc.gp.build.step.4')
  },
  {
    value: 5,
    status: 'success',
    text: l('rc.gp.build.step.5')
  },
  {
    value: 6,
    status: 'success',
    text: l('rc.gp.build.step.6')
  }
];

/**
 * git project build step enum
 */
export const processColor = {
  '0%': '#8ac1ea',
  '20%': '#99e5d0',
  '40%': '#9ae77b',
  '60%': '#59b732',
  '80%': '#47d50a',
  '100%': '#01ad31'
};

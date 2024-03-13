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
import { ModalFormProps } from '@ant-design/pro-form/es/layouts/ModalForm';

/**
 * user tenant id
 */
export const TENANT_ID = 'tenantId';

/**
 * the platform language
 */
export const STORY_LANGUAGE = 'language';
export const LANGUAGE_KEY = 'umi_locale';
export const LANGUAGE_ZH = 'zh-CN';
export const LANGUAGE_EN = 'en-US';

export const DATE_FORMAT = 'YYYY-MM-DD';
export const DATETIME_FORMAT = 'YYYY-MM-DD HH:mm:ss';

export const ENABLE_MODEL_TIP = 'enableModelTip';

/**
 * REQUEST METHOD CONSTANTS
 */
export enum METHOD_CONSTANTS {
  GET = 'GET',
  POST = 'POST',
  PUT = 'PUT',
  DELETE = 'DELETE'
}

/**
 * ALL TABLE COLUMN of status
 * @constructor
 */
export const STATUS_MAPPING = () => {
  return [
    {
      text: l('status.enabled'),
      value: 1
    },
    {
      text: l('status.disabled'),
      value: 0
    }
  ];
};

/**
 * ALL TABLE COLUMN of status enum
 * @constructor
 */
export const STATUS_ENUM = () => {
  return {
    true: { text: l('status.enabled'), status: 'Success' },
    false: { text: l('status.disabled'), status: 'Error' }
  };
};

export const RESPONSE_CODE = {
  SUCCESS: 0,
  ERROR: 1
};

/**
 * the form layout of public
 */
export const FORM_LAYOUT_PUBLIC = {
  labelCol: { span: 5 },
  wrapperCol: { span: 15 }
};

/**
 * the modal form layout of public
 */
export const MODAL_FORM_STYLE: any = {
  width: '55%',
  style: {
    maxHeight: '70vh',
    overflowY: 'auto'
  }
};

export const PRO_LIST_CARD_META = {
  title: {},
  subTitle: {},
  type: {},
  avatar: {},
  content: {},
  description: {},
  actions: {
    cardActionProps: 'actions'
  }
};

export const PRO_LIST_CARD_OPTIONS = {
  search: false,
  metas: PRO_LIST_CARD_META,
  size: 'small',
  pagination: {
    defaultPageSize: 15,
    hideOnSinglePage: true
  },
  grid: { gutter: 24, column: 5 }
};

/**
 * the protable layout of public
 */
export const PROTABLE_OPTIONS_PUBLIC: any = {
  pagination: {
    defaultPageSize: 10,
    hideOnSinglePage: true,
    showQuickJumper: false,
    showSizeChanger: true,
    position: ['bottomCenter']
  },
  ghost: false,
  rowKey: 'id',
  size: 'small',
  scroll: {
    y: 'auto'
  },
  search: {
    labelWidth: 100, // must be number
    span: 4
  }
};

/**
 * the modal layout of public
 */
export const NORMAL_MODAL_OPTIONS = {
  width: '50%',
  styles: {
    body: { padding: '20px 10px 10px' }
  },
  destroyOnClose: true,
  maskClosable: false
};

/**
 * the modal layout of public
 */
export const MODAL_FORM_OPTIONS: ModalFormProps = {
  width: '50%'
};

/**
 * the modal layout of public
 */
export const NORMAL_TABLE_OPTIONS = {
  pagination: {
    defaultPageSize: 6,
    hideOnSinglePage: true
  },
  rowKey: 'id',
  style: {
    scrollY: 'auto',
    scrollX: 'auto'
  }
};

export const SWITCH_OPTIONS = () => {
  return {
    checkedChildren: l('status.enabled'),
    unCheckedChildren: l('status.disabled')
  };
};

export const DIALECT = {
  JAVA: 'java',
  FLINK_SQL: 'flinksql',
  LOG: 'log',
  XML: 'xml',
  MD: 'md',
  MDX: 'mdx',
  MARKDOWN: 'markdown',
  SCALA: 'scala',
  PYTHON: 'py',
  PYTHON_LONG: 'python',
  YML: 'yml',
  YAML: 'yaml',
  CONF: 'conf',
  SH: 'sh',
  BASH: 'bash',
  CMD: 'cmd',
  SHELL: 'shell',
  JSON: 'json',
  SQL: 'sql',
  JAVASCRIPT: 'javascript',
  FLINKJAR: 'flinkjar',
  JAR: 'jar',
  ZIP: 'zip',
  TAR: 'tar',
  TAR_GZ: 'gz',
  FLINKSQLENV: 'flinksqlenv',
  MYSQL: 'mysql',
  ORACLE: 'oracle',
  SQLSERVER: 'sqlserver',
  POSTGRESQL: 'postgresql',
  CLICKHOUSE: 'clickhouse',
  DORIS: 'doris',
  HIVE: 'hive',
  PHOENIX: 'phoenix',
  STARROCKS: 'starrocks',
  PRESTO: 'presto'
};

export const RUN_MODE = {
  LOCAL: 'local',
  STANDALONE: 'standalone',
  YARN_SESSION: 'yarn-session',
  YARN_PER_JOB: 'yarn-per-job',
  YARN_APPLICATION: 'yarn-application',
  KUBERNETES_SESSION: 'kubernetes-session',
  KUBERNETES_APPLICATION: 'kubernetes-application',
  KUBERNETES_APPLICATION_OPERATOR: 'kubernetes-application-operator'
};

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

import { ProLayoutProps } from '@ant-design/pro-components';

/**
 * @name
 */
const Settings: ProLayoutProps & {
  pwa?: boolean;
  logo?: string;
} = {
  navTheme: 'light',
  // 拂晓蓝
  colorPrimary: '#1890ff',
  layout: 'mix',
  contentWidth: 'Fluid',
  fixedHeader: false,
  fixSiderbar: true,
  colorWeak: false,
  // 这里不能删除,需要先设置,否则会导致页面白屏, 然后在 layout 进行重新赋值
  title: 'Dinky Real-time Platform ',
  pwa: true,
  logo: './dinky.svg',
  iconfontUrl: '',
  splitMenus: true,
  menu: {
    locale: true
  },
  token: {
    // bgLayout: '', //layout 的背景颜色

    // 参见ts声明，demo 见文档，通过token 修改样式
    // https://procomponents.ant.design/components/layout#%E9%80%9A%E8%BF%87-token-%E4%BF%AE%E6%94%B9%E6%A0%B7%E5%BC%8F
    header: {
      colorBgHeader: '#292f33',
      colorHeaderTitle: '#fff',
      colorTextMenu: '#dfdfdf',
      colorTextMenuSecondary: '#dfdfdf',
      colorTextMenuSelected: '#fff',
      colorBgMenuItemSelected: '#1e252a',
      colorTextMenuActive: 'rgba(255,255,255,0.85)',
      colorTextRightActionsItem: '#dfdfdf'
    },
    sider: {
      colorBgMenuItemSelected: '#3399FF'
    },
    pageContainer: {
      paddingBlockPageContainerContent: 5,
      paddingInlinePageContainerContent: 10
    }
  }
};

export default Settings;

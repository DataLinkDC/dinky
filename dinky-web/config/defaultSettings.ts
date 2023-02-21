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
  title: 'Dinky Real-time Platform ',
  pwa: true,
  logo: '/dinky.svg',
  iconfontUrl: '',
  splitMenus: true,
  menu: {
    locale: true,
  },
  token: {
    // bgLayout: '', layout 的背景颜色

    // 官方 https://procomponents.ant.design/components/layout/#layout-classicmode
    header: {
      colorBgHeader: '#292f33',
      colorHeaderTitle: '#fff',
      colorTextMenu: '#dfdfdf',
      colorTextMenuSecondary: '#dfdfdf',
      colorTextMenuSelected: '#fff',
      colorBgMenuItemSelected: '#22272b',
      colorTextMenuActive: 'rgba(255,255,255,0.85)',
      colorTextRightActionsItem: '#dfdfdf',
    },
    colorTextAppListIconHover: '#fff',
    colorTextAppListIcon: '#dfdfdf',
    sider: {
      colorMenuBackground: '#fff',
      colorMenuItemDivider: '#dfdfdf',
      colorBgMenuItemHover: '#f6f6f6',
      colorTextMenu: '#595959',
      colorTextMenuSelected: '#242424',
      colorTextMenuActive: '#242424',
      colorBgMenuItemCollapsedHover: '#242424',
    },
    // 自定义
    // header: {
    //   colorBgHeader: '#292f33', //header 背景色
    //   colorHeaderTitle: '#fff', //header 的 title 颜色
    //   // colorBgMenuItemHover: '', // 悬浮某项时的菜单背景色
    //   colorBgMenuItemSelected: '#1890ff', // 选中某项时的菜单背景色
    //   colorTextMenuSelected: '#fff', // 选中某项时的字体颜色
    //   // colorTextMenuActive: 'rgba(239,7,7,0.85)',  // 悬浮选中某项时的字体颜色
    //   colorTextMenu: '#dfdfdf',  // 字体颜色
    //   colorTextMenuSecondary: '#ffffff', // 二级菜单字体颜色
    //   // colorBgRightActionsItemHover:'',
    //   colorTextRightActionsItem: '#e10a0a',
    //   // heightLayoutHeader: 0
    // },
    // colorTextAppListIconHover: 'rgba(93,161,192,0.75)',
    // colorTextAppListIcon: 'rgba(24,255,182,0.75)',
    // // 侧边side的 token 配置
    // sider: {
    //   // colorBgCollapsedButton: '#4c7cd5', // 折叠按钮背景色
    //   // colorTextCollapsedButtonHover: '', // 折叠按钮文本悬浮背景色
    //   // colorTextCollapsedButton: '', // 折叠按钮文本颜色
    //   colorMenuBackground: '#dfdfdf', // 侧边栏菜单整体背景色
    //   colorBgMenuItemCollapsedHover: '#8f8a8a',
    //   // colorBgMenuItemCollapsedSelected: '#6dce11',// 侧边栏收起时 二级菜单弹出时选中的背景色
    //   // colorBgMenuItemCollapsedElevated: '#800b26',// 侧边栏收起时 二级菜单弹出时的背景色
    //   colorMenuItemDivider: '#d74814',
    //   colorBgMenuItemHover: '#cb5252',
    //   colorBgMenuItemSelected: 'rgba(93,161,192,0.75)',
    //   colorTextMenuSelected: '#242424', // 选中菜单的字体颜色
    //   colorTextMenuItemHover: '#1890ff', // 悬浮菜单时的字体颜色
    //   colorTextMenuActive: '#d809ef', // 含有二级菜单的 菜单悬浮时的字体颜色
    //   // colorTextMenu: '#ab0e0e', // 侧边栏菜单字体颜色
    //   // colorTextMenuSecondary: '#ab0e0e', // 二级菜单字体颜色
    //   paddingInlineLayoutMenu: 0, // padding 内联布局菜单 间距
    //   paddingBlockLayoutMenu: 0, // padding 块布局菜单 间距
    //   /**
    //    * menu 顶部 title 的字体颜色
    //    */
    //   // colorTextMenuTitle: '#67081e',
    //   // colorTextSubMenuSelected: '#346dc0',
    // },
    // 参见ts声明，demo 见文档，通过token 修改样式
    //https://procomponents.ant.design/components/layout#%E9%80%9A%E8%BF%87-token-%E4%BF%AE%E6%94%B9%E6%A0%B7%E5%BC%8F
  },
};

export default Settings;

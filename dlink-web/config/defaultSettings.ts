import { Settings as LayoutSettings } from '@ant-design/pro-layout';

const Settings: LayoutSettings & {
  pwa?: boolean;
  logo?: string;
} = {
  navTheme: 'light',
  // 拂晓蓝
  primaryColor: '#1890ff',
  layout: 'mix',
  contentWidth: 'Fluid',
  fixedHeader: false,
  fixSiderbar: true,
  colorWeak: false,
  title: 'Dinky 实时计算平台',
  pwa: false,
  logo: 'dinky.svg',
  iconfontUrl: '',
  menu: {
    locale: true
  },
  headerHeight: 48,
  splitMenus: true
};

export default Settings;

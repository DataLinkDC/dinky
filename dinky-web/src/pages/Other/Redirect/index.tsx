import { SysMenu } from '@/types/AuthCenter/data';
import { Navigate, useModel } from 'umi';

const Redirect = () => {
  const { initialState, _ } = useModel('@@initialState');

  console.log(initialState);

  const filterMenus = (menus: SysMenu[]) => {
    return menus?.filter((menu) => menu.type !== 'F');
  };
  let extraRoutes = filterMenus(initialState?.currentUser?.menuList);

  if (initialState?.currentUser?.user?.superAdminFlag) {
    return <Navigate to='/datastudio' />;
  }

  return <Navigate to={extraRoutes[0]?.path} />;
};

export default Redirect;

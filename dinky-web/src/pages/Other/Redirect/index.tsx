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

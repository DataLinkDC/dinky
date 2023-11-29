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

import { API } from '@/services/data.d';
import { SysMenu } from '@/types/AuthCenter/data.d';
import React, { createContext, ReactElement, useContext } from 'react';

/***
 * 按钮的path和name
 */
export type Block = {
  path: string;
  name: string;
};

export type AccessContextProps = {
  isAdmin: boolean;
  blocks: Block[];
};

export const AccessContext = createContext<AccessContextProps>(null!);

type AuthorizedProps = {
  path: string;
  denied?: ReactElement | null;
  children?: ReactElement | null;
};

/**
 *  判断用户是否有某一个权限 有返回true 没有返回false
 *    <p>
 *      主要针对按钮级别的禁用 ,在按钮禁用属性中需要使用(取反) !HasAuthority('xxx')来判断
 *      使用: <Button disabled={!HasAuthority('xxx')}>Test</Button>
 * @param path
 * @constructor
 */
export const HasAuthority = (path: string): boolean => {
  const { isAdmin, blocks = [] } = useContext(AccessContext);
  if (isAdmin) return true;
  return blocks.some((block) => block.path === path);
};

export function Authorized({ path, denied = null, children = null }: AuthorizedProps) {
  const { isAdmin, blocks = [] } = useContext(AccessContext);

  if (isAdmin) return children;

  if (!blocks.length) return denied;

  const authority = blocks.some((block) => block.path === path);

  return authority ? children : denied;
}

export const AccessContextProvider = ({
  children,
  currentUser
}: {
  children: React.ReactNode;
  currentUser: Partial<API.CurrentUser | undefined>;
}) => {
  const isAdmin = currentUser?.user?.superAdminFlag ?? false;
  let blocks: Block[] = [];
  const flatTree = (menus: SysMenu[]) => {
    menus.forEach(({ path, children, name, type }) => {
      if (type === 'F') {
        blocks.push({
          path,
          name
        });
      }

      if (children.length) {
        flatTree(children);
      }
    });
  };

  flatTree(currentUser?.menuList ?? []);

  return <AccessContext.Provider value={{ isAdmin, blocks }}>{children}</AccessContext.Provider>;
};

export const useAccess = () => useContext(AccessContext);

export function AuthorizedObject({ path, denied = null, children = null, access = {} }: any) {
  const { isAdmin, blocks = [] } = access;

  if (isAdmin) return children;

  if (!blocks.length) return denied;

  const authority = blocks.some((block: { path: string }) => block.path === path);

  return authority ? children : denied;
}

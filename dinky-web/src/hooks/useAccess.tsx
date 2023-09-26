import { API } from '@/services/data.d';
import { SysMenu } from '@/types/AuthCenter/data.d';
import React, { createContext, ReactElement, useContext } from 'react';
import path from "path";

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
export const HasAuthority = (path: string) : boolean => {
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

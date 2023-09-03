import { API } from '@/services/data';
import { SysMenu } from '@/types/RegCenter/data';
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

export function AuthorizedObject({ path, denied = null, children = null,access = {} }: any) {
  const { isAdmin, blocks = [] } = access;

  if (isAdmin) return children;

  if (!blocks.length) return denied;

  const authority = blocks.some((block) => block.path === path);

  return authority ? children : denied;
}

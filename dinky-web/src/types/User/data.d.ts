
export type UserTableListItem = {
  id?: number;
  enabled?: boolean;
  isDelete?: string;
  createTime?: Date;
  updateTime?: Date;
  username?: string;
  nickname?: string;
  password?: string;
  avatar?: string;
  worknum?: string;
  mobile?: string;
};

export type PasswordItem = {
  username: string;
  password?: string;
  newPassword?: string;
  newPasswordCheck?: string;
};


export type TenantTableListItem = {
  id?: number;
  tenantCode?: string;
  isDelete?: boolean;
  note?: string;
  createTime?: Date;
  updateTime?: Date;
};


export type RoleTableListItem = {
  id?: number;
  tenantId?: number;
  tenant: TenantTableListItem;
  roleCode?: string;
  roleName?: string;
  namespaceIds?: string;
  namespaces?: NameSpaceTableListItem[];
  isDelete?: boolean;
  note?: string;
  createTime?: Date;
  updateTime?: Date;
};


export type NameSpaceTableListItem = {
  id?: number;
  tenantId?: number;
  tenant: TenantTableListItem;
  namespaceCode?: string;
  enabled?: boolean;
  note?: string;
  createTime?: Date;
  updateTime?: Date;
};


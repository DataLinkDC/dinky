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

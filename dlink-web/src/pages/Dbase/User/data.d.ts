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
  sex?: boolean;
};

export type TableListPagination = {
  total: number;
  pageSize: number;
  current: number;
};

export type TableListData = {
  list: TableListItem[];
  pagination: Partial<TableListPagination>;
};

export type TableListParams = {
  status?: string;
  name?: string;
  desc?: string;
  key?: number;
  pageSize?: number;
  currentPage?: number;
  filter?: Record<string, any[]>;
  sorter?: Record<string, any>;
};

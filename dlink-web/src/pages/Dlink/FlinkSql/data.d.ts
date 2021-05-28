export type FlinkSqlTableListItem = {
  id: number,
  name: string,
  alias: string,
  type: string,
  sqlIndex: number,
  statement: string,
  note: string,
  enabled: boolean,
  createUser: number,
  createNickName: string,
  createTime: Date,
  updateUser: number,
  updateNickName: string,
  updateTime: Date,
  taskId: number,
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

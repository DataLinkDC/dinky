export type TaskHistoryTableListItem = {
  id: number,
  versionId: number,
  statement: string,
  createTime: Date,
};


export type TaskHistoryRollbackItem = {
  id: number,
  versionId: number,
};


export type BaseDataSourceField = {
  fields: [{
    label?: string,
    displayName?: string,
    aliasName?: string,
    kind?: any,
    insertText?: string,
    insertTextRules?: any,
    detail?: string,
  }]
}

export type BaseDataSourceHeader = {
  fields: [{
    label?: string,
    displayName?: string,
    aliasName?: string,
    kind?: any,
    insertText?: string,
    insertTextRules?: any,
    detail?: string,
  }]
}

export type CompletionItem = {
  label: string,
  kind?: any,
  insertText: string,
  insertTextRules?: any,
  detail?: string,
}
export type StudioParam = {
  useSession:boolean;
  session: string,
  useRemote?:boolean;
  clusterId?: number,
  useResult:boolean;
  maxRowNum?: number,
  statement: string,
  fragment?: boolean,
  jobName?:string,
  parallelism?: number,
  checkPoint?: number,
  savePointPath?: string,
}

export type StudioMetaStoreParam = {
  statement?: string,
  fragment?: boolean,
  dialect?: string,
  envId?: number,
  databaseId?: number,
}

export type CAParam = {
  statement: string,
  statementSet: boolean,
  type: number,
  dialect?: string,
  databaseId?: number,
}

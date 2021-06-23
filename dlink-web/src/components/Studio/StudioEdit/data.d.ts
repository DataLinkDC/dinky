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
  label?: string,
  kind?: any,
  insertText?: string,
  insertTextRules?: any,
  detail?: string,
}
export type StudioParam = {
  statement: string,
  checkPoint?: number,
  savePointPath?: string,
  parallelism?: number,
  fragment?: boolean,
  clusterId: number,
  session: string,
  maxRowNum?: number,
}
export type CAParam = {
  statement: string,
  type: number,
}

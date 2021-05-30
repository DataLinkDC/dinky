export type BaseDataSourceField ={
  fields:[{
    label?:string,
    displayName?:string,
    aliasName?:string,
    kind?:any,
    insertText?:string,
    insertTextRules?:any,
    detail?:string,
  }]
}

export type BaseDataSourceHeader ={
  fields:[{
    label?:string,
    displayName?:string,
    aliasName?:string,
    kind?:any,
    insertText?:string,
    insertTextRules?:any,
    detail?:string,
  }]
}

export type CompletionItem ={
  label?:string,
  kind?:any,
  insertText?:string,
  insertTextRules?:any,
  detail?:string,
}
 export type StudioParam = {
   statement:string,
 }

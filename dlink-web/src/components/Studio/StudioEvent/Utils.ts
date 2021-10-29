import {MetaData} from "@/components/Studio/StudioEvent/data";

export function parseSqlMetaData(sql:string) {
  if(!sql||sql===''){
    return false;
  }
  sql = sql.replaceAll('\r\n','').replaceAll(',',' ,').replaceAll(/\s+/g,' ');
  let statements = getStatements(sql);
  let metaDatas:MetaData[]=[];
  for(let i in statements){
    if(!statements[i]||statements[i]===''){
      continue;
    }
    if(!/create\s+table/i.test(statements[i])){
      continue;
    }
    metaDatas.push(regMetaData(statements[i]));
  }
  return {
    statement: sql,
    metaData: metaDatas,
  }
}

 function RegStr(text:string,reg:any){
   let temp = [],data = [];
   while ((temp = reg.exec(text)) !== null) {
     data.push(temp[1]);
   }
   return data;
 }

 function getStatements(sql:string){
  return sql.split(';');
 }

 function regMetaData(statement:string){
   const regTable = new RegExp(/create\s+table\s+(\w+?)\s*\(/, 'ig');
   let table = RegStr(statement,regTable);
   const regConnector = new RegExp(/connector'\s*=\s*'(\w+?)'/, 'ig');
   let connector = RegStr(statement,regConnector);
   const regColumn = new RegExp(/\s+(\w+?)\s+(\w+)\s+[\)|,]/, 'ig');
   let temp = [],columns = [];
   while ((temp = regColumn.exec(statement)) !== null) {
     columns.push({
       name:temp[1],
       type:temp[2]
     });
   }
   return {
     table:table[0],
     connector:connector[0],
     columns:columns
   }
 }


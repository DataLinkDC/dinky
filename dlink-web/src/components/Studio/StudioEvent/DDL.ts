import {executeDDL} from "@/pages/FlinkSqlStudio/service";
import FlinkSQL from "./FlinkSQL";

export function showTables(clusterId:number,clusterName:string,session:string,dispatch:any) {
  const res = executeDDL({
    statement:FlinkSQL.SHOW_TABLES,
    clusterId: clusterId,
    session:session,
  });
  res.then((result)=>{
    let tableData = [];
    if(result.datas.result.rowData.length>0){
      tableData = result.datas.result.rowData;
    }
    dispatch&&dispatch({
      type: "Studio/refreshCurrentSessionCluster",
      payload: {
        session: session,
        clusterId: clusterId,
        clusterName: clusterName,
        connectors: tableData,
      },
    });
  });
}

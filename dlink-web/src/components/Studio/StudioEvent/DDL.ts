import {executeDDL} from "@/pages/FlinkSqlStudio/service";
import FlinkSQL from "./FlinkSQL";
import {TaskType} from "@/pages/FlinkSqlStudio/model";

export function showTables(task:TaskType,dispatch:any) {
  const res = executeDDL({
    statement:FlinkSQL.SHOW_TABLES,
    clusterId: task.clusterId,
    session:task.session,
    useRemote:task.useRemote,
    useSession:task.useSession,
    useResult:true,
  });
  res.then((result)=>{
    let tableData = [];
    if(result.datas.rowData.length>0){
      tableData = result.datas.rowData;
    }
    dispatch&&dispatch({
      type: "Studio/refreshCurrentSessionCluster",
      payload: {
        session: task.session,
        clusterId: task.clusterId,
        clusterName: task.clusterName,
        connectors: tableData,
      },
    });
  });
}

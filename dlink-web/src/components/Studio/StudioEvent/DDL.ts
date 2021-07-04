import {executeDDL} from "@/pages/FlinkSqlStudio/service";
import FlinkSQL from "./FlinkSQL";
import {TaskType} from "@/pages/FlinkSqlStudio/model";
import {Modal} from "antd";
import {handleRemove} from "@/components/Common/crud";

export function showTables(task:TaskType,dispatch:any) {
  const res = executeDDL({
    statement:FlinkSQL.SHOW_TABLES,
    clusterId: task.clusterId,
    session:task.session,
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

export function removeTable(tablename:string,task:TaskType,dispatch:any) {
  Modal.confirm({
    title: '确定删除表【'+tablename+'】吗？',
    okText: '确认',
    cancelText: '取消',
    onOk:async () => {
      const res = executeDDL({
        statement:"drop table "+tablename,
        clusterId: task.clusterId,
        session:task.session,
        useSession:task.useSession,
        useResult:true,
      });
      res.then((result)=>{
        showTables(task,dispatch);
      });
    }
  });
}

export function clearSession(session:string,task:TaskType,dispatch:any) {
  Modal.confirm({
    title: '确认清空会话【'+session+'】？',
    okText: '确认',
    cancelText: '取消',
    onOk:async () => {
      let para = {
        id:session,
      };
      const res = handleRemove('/api/studio/clearSession',[para]);
      res.then((result)=>{
        showTables(task,dispatch);
      });
    }
  });
}

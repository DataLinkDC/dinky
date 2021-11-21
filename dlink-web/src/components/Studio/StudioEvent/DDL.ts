import {executeDDL} from "@/pages/FlinkSqlStudio/service";
import FlinkSQL from "./FlinkSQL";
import {SessionType} from "@/pages/FlinkSqlStudio/model";
import {Modal,message} from "antd";
import {addOrUpdateData, getData, handleRemove} from "@/components/Common/crud";
/*--- 创建会话 ---*/
export function createSession(session: SessionType,dispatch: any) {
  const res = addOrUpdateData("api/studio/createSession",session)
  res.then((result) => {
    message.success(`创建会话【${session.session}】成功！`);
    result.datas&&changeSession(result.datas,dispatch);
    listSession(dispatch);
  });
}
/*--- 查询会话列表 ---*/
export function listSession(dispatch: any) {
  const res = getData("api/studio/listSession");
  res.then((result)=>{
    dispatch && dispatch({
      type: "Studio/saveSession",
      payload: result.datas,
    });
  });
}
/*--- 切换会话 ---*/
export function changeSession(session: SessionType, dispatch: any) {
  dispatch && dispatch({
    type: "Studio/refreshCurrentSession",
    payload: session,
  });
  setTimeout(function () {
    showTables(session.session,dispatch);
  },200);
}
/*--- 退出会话 ---*/
export function quitSession( dispatch: any) {
  dispatch && dispatch({
    type: "Studio/quitCurrentSession",
  });
}
/*--- 注销会话 ---*/
export function clearSession(session: string, dispatch: any) {
  Modal.confirm({
    title: '确认注销会话【' + session + '】？',
    okText: '确认',
    cancelText: '取消',
    onOk: async () => {
      let para = {
        id: session,
      };
      const res = handleRemove('/api/studio/clearSession', [para]);
      res.then((result) => {
        quitSession(dispatch);
        listSession(dispatch);
      });
    }
  });
}
/*--- 刷新 Catalog Table ---*/
export function showTables(session: string, dispatch: any) {
  if(session==null||session==''){
    return;
  }
  const res = executeDDL({
    statement: FlinkSQL.SHOW_TABLES,
    session: session,
    useSession: true,
    useResult: true,
  });
  res.then((result) => {
    let tableData = [];
    if (result.datas.rowData.length > 0) {
      tableData = result.datas.rowData;
    }
    dispatch && dispatch({
      type: "Studio/refreshCurrentSession",
      payload: {
        connectors:tableData
      },
    });
  });
}
/*--- 移除 Catalog Table ---*/
export function removeTable(tablename: string, session: string, dispatch: any) {
  Modal.confirm({
    title: '确定删除表【' + tablename + '】吗？',
    okText: '确认',
    cancelText: '取消',
    onOk: async () => {
      const res = executeDDL({
        statement: "drop table " + tablename,
        session: session,
        useSession: true,
        useResult: true,
      });
      res.then((result) => {
        showTables(session, dispatch);
      });
    }
  });
}
/*--- 刷新 集群 ---*/
export function showCluster(dispatch: any) {
  const res = getData('api/cluster/listEnabledAll');
  res.then((result) => {
    result.datas && dispatch && dispatch({
      type: "Studio/saveCluster",
      payload: result.datas,
    });
  });
}
/*--- 刷新 Session集群 ---*/
export function showSessionCluster(dispatch: any) {
  const res = getData('api/cluster/listSessionEnable');
  res.then((result) => {
    result.datas && dispatch && dispatch({
      type: "Studio/saveSessionCluster",
      payload: result.datas,
    });
  });
}
/*--- 刷新 数据源 ---*/
export function showDataBase(dispatch: any) {
  const res = getData('api/database/listEnabledAll');
  res.then((result) => {
    result.datas && dispatch && dispatch({
      type: "Studio/saveDataBase",
      payload: result.datas,
    });
  });
}
/*--- 刷新 元数据表 ---*/
export function showMetaDataTable(id:number) {
  return getData('api/database/getSchemasAndTables',{id:id});
}
/*--- 刷新 Flink Jobs ---*/
export function showFlinkJobs(clusterId:number) {
  return getData('api/studio/listJobs',{clusterId:clusterId});
}
/*--- 停止 Flink Jobs ---*/
export function cancelJob(clusterId:number,jobId:string) {
  return getData('api/studio/cancel',{clusterId:clusterId,jobId:jobId});
}
/*--- 停止 SavePoint Jobs ---*/
export function savepointJob(clusterId:number,jobId:string,savePointType:string,name:string) {
  return getData('api/studio/savepoint',{clusterId,jobId,savePointType,name});
}
/*--- 根据版本号获取所有自动补全的文档 ---*/
export function getFillAllByVersion(version:string,dispatch: any) {
  const res = getData('api/document/getFillAllByVersion',{version:version});
  res.then((result) => {
    result.datas && dispatch && dispatch({
      type: "Document/saveAllFillDocuments",
      payload: result.datas,
    });
  });
}
/*--- 刷新 集群 ---*/
export function showClusterConfiguration(dispatch: any) {
  const res = getData('api/clusterConfiguration/listEnabledAll');
  res.then((result) => {
    result.datas && dispatch && dispatch({
      type: "Studio/saveClusterConfiguration",
      payload: result.datas,
    });
  });
}

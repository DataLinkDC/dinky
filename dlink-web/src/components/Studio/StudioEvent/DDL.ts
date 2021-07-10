import {executeDDL} from "@/pages/FlinkSqlStudio/service";
import FlinkSQL from "./FlinkSQL";
import {SessionType, TaskType} from "@/pages/FlinkSqlStudio/model";
import {Modal} from "antd";
import {getData, handleRemove} from "@/components/Common/crud";

export function changeSession(session: SessionType, dispatch: any) {
  dispatch && dispatch({
    type: "Studio/refreshCurrentSession",
    payload: session,
  });
  setTimeout(function () {
    showTables(session.session,dispatch);
  },200);
}

export function quitSession( dispatch: any) {
  dispatch && dispatch({
    type: "Studio/quitCurrentSession",
  });
}

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

export function clearSession(session: string, dispatch: any) {
  Modal.confirm({
    title: '确认清空会话【' + session + '】？',
    okText: '确认',
    cancelText: '取消',
    onOk: async () => {
      let para = {
        id: session,
      };
      const res = handleRemove('/api/studio/clearSession', [para]);
      res.then((result) => {
        quitSession(dispatch);
      });
    }
  });
}

export function showCluster(dispatch: any) {
  const res = getData('api/cluster/listEnabledAll');
  res.then((result) => {
    result.datas && dispatch && dispatch({
      type: "Studio/saveCluster",
      payload: result.datas,
    });
  });
}

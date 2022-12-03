/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */


import {executeDDL, getMSCatalogs} from "@/pages/DataStudio/service";
import FlinkSQL from "./FlinkSQL";
import {MetaStoreCatalogType, SessionType, TaskType} from "@/pages/DataStudio/model";
import {message, Modal} from "antd";
import {addOrUpdateData, getData, handleRemove, postAll} from "@/components/Common/crud";
import {getIntl} from "umi";

/*--- 保存sql ---*/
export function saveTask(current: any, dispatch: any) {
  if (current.task) {
    let task = {
      ...current.task,
      statement: current.value,
    };
    dispatch && dispatch({
      type: "Studio/saveTask",
      payload: task,
    });
  }
}

/*--- 创建会话 ---*/
export function createSession(session: SessionType, dispatch: any) {
  const res = addOrUpdateData("api/studio/createSession", session)
  res.then((result) => {
    message.success(`创建会话【${session.session}】成功！`);
    result.datas && changeSession(result.datas, dispatch);
    listSession(dispatch);
  });
}

/*--- 查询会话列表 ---*/
export function listSession(dispatch: any) {
  const res = getData("api/studio/listSession");
  res.then((result) => {
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
    showTables(session.session, dispatch);
  }, 200);
}

/*--- 退出会话 ---*/
export function quitSession(dispatch: any) {
  dispatch && dispatch({
    type: "Studio/quitCurrentSession",
  });
}

/*--- 注销会话 ---*/
export function clearSession(session: string, dispatch: any) {
  Modal.confirm({
    title: getIntl().formatMessage({id: 'tips.confirm.logout.session'}, {sessionName: session}),
    okText: getIntl().formatMessage({id: 'button.confirm'}),
    cancelText: getIntl().formatMessage({id: 'button.cancel'}),
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
  if (session == null || session == '') {
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
        connectors: tableData
      },
    });
  });
}

/*--- 移除 Catalog Table ---*/
export function removeTable(tablename: string, session: string, dispatch: any) {
  Modal.confirm({
    title: getIntl().formatMessage({id: 'tips.confirm.delete.table'}, {tableName: tablename}),
    okText: getIntl().formatMessage({id: 'button.confirm'}),
    cancelText: getIntl().formatMessage({id: 'button.cancel'}),
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

/*--- 刷新 执行环境 ---*/
export function showEnv(dispatch: any) {
  const res = getData('api/task/listFlinkSQLEnv');
  res.then((result) => {
    result.datas && dispatch && dispatch({
      type: "Studio/saveEnv",
      payload: result.datas,
    });
  });
}

/*--- 刷新 自定义Jar ---*/
export function showJars(dispatch: any) {
  const res = getData('api/jar/listEnabledAll');
  res.then((result) => {
    result.datas && dispatch && dispatch({
      type: "Jar/saveJars",
      payload: result.datas,
    });
  });
}

/*--- 刷新 报警实例 ---*/
export function showAlertInstance(dispatch: any) {
  const res = getData('api/alertInstance/listEnabledAll');
  res.then((result) => {
    result.datas && dispatch && dispatch({
      type: "Alert/saveInstance",
      payload: result.datas,
    });
  });
}

/*--- 刷新 报警组 ---*/
export function showAlertGroup(dispatch: any) {
  const res = getData('api/alertGroup/listEnabledAll');
  res.then((result) => {
    result.datas && dispatch && dispatch({
      type: "Alert/saveGroup",
      payload: result.datas,
    });
  });
}

/*--- 刷新 元数据表 ---*/
export function showMetaDataTable(id: number) {
  return getData('api/database/getSchemasAndTables', {id: id});
}

/*--- 清理 元数据表缓存 ---*/
export function clearMetaDataTable(id: number) {
  return getData('api/database/unCacheSchemasAndTables', {id: id});
}

/*--- 刷新 数据表样例数据 ---*/
export function showTableData(id: number, schemaName: String, tableName: String, option: {}) {
  return postAll('api/database/queryData', {id: id, schemaName: schemaName, tableName: tableName, option: option});
}
/*--- 执行sql---*/
export function execDatabaseSql(id: number, sql: String) {
  return postAll('api/database/execSql', {id: id, sql: sql});
}

/*--- 刷新 Flink Jobs ---*/
export function showFlinkJobs(clusterId: number) {
  return getData('api/studio/listJobs', {clusterId: clusterId});
}

/*--- 停止 Flink Jobs ---*/
export function cancelJob(clusterId: number, jobId: string) {
  return getData('api/studio/cancel', {clusterId: clusterId, jobId: jobId});
}

/*--- 重启 Flink Jobs ---*/
export function restartJob(id: number, isOnLine: boolean) {
  return getData('api/task/restartTask', {id, isOnLine});
}

/*--- 停止 SavePoint Jobs ---*/
export function savepointJob(clusterId: number, jobId: string, savePointType: string, name: string, taskId: number) {
  return getData('api/studio/savepoint', {clusterId, jobId, savePointType, name, taskId});
}

/*--- 根据版本号获取所有自动补全的文档 ---*/
export function getFillAllByVersion(version: string, dispatch: any) {
  const res = getData('api/document/getFillAllByVersion', {version: version});
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

/*--- 发布作业 ---*/
export function releaseTask(id: number) {
  return getData('api/task/releaseTask', {id});
}

/*--- 发布作业 ---*/
export function developTask(id: number) {
  return getData('api/task/developTask', {id});
}

/*--- 上线作业 ---*/
export function onLineTask(id: number) {
  return getData('api/task/onLineTask', {id});
}

/*--- 下线作业 ---*/
export function offLineTask(id: number, type: string) {
  return getData('api/task/offLineTask', {id, type});
}

/*--- 注销作业 ---*/
export function cancelTask(id: number) {
  return getData('api/task/cancelTask', {id});
}

/*--- 恢复作业 ---*/
export function recoveryTask(id: number) {
  return getData('api/task/recoveryTask', {id});
}

/*--- 刷新 MetaStore Catalogs ---*/
export async function showMetaStoreCatalogs(task: TaskType, dispatch: any) {
  if (!task?.dialect) {
    return;
  }
  let param = {
    envId: task.envId,
    fragment: task.fragment,
    dialect: task.dialect,
    databaseId: task.databaseId,
  };
  const result = getMSCatalogs(param);
  result.then(res => {
    const catalogs: MetaStoreCatalogType[] = [];
    if (res.datas) {
      for (let i = 0; i < res.datas.length; i++) {
        catalogs.push({
          name: res.datas[i].name,
          databases: res.datas[i].schemas,
        });
      }
    }
    dispatch && dispatch({
      type: "Studio/saveMetaStore",
      payload: {
        activeKey: task.id,
        metaStore: catalogs
      },
    });
  })
}

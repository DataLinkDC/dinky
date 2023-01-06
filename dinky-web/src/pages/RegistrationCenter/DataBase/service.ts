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


import {getInfoById, handleAddOrUpdate, postAll} from "@/components/Common/crud";
import {DataBaseItem} from "@/pages/RegistrationCenter/data";
import {message} from "antd";
import {l} from "@/utils/intl";

export async function createOrModifyDatabase(databse: DataBaseItem) {
  return handleAddOrUpdate('/api/database', databse);
}

export async function testDatabaseConnect(databse: DataBaseItem) {
  const hide = message.loading(l('app.request.test.connection'));
  try {
    const {code,msg} = await postAll('/api/database/testConnect',databse);
    hide();
    code==0?message.success(msg):message.error(msg);
  } catch (error) {
    hide();
    message.error(l('app.request.failed'));
  }
}

export async function checkHeartBeat(id: number) {
  const hide = message.loading(l('app.request.heartbeat.connection'));
  try {
    const {datas} = await getInfoById('/api/database/checkHeartBeatById',id);
    hide();
    datas.status==1?message.success(
        l('app.request.heartbeat.connection.success','',{time :datas.heartbeatTime }))
      :
      message.error(l('app.request.heartbeat.connection.failed','',{time :datas.heartbeatTime }));
  } catch (error) {
    hide();
    message.error(l('app.request.failed'));
  }
}


export async function copyDatabase(databse: DataBaseItem) {
  const hide = message.loading(l('pages.rc.db.copyDatabase'));
  try {
    const {code, msg} = await postAll('/api/database/copyDatabase', databse);
    hide();
    code == 0 ? message.success(msg) : message.error(msg);
  } catch (error) {
    hide();
    message.error(l('app.request.failed'));
  }
}

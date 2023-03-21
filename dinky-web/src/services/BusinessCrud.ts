/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {message} from "antd";
import {addOrUpdateData, getData, postAll, postDataArray, removeData} from "@/services/api";
import {l} from "@/utils/intl";
import {showMsgTips} from "@/services/function";


export const handleAddOrUpdate = async (url: string, fields: any) => {
  const tipsTitle = fields.id ? l('app.request.update') : l('app.request.add');
  const hide = message.loading(l('app.request.running') + tipsTitle);
  try {
    const {code, msg} = await addOrUpdateData(url, {...fields});
    hide();
    showMsgTips({code, msg})
    return true;
  } catch (error) {
    hide();
    message.error(l('app.request.error'));
    return false;
  }
};

export const handleAddOrUpdateWithResult = async (url: string, fields: any) => {
  const tipsTitle = fields.id ? l('app.request.update') : l('app.request.add');
  const hide = message.loading(l('app.request.running') + tipsTitle);
  try {
    const {code, msg, datas} = await addOrUpdateData(url, {...fields});
    hide();
    showMsgTips({code, msg})
    return datas;
  } catch (error) {
    hide();
    message.error(l('app.request.error'));
    return null;
  }
};

export const handleRemove = async (url: string, selectedRows: any) => {
  const hide = message.loading(l('app.request.delete'));
  if (!selectedRows) return true;
  try {
    const {code, msg} = await removeData(url, selectedRows.map((row: any) => row.id));
    hide();
    showMsgTips({code, msg})
    return true;
  } catch (error) {
    hide();
    message.error(l('app.request.delete.error'));
    return false;
  }
};

export const handleRemoveById = async (url: string, id: number) => {
  const hide = message.loading(l('app.request.delete'));
  try {
    const {code, msg} = await removeData(url, [id]);
    hide();
    showMsgTips({code, msg})
    return true;
  } catch (error) {
    hide();
    message.error(l('app.request.delete.error'));
    return false;
  }
};

export const handleSubmit = async (url: string, title: string, selectedRows: any[]) => {
  const hide = message.loading(l('app.request.running') + title);
  if (!selectedRows) return true;
  try {
    const {code, msg} = await postDataArray(url, selectedRows.map((row) => row.id));
    hide();
    showMsgTips({code, msg})
    return true;
  } catch (error) {
    hide();
    message.error(title + l('app.request.error.try'));
    return false;
  }
};

export const updateEnabled = (url: string, selectedRows: [], enabled: boolean) => {
  selectedRows.forEach((item: any) => {
    handleAddOrUpdate(url, {id: item.id, enabled: enabled})
  })
};

export const handleOption = async (url: string, title: string, param: any) => {
  const hide = message.loading(l('app.request.running') + title);
  try {
    const {code, msg} = await postAll(url, param);
    hide();
    showMsgTips({code, msg})
    return true;
  } catch (error) {
    hide();
    message.error(title + l('app.request.error.try'));
    return false;
  }
};

export const handleData = async (url: string, id: any) => {
  try {
    const {code, datas, msg} = await getData(url, id);
    if (code === NetWork.RESPONSE_CODE.SUCCESS) {
      return datas;
    } else {
      message.warning(msg);
      return false;
    }
  } catch (error) {
    message.error(l('app.request.geterror.error'));
    return false;
  }
};

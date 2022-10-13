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


import {extend} from "umi-request";
import {TableListParams} from "@/components/Common/data";
import {message} from "antd";


export const request2 = extend(
  { headers: { tenantId: localStorage.getItem('dlink-tenantId') || ''} }
);

export const getStorageTenantId = () => {
  return localStorage.getItem('dlink-tenantId') || '';
}


export const CODE = {
  SUCCESS: 0,
  ERROR: 1,
};

export async function queryData(url:string,params?: TableListParams) {
  return request2(url, {
    method: 'POST',
    data: {
      ...params,
    },
  });
}

export async function getData(url:string,params?: any) {
  return request2(url, {
    method: 'GET',
    params: {
      ...params,
    },
  });
}

export async function removeData(url:string,params: any[]) {
  return request2(url, {
    method: 'DELETE',
    data: {
      ...params,
    },
  });
}

export async function addOrUpdateData(url:string,params: any) {
  return request2(url, {
    method: 'PUT',
    data: {
      ...params,
    },
  });
}

export async function postDataArray(url:string,params: number[]) {
  return request2(url, {
    method: 'POST',
    data: {
      ...params,
    },
  });
}

export async function postAll(url:string,params?: any) {
  return request2(url, {
    method: 'POST',
    data: {
      ...params,
    },
  });
}

export async function getInfoById(url:string,id:number) {
  return request2(url, {
    method: 'GET',
    params: {
      id:id,
    },
  });
}

export const handleAddOrUpdate = async (url:string,fields: any) => {
  const tipsTitle = fields.id ? "修改" : "添加";
  const hide = message.loading(`正在${tipsTitle}`);
  try {
    const {code,msg} = await addOrUpdateData(url,{...fields});
    hide();
    if(code == CODE.SUCCESS){
      message.success(msg);
    }else{
      message.warn(msg);
    }
    return true;
  } catch (error) {
    hide();
    message.error('出错啦');
    return false;
  }
};

export const handleAddOrUpdateWithResult = async (url:string,fields: any) => {
  const tipsTitle = fields.id ? "修改" : "添加";
  const hide = message.loading(`正在${tipsTitle}`);
  try {
    const {code, msg,datas} = await addOrUpdateData(url,{...fields});
    hide();
    if(code == CODE.SUCCESS){
      message.success(msg);
    }else{
      message.warn(msg);
    }
    return datas;
  } catch (error) {
    hide();
    message.error('出错啦');
    return null;
  }
};

export const handleRemove = async (url:string,selectedRows: []) => {
  const hide = message.loading('正在删除');
  if (!selectedRows) return true;
  try {
    const {code, msg} = await removeData(url,selectedRows.map((row) => row.id));
    hide();
    if(code == CODE.SUCCESS){
      message.success(msg);
    }else{
      message.warn(msg);
    }
    return true;
  } catch (error) {
    hide();
    message.error('删除失败，请重试');
    return false;
  }
};

export const handleRemoveById = async (url:string,id: number) => {
  const hide = message.loading('正在删除');
  try {
    const {code, msg} = await removeData(url,[id]);
    hide();
    if(code == CODE.SUCCESS){
      message.success(msg);
    }else{
      message.warn(msg);
    }
    return true;
  } catch (error) {
    hide();
    message.error('删除失败，请重试');
    return false;
  }
};

export const handleSubmit = async (url:string,title:string,selectedRows: any[]) => {
  const hide = message.loading('正在'+title);
  if (!selectedRows) return true;
  try {
    const {code, msg} = await postDataArray(url,selectedRows.map((row) => row.id));
    hide();
    if(code == CODE.SUCCESS){
      message.success(msg);
    }else{
      message.warn(msg);
    }
    return true;
  } catch (error) {
    hide();
    message.error(title+'失败，请重试');
    return false;
  }
};

export const updateEnabled = (url:string,selectedRows: [], enabled: boolean) => {
  selectedRows.forEach((item) => {
    handleAddOrUpdate(url,{id: item.id, enabled: enabled})
  })
};

export const handleOption = async (url:string,title:string,param:any) => {
  const hide = message.loading('正在'+title);
  try {
    const {code, msg} = await postAll(url,param);
    hide();
    if(code == CODE.SUCCESS){
      message.success(msg);
    }else{
      message.warn(msg);
    }
    return true;
  } catch (error) {
    hide();
    message.error(title+'失败，请重试');
    return false;
  }
};

export const handleData = async (url:string,id:any) => {
  try {
    const {code,datas,msg} = await getData(url,id);
    if(code == CODE.SUCCESS){
      return datas;
    }else{
      message.warn(msg);
      return false;
    }
  } catch (error) {
    message.error('获取失败，请重试');
    return false;
  }
};

export const handleInfo = async (url:string,id:number) => {
  try {
    const {datas} = await getInfoById(url,id);
    return datas;
  } catch (error) {
    message.error('获取失败，请重试');
    return false;
  }
};

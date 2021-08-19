import request from "umi-request";
import {getInfoById, handleAddOrUpdate, handleOption, postAll} from "@/components/Common/crud";
import {DataBaseItem} from "@/pages/DataBase/data";
import {message} from "antd";
import {Protocol} from "puppeteer-core";

export async function createOrModifyDatabase(databse: DataBaseItem) {
  return handleAddOrUpdate('/api/database', databse);
}

export async function testDatabaseConnect(databse: DataBaseItem) {
  const hide = message.loading('正在测试连接');
  try {
    const {code,msg} = await postAll('/api/database/testConnect',databse);
    hide();
    code==0?message.success(msg):message.error(msg);
  } catch (error) {
    hide();
    message.error('请求失败，请重试');
  }
}

export async function checkHeartBeat(id: number) {
  const hide = message.loading('正在检测心跳');
  try {
    const {datas} = await getInfoById('/api/database/checkHeartBeatById',id);
    hide();
    datas.status==1?message.success("数据源心跳正常，检测时间为"+datas.heartbeatTime):message.error("数据源心跳异常，检测时间为"+datas.heartbeatTime);
  } catch (error) {
    hide();
    message.error('请求失败，请重试');
  }
}

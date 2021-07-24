import request from "umi-request";
import {handleAddOrUpdate, handleOption, postAll} from "@/components/Common/crud";
import {DataBaseItem} from "@/pages/DataBase/data";
import {message} from "antd";

export async function createOrModifyDatabase(databse: DataBaseItem) {
  return handleAddOrUpdate('/api/database', databse);
}

export async function testDatabaseConnect(databse: DataBaseItem) {
  const hide = message.loading('正在测试连接');
  try {
    const {datas} = await postAll('/api/database/testConnect',databse);
    hide();
    datas?message.success("数据源连接测试成功！"):message.error("数据源连接测试失败，请检查连接配置。");
  } catch (error) {
    hide();
    message.error('请求失败，请重试');
  }
}

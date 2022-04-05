import {handleAddOrUpdate, postAll} from "@/components/Common/crud";
import {AlertInstanceTableListItem} from "@/pages/AlertInstance/data";
import {message} from "antd";

export async function createOrModifyAlertInstance(alertInstance: AlertInstanceTableListItem) {
  return handleAddOrUpdate('/api/alertInstance', alertInstance);
}

export async function sendTest(alertInstance: AlertInstanceTableListItem) {
  const hide = message.loading('正在发送测试告警信息');
  try {
    const {code,msg} = await postAll('/api/alertInstance/sendTest',alertInstance);
    hide();
    code==0?message.success(msg):message.error(msg);
  } catch (error) {
    hide();
    message.error('请求失败，请重试');
  }
}

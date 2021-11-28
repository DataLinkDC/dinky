import {postAll} from "@/components/Common/crud";
import {message} from "antd";
import {ClusterConfigurationTableListItem} from "@/pages/ClusterConfiguration/data";

export async function testClusterConfigurationConnect(clusterConfiguration: ClusterConfigurationTableListItem) {
  const hide = message.loading('正在测试连接');
  try {
    const {code,msg} = await postAll('/api/clusterConfiguration/testConnect',clusterConfiguration);
    hide();
    code==0?message.success(msg):message.error(msg);
  } catch (error) {
    hide();
    message.error('请求失败，请重试');
  }
}

import {Typography, Divider, Badge, Empty} from "antd";


const { Title, Paragraph, Text, Link } = Typography;

const StudioMsg = () => {

  return (
    <Typography>
      <Title level={3}>基本概念与使用</Title>
      <Paragraph>
        介绍了 0.2.1 版本 Flink 集群、共享会话、同步执行、异步提交的概念及使用。
      </Paragraph>
      <Title level={4}>Flink 集群</Title>
      <Paragraph>
        <p>Flink 集群主要有两种，LOCAL 和 REMOTE，通过集群中心进行新集群的注册，注册成功后，点击心跳刷新状态，需要重新进入Studio后新集群才会被加载到下拉框。</p>
        <p>LOCAL 模式为通过 dlink 自身环境和内存进行 FlinkSql 的执行。</p>
        <p>REMOTE 模式会将 FlinkSql 进行解析处理后提交到目标集群进行执行。</p>
      </Paragraph>
      <Title level={4}>共享会话</Title>
      <Paragraph>
        <p>FlinkSql 执行过程所有创建的 Table 等都被存储到了共享会话的 Catalogue 中，不同集群间的 Catalogue 不共享。</p>
      </Paragraph>
      <Title level={4}>同步执行</Title>
      <Paragraph>
        <p>同步执行当前选项卡的 FlinkSql 在选中的集群上执行，执行完成后将数据结果展示在前端。</p>
      </Paragraph>
      <Title level={4}>异步提交</Title>
      <Paragraph>
        <p>异步提交当前选项卡或右键的树节点的 FlinkSql 在选中的集群上异步执行，无返回值，不记录历史。</p>
      </Paragraph>
    </Typography>
  );
};

export default StudioMsg;

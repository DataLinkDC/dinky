import {
  message, Button, Table, Empty, Divider,
  Tooltip
} from "antd";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {connect} from "umi";
import {useState} from "react";
import styles from "./index.less";
import {
  ReloadOutlined,
  PlusOutlined
} from '@ant-design/icons';
import React from "react";
import {showCluster} from "../../StudioEvent/DDL";

const StudioCluster = (props: any) => {

  const {cluster, dispatch} = props;

  const getColumns = () => {
    let columns: any = [{
      title: "集群名",
      dataIndex: "alias",
      key: "alias",
      sorter: true,
    }, {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => [
        <a
          onClick={() => {
            message.success('敬请期待');
          }}
        >
          详情
        </a>, <Divider type="vertical"/>, <a
          onClick={() => {
            message.success('敬请期待');
          }}
        >
          管理
        </a>
      ],
    },];
    return columns;
  };

  const onRefreshCluster = () => {
    showCluster(dispatch);
  };

  const onCreateCluster = () => {

  };

  return (
    <>
      <Tooltip title="新建 Flink 集群">
        <Button
          type="text"
          icon={<PlusOutlined/>}
          onClick={onCreateCluster}
        />
      </Tooltip>
      <div style={{float: "right"}}>
        <Tooltip title="刷新 Flink 集群">
          <Button
            type="text"
            icon={<ReloadOutlined />}
            onClick={onRefreshCluster}
          />
        </Tooltip>
      </div>
      {cluster.length > 0 ? (
        <Table dataSource={cluster} columns={getColumns()} size="small"/>) : (
        <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>)}
    </>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  cluster: Studio.cluster,
}))(StudioCluster);

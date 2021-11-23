import {
  message, Button, Table, Empty, Divider,
  Tooltip,Drawer
} from "antd";
import ProDescriptions from '@ant-design/pro-descriptions';
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
import {handleAddOrUpdate} from "@/components/Common/crud";
import ClusterForm from "@/pages/Cluster/components/ClusterForm";
import {SavePointTableListItem} from "@/components/Studio/StudioRightTool/StudioSavePoint/data";

const StudioCluster = (props: any) => {

  const {cluster, dispatch} = props;
  const [createModalVisible, handleCreateModalVisible] = useState<boolean>(false);
  const [row, setRow] = useState<{}>();

  const getColumns = () => {
    return [{
      title: "集群名",
      dataIndex: "alias",
      key: "alias",
      sorter: true,
      render: (dom, entity) => {
        return <a onClick={() => setRow(entity)}>{dom}</a>;
      },
    }];
  };

  const getAllColumns = () => {
    return [{
      title: "集群名",
      dataIndex: "alias",
      key: "alias",
    },{
      title: '名称',
      dataIndex: 'name',
    },
      {
        title: '集群ID',
        dataIndex: 'id',
      },
      {
        title: '类型',
        sorter: true,
        dataIndex: 'type',
        filters: [
          {
            text: 'Yarn Session',
            value: 'yarn-session',
          },
          {
            text: 'Standalone',
            value: 'standalone',
          },
          {
            text: 'Yarn Per-Job',
            value: 'yarn-per-job',
          },
          {
            text: 'Yarn Application',
            value: 'yarn-application',
          },
        ],
        filterMultiple: false,
        valueEnum: {
          'yarn-session': {text: 'Yarn Session'},
          'standalone': {text: 'Standalone'},
          'yarn-per-job': {text: 'Yarn Per-Job'},
          'yarn-application': {text: 'Yarn Application'},
        },
      },
      {
        title: 'JobManager HA 地址',
        sorter: true,
        dataIndex: 'hosts',
        valueType: 'textarea',
      },
      {
        title: '当前 JobManager 地址',
        sorter: true,
        dataIndex: 'jobManagerHost',
      },{
        title: '版本',
        sorter: true,
        dataIndex: 'version',
      },
      {
        title: '状态',
        dataIndex: 'status',
        filters: [
          {
            text: '正常',
            value: 1,
          },
          {
            text: '异常',
            value: 0,
          },
        ],
        filterMultiple: false,
        valueEnum: {
          1: {text: '正常', status: 'Success'},
          0: {text: '异常', status: 'Error'},
        },
      },
      {
        title: '注释',
        sorter: true,
        valueType: 'textarea',
        dataIndex: 'note',
      },
      {
        title: '是否启用',
        dataIndex: 'enabled',
        filters: [
          {
            text: '已启用',
            value: 1,
          },
          {
            text: '已禁用',
            value: 0,
          },
        ],
        filterMultiple: false,
        valueEnum: {
          true: {text: '已启用', status: 'Success'},
          false: {text: '已禁用', status: 'Error'},
        },
      },
      {
        title: '注册方式',
        dataIndex: 'autoRegisters',
        filters: [
          {
            text: '自动',
            value: 1,
          },
          {
            text: '手动',
            value: 0,
          },
        ],
        filterMultiple: false,
        valueEnum: {
          true: {text: '自动', status: 'Success'},
          false: {text: '手动', status: 'Error'},
        },
      },
      {
        title: '创建时间',
        dataIndex: 'createTime',
        valueType: 'dateTime',
      },
      {
        title: '最近更新时间',
        dataIndex: 'updateTime',
        valueType: 'dateTime',
      },
      {
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
  };

  const onRefreshCluster = () => {
    showCluster(dispatch);
  };

  const onCreateCluster = () => {
    handleCreateModalVisible(true);
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
      <ClusterForm
        onSubmit={async (value) => {
          const success = await handleAddOrUpdate("api/cluster", value);
          if (success) {
            handleCreateModalVisible(false);
            showCluster(dispatch);
          }
        }}
        onCancel={() => {
          handleCreateModalVisible(false);
        }}
        modalVisible={createModalVisible}
      />
      <Drawer
        width={600}
        visible={!!row}
        onClose={() => {
          setRow(undefined);
        }}
        closable={false}
      >
        {row?.name && (
          <ProDescriptions
            column={2}
            title={row?.name}
            request={async () => ({
            data: row || {},
          })}
            params={{
            id: row?.name,
          }}
            columns={getAllColumns()}
            />
            )}
      </Drawer>
    </>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  cluster: Studio.cluster,
}))(StudioCluster);

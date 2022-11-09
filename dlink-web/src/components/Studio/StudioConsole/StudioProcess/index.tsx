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


import {Button, Divider, Dropdown, Empty, Menu, message, Modal, Select, Space, Tag, Tooltip} from "antd";
import {StateType} from "@/pages/DataStudio/model";
import {connect} from "umi";
import React, {useState} from "react";
import {DownOutlined, SearchOutlined} from '@ant-design/icons';
import ProTable from '@ant-design/pro-table';
import {cancelJob, savepointJob, showFlinkJobs} from "../../StudioEvent/DDL";
import JobStatus from "@/components/Common/JobStatus";
import {parseSecondStr} from "@/components/Common/function";

const {Option} = Select;

const StudioProcess = (props: any) => {

  const {cluster} = props;
  const [jobsData, setJobsData] = useState<any>({});
  const [clusterId, setClusterId] = useState<number>();

  const savepoint = (key: string | number, currentItem: {}) => {
    Modal.confirm({
      title: key + '任务',
      content: `确定${key}该作业吗？`,
      okText: l('button.confirm'),
      cancelText: l('button.cancel'),
      onOk: async () => {
        if (!clusterId) return;
        let res = savepointJob(clusterId, currentItem.jid, key, key, 0);
        res.then((result) => {
          if (result.datas == true) {
            message.success(key + "成功");
            onRefreshJobs();
          } else {
            message.error(key + "失败");
          }
        });
      }
    });
  };

  const SavePointBtn: React.FC<{
    item: {};
  }> = ({item}) => (
    <Dropdown
      overlay={
        <Menu onClick={({key}) => savepoint(key, item)}>
          <Menu.Item key="trigger">Trigger</Menu.Item>
          <Menu.Item key="stop">Stop</Menu.Item>
          <Menu.Item key="cancel">Cancel</Menu.Item>
        </Menu>
      }
    >
      <a>
        SavePoint <DownOutlined/>
      </a>
    </Dropdown>
  );

  const getColumns = () => {
    let columns: any = [{
      title: "作业ID",
      dataIndex: "jid",
      key: "jid",
      sorter: true,
    }, {
      title: "作业名",
      dataIndex: "name",
      sorter: true,
    }, {
      title: "状态",
      dataIndex: "state",
      sorter: true,
      render: (_, row) => {
        return (<JobStatus status={row.state}/>);
      }
    }, {
      title: "开始时间",
      dataIndex: "start-time",
      sorter: true,
      valueType: 'dateTime',
    }, {
      title: "最近修改时间",
      dataIndex: "last-modification",
      sorter: true,
      valueType: 'dateTime',
    }, {
      title: "结束时间",
      dataIndex: "end-time",
      sorter: true,
      valueType: 'dateTime',
    }, {
      title: "耗时",
      sorter: true,
      render: (_, row) => {
        return (parseSecondStr(row.duration))
      }
    }, {
      title: "tasks",
      dataIndex: "tasks",
      sorter: true,
      render: (_, row) => {
        return (<>
            {row.tasks.total > 0 ? (<Tooltip title="TOTAL"><Tag color="#666">{row.tasks.total}</Tag></Tooltip>) : ''}
            {row.tasks.created > 0 ? (
              <Tooltip title="CREATED"><Tag color="#666">{row.tasks.created}</Tag></Tooltip>) : ''}
            {row.tasks.deploying > 0 ? (
              <Tooltip title="DEPLOYING"><Tag color="#666">{row.tasks.deploying}</Tag></Tooltip>) : ''}
            {row.tasks.running > 0 ? (
              <Tooltip title="RUNNING"><Tag color="#44b549">{row.tasks.running}</Tag></Tooltip>) : ''}
            {row.tasks.failed > 0 ? (
              <Tooltip title="FAILED"><Tag color="#ff4d4f">{row.tasks.failed}</Tag></Tooltip>) : ''}
            {row.tasks.finished > 0 ? (
              <Tooltip title="FINISHED"><Tag color="#108ee9">{row.tasks.finished}</Tag></Tooltip>) : ''}
            {row.tasks.reconciling > 0 ? (
              <Tooltip title="RECONCILING"><Tag color="#666">{row.tasks.reconciling}</Tag></Tooltip>) : ''}
            {row.tasks.scheduled > 0 ? (
              <Tooltip title="SCHEDULED"><Tag color="#666">{row.tasks.scheduled}</Tag></Tooltip>) : ''}
            {row.tasks.canceling > 0 ? (
              <Tooltip title="CANCELING"><Tag color="#feb72b">{row.tasks.canceling}</Tag></Tooltip>) : ''}
            {row.tasks.canceled > 0 ? (
              <Tooltip title="CANCELED"><Tag color="#db970f">{row.tasks.canceled}</Tag></Tooltip>) : ''}
          </>
        )
      }
    }, {
      title: l('global.table.operate'),
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => {
        let option = [<a
          onClick={() => {
            message.success(l('global.stay.tuned'));
          }}
        >
          详情
        </a>];
        if (record.state == 'RUNNING' || record.state == 'RECONCILING' || record.state == 'SCHEDULED') {
          option.push(<Divider type="vertical"/>);
          option.push(<a
            onClick={() => {
              onCancel(record.jid);
            }}
          >
            停止
          </a>);
        }
        option.push(<SavePointBtn key="savepoint" item={record}/>,)
        return option;
      },
    },];
    return columns;
  };

  const onCancel = (jobId: string) => {
    Modal.confirm({
      title: `确认停止作业【${jobId}】？`,
      okText: '停止',
      cancelText: '取消',
      onOk: async () => {
        if (!clusterId) return;
        let res = cancelJob(clusterId, jobId);
        res.then((result) => {
          if (result.datas == true) {
            message.success("停止成功");
            onRefreshJobs();
          } else {
            message.error("停止失败");
          }
        });
      }
    });
  };

  const getClusterOptions = () => {
    let itemList = [];
    for (let item of cluster) {
      let tag = (<><Tag
        color={item.enabled ? "processing" : "error"}>{item.type}</Tag>{item.alias === "" ? item.name : item.alias}</>);
      itemList.push(<Option value={item.id} label={tag}>
        {tag}
      </Option>)
    }
    return itemList;
  };

  const onChangeCluster = (value: number) => {
    setClusterId(value);
    onRefreshJobs();
  };

  const onRefreshJobs = () => {
    if (!clusterId) return;
    let res = showFlinkJobs(clusterId);
    res.then((result) => {
      for (let i in result.datas) {
        result.datas[i].duration = result.datas[i].duration * 0.001;
        if (result.datas[i]['end-time'] == -1) {
          result.datas[i]['end-time'] = null;
        }
      }
      setJobsData(result.datas);
    });
  };

  return (
    <div style={{width: '100%'}}>
      <Space>
        <Select
          // style={{width: '100%'}}
          placeholder="选择Flink集群"
          optionLabelProp="label"
          onChange={onChangeCluster}
        >
          {getClusterOptions()}
        </Select>
        <Button type="primary" icon={<SearchOutlined/>} onClick={onRefreshJobs}/>
      </Space>
      {jobsData.length > 0 ?
        (<ProTable dataSource={jobsData} columns={getColumns()} size="small" search={false} toolBarRender={false}
                   pagination={{
                     defaultPageSize: 5,
                     showSizeChanger: true,
                   }}
        />) : (<Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>)}
    </div>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  cluster: Studio.cluster,
}))(StudioProcess);

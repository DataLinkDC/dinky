import {
  message, Button,Table, Empty, Divider,
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
import {showDataBase} from "../../StudioEvent/DDL";
import DBForm from "@/pages/DataBase/components/DBForm";
import { Scrollbars } from 'react-custom-scrollbars';

const StudioDataBase = (props: any) => {

  const {database,toolHeight, dispatch} = props;
  const [chooseDBModalVisible, handleDBFormModalVisible] = useState<boolean>(false);
  const [values, setValues] = useState<any>({});

  const getColumns = () => {
    let columns: any = [{
      title: "数据源名",
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

  const onRefreshDataBase = () => {
    showDataBase(dispatch);
  };

  const onCreateDataBase = () => {
    setValues({});
    handleDBFormModalVisible(true);
  };

  return (
    <>
      <Tooltip title="新建数据源">
        <Button
          type="text"
          icon={<PlusOutlined/>}
          onClick={onCreateDataBase}
        />
      </Tooltip>
      <div style={{float: "right"}}>
        <Tooltip title="刷新数据源">
          <Button
            type="text"
            icon={<ReloadOutlined/>}
            onClick={onRefreshDataBase}
          />
        </Tooltip>
      </div>
      <Scrollbars style={{height: (toolHeight - 32)}}>
      {database.length > 0 ? (
        <Table dataSource={database} columns={getColumns()} size="small"/>) : (
        <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>)}
      <DBForm
        onCancel={() => {
          handleDBFormModalVisible(false);
          setValues({});
        }}
        modalVisible={chooseDBModalVisible}
        onSubmit={() => {
          showDataBase(dispatch);
        }}
        values={values}
      />
      </Scrollbars>
    </>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  database: Studio.database,
  toolHeight: Studio.toolHeight,
}))(StudioDataBase);

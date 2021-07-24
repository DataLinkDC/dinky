import {
  message, Input, Button, Space, Table, Dropdown, Menu, Empty, Divider,
  Tooltip
} from "antd";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {connect} from "umi";
import {useState} from "react";
import styles from "./index.less";
import {
  SearchOutlined,
  DownOutlined,
  DeleteOutlined,
  CommentOutlined,
  PoweroffOutlined,
  PlusOutlined
} from '@ant-design/icons';
import React from "react";
import {showDataBase} from "../../StudioEvent/DDL";
import DBForm from "@/pages/DataBase/components/DBForm";

const StudioDataBase = (props: any) => {

  const {database, dispatch} = props;
  const [chooseDBModalVisible, handleDBFormModalVisible] = useState<boolean>(false);
  const [values, setValues] = useState<any>({});

  const getColumns = () => {
    let columns: any = [{
      title: "名称",
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
            icon={<SearchOutlined/>}
            onClick={onRefreshDataBase}
          />
        </Tooltip>
      </div>
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
    </>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  database: Studio.database,
}))(StudioDataBase);

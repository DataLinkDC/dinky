import React from "react";
import styles from "./index.less";
import { Menu, Dropdown, Typography, Row, Col } from "antd";
import {CaretRightOutlined, DownOutlined, PlayCircleOutlined } from "@ant-design/icons";
import Space from "antd/es/space";
import Divider from "antd/es/divider";
import Button from "antd/es/button/button";
import Breadcrumb from "antd/es/breadcrumb/Breadcrumb";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {connect} from "umi";

const { SubMenu } = Menu;
//<Button shape="circle" icon={<CaretRightOutlined />} />
const menu = (
  <Menu>
    <Menu.Item>1st menu item</Menu.Item>
    <Menu.Item>2nd menu item</Menu.Item>
    <SubMenu title="sub menu">
      <Menu.Item>3rd menu item</Menu.Item>
      <Menu.Item>4th menu item</Menu.Item>
    </SubMenu>
    <SubMenu title="disabled sub menu" disabled>
      <Menu.Item>5d menu item</Menu.Item>
      <Menu.Item>6th menu item</Menu.Item>
    </SubMenu>
  </Menu>
);



const StudioMenu = (props:any) => {

  const {sql} = props;
  console.log(props);
  const executeSql = () =>{
    console.log('获取'+sql);
  };

  const runMenu = (
    <Menu>
      <Menu.Item onClick={executeSql}>执行</Menu.Item>
    </Menu>
  );

  return (
    <Row className={styles.container}>
      <Col span={24}>
        <div>
          <Space>
            <Dropdown overlay={menu}>
              <Button type="text" onClick={e => e.preventDefault()}>
                文件
              </Button>
            </Dropdown>
            <Dropdown overlay={menu}>
              <Button type="text" onClick={e => e.preventDefault()}>
                编辑
              </Button>
            </Dropdown>
            <Dropdown overlay={runMenu}>
              <Button type="text" onClick={e => e.preventDefault()}>
                执行
              </Button>
            </Dropdown>
            <Dropdown overlay={menu}>
              <Button type="text" onClick={e => e.preventDefault()}>
                帮助
              </Button>
            </Dropdown>
          </Space>
        </div>
      </Col>
      <Divider className={styles["ant-divider-horizontal-0"]}/>
      <Col span={24}>
        <Breadcrumb className={styles["dw-path"]}>
          <Breadcrumb.Item>数据仓库</Breadcrumb.Item>
          <Breadcrumb.Item>维度</Breadcrumb.Item>
          <Breadcrumb.Item>用户信息</Breadcrumb.Item>
        </Breadcrumb>
      </Col>
    </Row>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  sql: Studio.sql,
}))(StudioMenu);

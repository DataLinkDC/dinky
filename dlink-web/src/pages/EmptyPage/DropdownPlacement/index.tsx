import React from "react";
import styles from "./index.less";
import { Menu, Dropdown, Button, Row, Col } from "antd";

const menu = (
  <Menu>
    <Menu.Item>
      <a
        target="_blank"
        rel="noopener noreferrer"
        href="http://www.alipay.com/"
      >
        1st menu item
      </a>
    </Menu.Item>
    <Menu.Item>
      <a
        target="_blank"
        rel="noopener noreferrer"
        href="http://www.taobao.com/"
      >
        2nd menu item
      </a>
    </Menu.Item>
    <Menu.Item>
      <a target="_blank" rel="noopener noreferrer" href="http://www.tmall.com/">
        3rd menu item
      </a>
    </Menu.Item>
  </Menu>
);

export default () => (
  <Row>
    <Col span={24}>
      <div className={styles.container}>
        <div id="components-dropdown-demo-placement">
          <div>
            <Dropdown overlay={menu}>
              <Button>文件</Button>
            </Dropdown>
            <Dropdown overlay={menu}>
              <Button>编辑</Button>
            </Dropdown>
            <Dropdown overlay={menu}>
              <Button>运行</Button>
            </Dropdown>
            <Dropdown overlay={menu}>
              <Button>帮助</Button>
            </Dropdown>
          </div>
        </div>
      </div>
    </Col>
  </Row>
);

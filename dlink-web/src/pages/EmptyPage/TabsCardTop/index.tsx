import React from "react";
import styles from "./index.less";
import { Tabs } from "antd";

const { TabPane } = Tabs;

export default () => (
  <div className={styles.container}>
    <div id="components-tabs-demo-card-top">
      <div className="card-container">
        <Tabs type="card">
          <TabPane tab="Tab Title 1" key="1">
            <p>Content of Tab Pane 1</p>
            <p>Content of Tab Pane 1</p>
            <p>Content of Tab Pane 1</p>
          </TabPane>
          <TabPane tab="Tab Title 2" key="2">
            <p>Content of Tab Pane 2</p>
            <p>Content of Tab Pane 2</p>
            <p>Content of Tab Pane 2</p>
          </TabPane>
          <TabPane tab="Tab Title 3" key="3">
            <p>Content of Tab Pane 3</p>
            <p>Content of Tab Pane 3</p>
            <p>Content of Tab Pane 3</p>
          </TabPane>
        </Tabs>
      </div>
    </div>
  </div>
);

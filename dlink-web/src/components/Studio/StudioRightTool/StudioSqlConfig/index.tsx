import {connect} from "umi";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {
  Form, InputNumber, Select, Tag, Row, Col,  Tooltip, Button,
} from "antd";
import {MinusSquareOutlined} from "@ant-design/icons";
import styles from "./index.less";
import {useEffect, useState} from "react";
import { Scrollbars } from 'react-custom-scrollbars';

const { Option } = Select;

const StudioSqlConfig = (props: any) => {

  const {current,form,dispatch,tabs,database,toolHeight} = props;

  form.setFieldsValue(current.task);


  const onValuesChange = (change:any,all:any)=>{
    let newTabs = tabs;
    for(let i=0;i<newTabs.panes.length;i++){
      if(newTabs.panes[i].key==newTabs.activeKey){
        for(let key in change){
          newTabs.panes[i].task[key]=change[key];
        }
        break;
      }
    }

    dispatch&&dispatch({
      type: "Studio/saveTabs",
      payload: newTabs,
    });
  };


  const getDataBaseOptions = () => {
    const itemList = [];
    for (const item of database) {
      const tag = (<><Tag color={item.enabled ? "processing" : "error"}>{item.type}</Tag>{item.alias}</>);
      itemList.push(<Option key={item.id} value={item.id} label={tag}>
        {tag}
      </Option>)
    }
    return itemList;
  };

  return (
    <>
      <Row>
        <Col span={24}>
          <div style={{float: "right"}}>
            <Tooltip title="最小化">
              <Button
                type="text"
                icon={<MinusSquareOutlined />}
              />
            </Tooltip>
          </div>
        </Col>
      </Row>
      <Scrollbars style={{height:(toolHeight-32)}}>
    <Form
      form={form}
      layout="vertical"
      className={styles.form_setting}
      onValuesChange={onValuesChange}
    >
      <Row>
        <Col span={24}>
          <Form.Item label="数据源" tooltip={`选择 Sql 语句执行的数据源`}
                     name="databaseId"
                     className={styles.form_item}>
            <Select
              style={{width: '100%'}}
              placeholder="选择数据源"
              optionLabelProp="label"
            >
              {getDataBaseOptions()}
            </Select>
          </Form.Item>
        </Col>
        <Col span={24}>
          <Form.Item
            label="最大行数" className={styles.form_item} name="maxRowNum"
            tooltip='预览数据的最大行数'
          >
            <InputNumber min={1} max={9999} defaultValue={100} />
          </Form.Item>
        </Col>
      </Row>
    </Form>
      </Scrollbars>
      </>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  database: Studio.database,
  current: Studio.current,
  tabs: Studio.tabs,
  toolHeight: Studio.toolHeight,
}))(StudioSqlConfig);

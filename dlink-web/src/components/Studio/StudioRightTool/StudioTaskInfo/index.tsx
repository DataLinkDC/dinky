import {connect} from "umi";
import {StateType} from "@/pages/DataStudio/model";
import {
  Form, Row, Col, Tooltip, Button, Input, Descriptions, Typography
} from "antd";
import {MinusSquareOutlined} from "@ant-design/icons";
import styles from "./index.less";
import {Scrollbars} from 'react-custom-scrollbars';

const {TextArea} = Input;
const {Paragraph} = Typography;

const StudioTaskInfo = (props: any) => {

  const {current, form, dispatch, tabs, toolHeight} = props;

  form.setFieldsValue(current.task);

  const onValuesChange = (change: any, all: any) => {
    let newTabs = tabs;
    for (let i = 0; i < newTabs.panes.length; i++) {
      if (newTabs.panes[i].key == newTabs.activeKey) {
        for (let key in change) {
          newTabs.panes[i].task[key] = change[key];
        }
        break;
      }
    }

    dispatch && dispatch({
      type: "Studio/saveTabs",
      payload: newTabs,
    });
  };

  return (
    <>
      <Row>
        <Col span={24}>
          <div style={{float: "right"}}>
            <Tooltip title="最小化">
              <Button
                type="text"
                icon={<MinusSquareOutlined/>}
              />
            </Tooltip>
          </div>
        </Col>
      </Row>
      <Scrollbars style={{height: (toolHeight - 32)}}>
        <Descriptions bordered size="small" column={1}>
          <Descriptions.Item label="ID">
            <Paragraph copyable>{current.task.id}</Paragraph>
          </Descriptions.Item>
          <Descriptions.Item label="标题">
            {current.task.alias}
          </Descriptions.Item>
          <Descriptions.Item label="方言">
            {current.task.dialect}
          </Descriptions.Item>
          <Descriptions.Item label="版本">
            {current.task.versionId}
          </Descriptions.Item>
          <Descriptions.Item label="创建于">
            {current.task.createTime}
          </Descriptions.Item>
          <Descriptions.Item label="更新于">
            {current.task.updateTime}
          </Descriptions.Item>
        </Descriptions>
        <Form
          form={form}
          layout="vertical"
          className={styles.form_setting}
          onValuesChange={onValuesChange}
        >
          <Row>
            <Col span={24}>
              <Form.Item
                label="备注" className={styles.form_item} name="note"
              >
                <TextArea rows={4} maxLength={255}/>
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Scrollbars>
    </>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  current: Studio.current,
  tabs: Studio.tabs,
  toolHeight: Studio.toolHeight,
}))(StudioTaskInfo);

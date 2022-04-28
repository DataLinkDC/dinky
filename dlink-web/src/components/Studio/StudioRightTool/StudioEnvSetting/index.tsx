import {connect} from "umi";
import {StateType} from "@/pages/DataStudio/model";
import {Form, Switch, Row, Col,Tooltip, Button} from "antd";
import {InfoCircleOutlined,MinusSquareOutlined} from "@ant-design/icons";
import styles from "./index.less";
import {useEffect} from "react";
import {JarStateType} from "@/pages/Jar/model";
import {Scrollbars} from "react-custom-scrollbars";

const StudioEnvSetting = (props: any) => {

  const { current, form, dispatch, tabs, toolHeight} = props;

  useEffect(() => {
    form.setFieldsValue(current.task);
  }, [current.task]);


  const onValuesChange = (change: any, all: any) => {
    const newTabs = tabs;
    for (let i = 0; i < newTabs.panes.length; i++) {
      if (newTabs.panes[i].key === newTabs.activeKey) {
        for (const key in change) {
          newTabs.panes[i].task[key] = all[key];
        }
        break;
      }
    }
    dispatch({
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
        <Form
          form={form}
          layout="vertical"
          className={styles.form_setting}
          onValuesChange={onValuesChange}
        >
          <Row>
            <Col span={12}>
              <Form.Item
                label="Fragment" className={styles.form_item} name="fragment" valuePropName="checked"
                tooltip={{title: '【增强特性】 开启FlinkSql片段机制，使用“:=”进行定义（以“;”结束），“${}”进行调用', icon: <InfoCircleOutlined/>}}
              >
                <Switch checkedChildren="启用" unCheckedChildren="禁用"
                />
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Scrollbars>
    </>
  );
};

export default connect(({Studio, Jar}: { Studio: StateType, Jar: JarStateType }) => ({
  current: Studio.current,
  tabs: Studio.tabs,
  toolHeight: Studio.toolHeight,
  jars: Jar.jars,
}))(StudioEnvSetting);

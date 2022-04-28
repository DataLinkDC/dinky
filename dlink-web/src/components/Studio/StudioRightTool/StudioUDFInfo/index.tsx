import {connect} from "umi";
import {StateType} from "@/pages/DataStudio/model";
import {Form, Switch, Row, Col, Tooltip, Button, Input} from "antd";
import {InfoCircleOutlined,MinusSquareOutlined} from "@ant-design/icons";
import styles from "./index.less";
import {useEffect} from "react";
import {JarStateType} from "@/pages/Jar/model";
import {Scrollbars} from "react-custom-scrollbars";

const StudioUDFInfo = (props: any) => {

  const { current, form, toolHeight} = props;

  useEffect(() => {
    form.setFieldsValue(current.task);
  }, [current.task]);

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
        >
          <Row>
            <Col span={24}>
              <Form.Item
                label="类名" className={styles.form_item} name="savePointPath"
              >
                <Input readOnly={true} placeholder="自动识别"/>
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
  toolHeight: Studio.toolHeight,
}))(StudioUDFInfo);

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


import {connect} from "umi";
import {StateType} from "@/pages/DataStudio/model";
import {Button, Col, Form, Input, Row, Tooltip} from "antd";
import {MinusSquareOutlined} from "@ant-design/icons";
import styles from "./index.less";
import {useEffect} from "react";
import {JarStateType} from "@/pages/RegistrationCenter/Jar/model";
import {Scrollbars} from "react-custom-scrollbars";

const StudioUDFInfo = (props: any) => {

  const {current, form, toolHeight} = props;

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

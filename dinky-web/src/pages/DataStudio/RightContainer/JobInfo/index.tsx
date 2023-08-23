/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

import {getCurrentData, getCurrentTab, isDataStudioTabsItemType} from '@/pages/DataStudio/function';
import { StateType, STUDIO_MODEL } from '@/pages/DataStudio/model';
import { l } from '@/utils/intl';
import { Col, Descriptions, Form, Row } from 'antd';
import { useForm } from 'antd/es/form/Form';
import TextArea from 'antd/es/input/TextArea';
import Paragraph from 'antd/es/typography/Paragraph';
import { connect } from 'umi';

const JobInfo = (props: any) => {
  const {
    dispatch,
    tabs: { panes, activeKey }
  } = props;
  const current = getCurrentData(panes, activeKey);

  const [form] = useForm();
  form.setFieldsValue(current);
  const onValuesChange = (change: any, all: any) => {
    const pane = getCurrentTab(panes, activeKey);
    if(!isDataStudioTabsItemType(pane)){
      return
    }

    Object.keys(change).forEach((key) => {
      pane.params.taskData[key] = change[key];
    });

    dispatch({
      type: STUDIO_MODEL.saveTabs,
      payload: { ...props.tabs }
    });
  };

  return (
    <div style={{ paddingInline: 8 }}>
      <Descriptions bordered size='small' column={1}>
        <Descriptions.Item label={l('pages.datastudio.label.jobInfo.id')}>
          <Paragraph copyable>{current?.id}</Paragraph>
        </Descriptions.Item>
        <Descriptions.Item label={l('pages.datastudio.label.jobInfo.name')}>
          {current?.name}
        </Descriptions.Item>
        <Descriptions.Item label={l('pages.datastudio.label.jobInfo.dialect')}>
          {current?.dialect}
        </Descriptions.Item>
        <Descriptions.Item label={l('pages.datastudio.label.jobInfo.versionId')}>
          {current?.versionId}
        </Descriptions.Item>
        <Descriptions.Item label={l('global.table.createTime')}>
          {current?.createTime}
        </Descriptions.Item>
        <Descriptions.Item label={l('global.table.updateTime')}>
          {current?.updateTime}
        </Descriptions.Item>
      </Descriptions>
      <Form
        layout='vertical'
        form={form}
        // className={styles.form_setting}
        onValuesChange={onValuesChange}
      >
        <Row>
          <Col span={24}>
            <Form.Item label={l('global.table.note')} name='note'>
              <TextArea rows={4} maxLength={255} />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </div>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  tabs: Studio.tabs
}))(JobInfo);

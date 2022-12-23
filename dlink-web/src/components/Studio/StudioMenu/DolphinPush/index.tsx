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

import type {FormInstance} from 'antd/es/form';
import {Button, Checkbox, Col, Form, InputNumber, message, Row, Select, Switch} from "antd";
import {StateType} from "@/pages/DataStudio/model";
import {connect} from "umi";
import React, {useEffect, useState} from "react";
import {createTaskDefinition, getTaskMainInfos, updateTaskDefinition} from "@/pages/DataStudio/service";
import {CODE} from "@/components/Common/crud";
import TextArea from "antd/es/input/TextArea";
import {l} from "@/utils/intl";

const DolphinPush = (props: any) => {


  const {data, taskCur, handleDolphinModalVisible} = props;

  const [options, setOptions] = useState([]);
  const formRef = React.createRef<FormInstance>();

  const [processCode, setProcessCode] = useState("");

  const CheckboxGroup = Checkbox.Group;

  const layout = {
    labelCol: {span: 6},
    wrapperCol: {span: 18},
  };
  useEffect(() => {
    options.length = 0;
    taskMainInfos();
    setFormValue();
  }, [taskCur])

  //前置任务数据集合
  const taskMainInfos = () => {
    if (taskCur) {
      const res = getTaskMainInfos(taskCur.task.id);
      res.then((result) => {
        if (result.code == CODE.SUCCESS) {
          setOptions(result.datas.map((item: { taskName: any; taskCode: any; }) => ({
              label: item.taskName,
              value: item.taskCode
            })
          ))
        } else {
          message.error(l('pages.datastudio.editor.push.ds.query.error','',{msg: result.msg}));
        }
      })
    }
  };

  //赋值数据
  const setFormValue = () => {
    //是否已有数据
    if (data) {
      // debugger
      setProcessCode(data.processDefinitionCode)

      setTimeoutFlagHidden(data.timeoutFlag === 'OPEN');

      let tns = []
      if (data.timeoutNotifyStrategy === "WARNFAILED") {
        tns = ['WARN', 'FAILED']
      } else {
        // debugger
        tns = data.timeoutNotifyStrategy ? data.timeoutNotifyStrategy.split(',') : []
      }
      // debugger
      //前置任务勾选
      let upstreamCodesTem = [];
      for (let key in data.upstreamTaskMap) {
        upstreamCodesTem.push(parseInt(key))
      }

      formRef.current.setFieldsValue({
        upstreamCodes: upstreamCodesTem,
        taskPriority: data.taskPriority,
        failRetryTimes: data.failRetryTimes,
        description: data.description,
        failRetryInterval: data.failRetryInterval,
        delayTime: data.delayTime,
        timeout: data.timeout,
        flag: data.flag === 'YES',
        timeoutFlag: data.timeoutFlag === 'OPEN',
        timeoutNotifyStrategy: tns
      });

    } else {
      formRef.current.setFieldsValue({
        flag: true,
        taskPriority: 'MEDIUM',
        timeoutFlag: false,
      });
    }
  };

  //表单提交，添加/更新海豚任务
  const onFinish = (values: any) => {
    // console.log(values);
    values.flag === true ? values.flag = 'YES' : values.flag = 'NO';
    values.upstreamCodes ? values.upstreamCodes = values.upstreamCodes.toString() : "";
    values.processCode = processCode;

    if (values.timeoutFlag === false) {
      values.timeoutFlag = 'CLOSE'
      values.timeoutNotifyStrategy = null;
      values.timeout = 0;
    } else {
      values.timeoutFlag = 'OPEN'
      values.timeout = 1;
      if (values.timeoutNotifyStrategy && values.timeoutNotifyStrategy.length > 1) {
        values.timeoutNotifyStrategy = "WARNFAILED";
      } else if (values.timeoutNotifyStrategy && values.timeoutNotifyStrategy.length === 1) {
        values.timeoutNotifyStrategy = values.timeoutNotifyStrategy[0]
      } else {
        message.error(l('pages.datastudio.editor.push.ds.timeout.strategy'));
        return
      }
    }

    if (!data) {
      const res = createTaskDefinition(taskCur.task.id, values.upstreamCodes, values);
      res.then((result) => {
        if (result.code == CODE.SUCCESS) {
          handleDolphinModalVisible(false);
        } else {
          message.error(l('pages.datastudio.editor.push.ds.add.job.error','',{msg:result.msg}));
        }
      })
    } else {
      const res = updateTaskDefinition(data.processDefinitionCode, data.projectCode, data.code, values.upstreamCodes, values);
      res.then((result) => {
        if (result.code == CODE.SUCCESS) {
          handleDolphinModalVisible(false);
        } else {
          message.error(l('pages.datastudio.editor.push.ds.add.job.error','',{msg:result.msg}));
        }
      })
    }
  };

  const [timeoutFlagHidden, setTimeoutFlagHidden] = useState<boolean>(false);

  function onSwitchChange(checked: boolean) {
    setTimeoutFlagHidden(checked);
    formRef.current.setFieldsValue({
      timeout: 1
    });
  }

  return (
    <Form {...layout} ref={formRef} name="control-hooks" onFinish={onFinish}>
      <Form.Item name={['upstreamCodes']} style={{marginBottom: 10}} label={l('pages.datastudio.editor.push.ds.prejob')}>
        <Select mode='multiple' style={{width: '100%'}} options={options} placeholder={l('pages.datastudio.editor.push.ds.prejob.tip')}
                maxTagCount='responsive'/>
      </Form.Item>

      <Form.Item name={['taskPriority']} style={{marginBottom: 10}} label={l('pages.datastudio.editor.push.ds.job.priority')}>
        <Select style={{width: 180}}>
          <Option value="HIGH">HIGH</Option>
          <Option value="HIGHEST">HIGHEST</Option>
          <Option value="LOW">LOW</Option>
          <Option value="LOWEST">LOWEST</Option>
          <Option value="MEDIUM">MEDIUM</Option>
        </Select>
      </Form.Item>

      <Form.Item name={['failRetryTimes']} style={{marginBottom: 10}} label={l('pages.datastudio.editor.push.ds.errortry.count')}>
        <InputNumber min={0} max={99} style={{width: 180}}/>
      </Form.Item>
      <Form.Item name={['failRetryInterval']} style={{marginBottom: 10}} label={l('pages.datastudio.editor.push.ds.errortry.min')}>
        <InputNumber min={0} style={{width: 180}}/>
      </Form.Item>
      <Form.Item name={['delayTime']} style={{marginBottom: 10}} label={l('pages.datastudio.editor.push.ds.delayed.time')}>
        <InputNumber min={0} style={{width: 180}}/>
      </Form.Item>
      <Form.Item name={['timeoutFlag']} style={{marginBottom: 10}} label={l('pages.datastudio.editor.push.ds.timeout.alarm')} valuePropName="checked">
        <Switch checkedChildren="OPEN" unCheckedChildren="CLOSE" onChange={onSwitchChange}/>
      </Form.Item>
      <Form.Item name={['timeoutNotifyStrategy']} style={{marginBottom: 10}} hidden={!timeoutFlagHidden}
                 label={l('pages.datastudio.editor.push.ds.timeout.strategy')}>
        <CheckboxGroup>
          <Row>
            <Col span={12}>
              <Checkbox value="WARN">{l('pages.datastudio.editor.push.ds.timeout.alarm')}</Checkbox>
            </Col>
            <Col span={12}>
              <Checkbox value="FAILED">{l('pages.datastudio.editor.push.ds.timeout.error')}</Checkbox>
            </Col>
          </Row>
        </CheckboxGroup>
      </Form.Item>
      <Form.Item name={['timeout']} style={{marginBottom: 10}} hidden={!timeoutFlagHidden} label={l('pages.datastudio.editor.push.ds.timeout.min')}>
        <InputNumber min={1} value={30} style={{width: 180}}/>
      </Form.Item>
      <Form.Item name={['flag']} style={{marginBottom: 10}} label={l('pages.datastudio.editor.push.ds.running.tag')} valuePropName="checked">
        <Switch checkedChildren="YES" unCheckedChildren="NO"/>
      </Form.Item>
      <Form.Item name={['description']} style={{marginBottom: 10}} label={l('global.table.note')}>
        <TextArea rows={3} placeholder={l('global.table.note')} maxLength={250}/>
      </Form.Item>
      <Form.Item wrapperCol={{offset: 8, span: 16}}>
        <Button type="primary" htmlType="submit">
          {l('button.save')}
        </Button>
      </Form.Item>
    </Form>
  );
}


export default connect(({Studio}: { Studio: StateType }) => ({
  current: Studio.current,
  currentSession: Studio.currentSession,
}))(DolphinPush);


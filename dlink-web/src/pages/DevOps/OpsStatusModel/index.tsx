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

import React, {useEffect, useRef, useState} from 'react'
import {Button, Card, Checkbox, Col, Divider, Form, Modal, Row, Select, TreeSelect} from 'antd'
import {queryOnClickOperatingTask} from '../service'
import type {CheckboxChangeEvent} from 'antd/es/checkbox';
import type {CheckboxValueType} from 'antd/es/checkbox/Group';
import {l} from "@/utils/intl";

interface IOpsStatusModalProps {
  opsStatusVisible: boolean;
  opsStatus: string;
  opsStatusListTree: any;
  onOpsStatusCallBack: (values?: any) => void
}

export const OpsStatusLabel = {
  '1': '上线',
  '0': '下线'
}

const {Option} = Select
const CheckboxGroup = Checkbox.Group;

const OpsStatusModal: React.FC<IOpsStatusModalProps> = (props): React.ReactElement => {

  const {opsStatusVisible, opsStatus, opsStatusListTree, onOpsStatusCallBack} = props
  const formRef = useRef<any>(null)
  const [opsStatusList, setOpsStatusList] = useState<any[]>([])
  const [treeValue, setTreeValue] = useState<number | null>(null);

  useEffect(() => {
    setTreeValue(null)
    setOpsStatusList([])
    formRef.current?.resetFields(['tasks', 'checkoutAll'])
    console.log(opsStatus)
    if (opsStatus === '1') {
      formRef.current?.setFieldsValue({
        taskOperatingSavepointSelect: '0'
      })
    }
  }, [opsStatusVisible, opsStatus])

  /**
   * onCheckAllChange 全选
   * */
  const onCheckAllChange = (e: CheckboxChangeEvent) => {
    formRef.current?.setFieldsValue({
      tasks: e.target.checked ? opsStatusList.map((item: any) => item.id) : []
    })
  };

  /**
   * onChangeCheckout 选中
   * */
  const onChangeCheckout = (list: CheckboxValueType[]) => {
    formRef.current?.setFieldsValue({
      checkoutAll: list.length === opsStatusList.length ? true : false
    })
  };

  /**
   * onTreeChange 结构树选中
   * */
  const onTreeChange = (newValue: number) => {
    setTreeValue(newValue);

  };

  /**
   * onSubmit 提交
   * */
  const onSubmit = () => {
    formRef.current.validateFields(['tasks', 'taskOperatingSavepointSelect']).then((values: any) => {
      const {tasks, ...rest} = values
      onOpsStatusCallBack({
        ...rest,
        operating: opsStatus,
        tasks: tasks.map((item: any) => {
          return {
            id: item,
            name: opsStatusList.find((items: any) => items.id === item).name
          }
        })
      })
    })
  }

  /**
   * onSubmitTree 树型选择提交
   * */
  const onSubmitTree = async () => {
    try {
      const {datas} = await queryOnClickOperatingTask({
        operating: opsStatus,
        catalogueId: treeValue
      })
      setOpsStatusList(datas)
    } catch (e) {
      console.log(e)
    }
  }

  // options = {opsStatusList}
  // value = {checkedList}
  // onChange = {onChange}
  return (
    <Modal width={800} okText={l('button.submit')} onCancel={() => {
      onOpsStatusCallBack()
    }} onOk={() => {
      onSubmit()
    }} title={OpsStatusLabel[opsStatus]} visible={opsStatusVisible}>
      <Form ref={formRef}>
        <Card>
          <Row>
            <Col span={8}>
              <TreeSelect
                showSearch
                allowClear
                treeDataSimpleMode
                style={{width: '100%'}}
                dropdownStyle={{maxHeight: 400, overflow: 'auto'}}
                fieldNames={{label: 'name', value: 'id'}}
                treeNodeFilterProp={'name'}
                placeholder="请选择"
                onChange={onTreeChange}
                value={treeValue}
                treeData={opsStatusListTree}
              />
            </Col>
            <Col push={2}>
              <Button type={'primary'} onClick={() => {
                onSubmitTree()
              }}>查询</Button>
            </Col>
          </Row>
          <Divider/>
          <Row>
            <Col>
              <Form.Item name={'checkoutAll'} label={'全选'}>
                <Checkbox onChange={onCheckAllChange}/>
              </Form.Item>
            </Col>
            <Col push={15}>
              {
                opsStatus === '1' &&
                <Form.Item name={'taskOperatingSavepointSelect'} label={' '} colon={false}
                           rules={[{message: '请输入', required: true}]}>
                  <Select style={{width: '150px'}}>
                    <Option value={'0'}>默认保存点</Option>
                    <Option value={'1'}>最新保存点</Option>
                  </Select>
                </Form.Item>}
            </Col>
          </Row>
          <Divider/>
          <Form.Item name={'tasks'}>
            <CheckboxGroup onChange={onChangeCheckout}>
              <Row>
                {
                  opsStatusList.map((item: any) => {
                    return <Col key={item.id} span={8}>
                      <Checkbox value={item.id}>{item.name}</Checkbox>
                    </Col>
                  })
                }
              </Row>
            </CheckboxGroup>
          </Form.Item>

        </Card>
      </Form>
    </Modal>
  )
}

export default OpsStatusModal

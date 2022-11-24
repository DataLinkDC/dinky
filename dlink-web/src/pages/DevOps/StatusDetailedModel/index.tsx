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

import React from 'react';
import {Button, Modal, Table, Tooltip} from 'antd'
import {l} from "@/utils/intl";

interface IStatusDetailedModal {
  statusDetailedVisible: boolean;
  statusDetailedList: any;
  onCancelStatusDetailed: () => void
  opsStatus: string;
}

export const OpsStatusTitle = {
  '1': '上线明细列表',
  '0': '下线明细列表'
}


const StatusDetailedModal: React.FC<IStatusDetailedModal> = (props): React.ReactElement => {
  const {statusDetailedVisible, statusDetailedList, opsStatus, onCancelStatusDetailed} = props

  const columns = [{
    title: '名称',
    dataIndex: 'name'
  }, {
    title: '状态',
    dataIndex: 'status'
  }, {
    title: '结果',
    dataIndex: 'code'
  }, {
    title: '信息',
    dataIndex: 'message',
    render: (text: string) => <Tooltip overlayInnerStyle={{width: '800px'}} placement="bottom" title={text}>
      <div style={{width: '150px', overflow: 'hidden', whiteSpace: 'nowrap', textOverflow: 'ellipsis'}}>{text}</div>
    </Tooltip>,
    width: 150
  }, {
    title: '点位配置选择',
    dataIndex: 'taskOperatingSavepointSelect',
  }].filter((item) => {
    if (item.dataIndex === 'taskOperatingSavepointSelect') {
      if (opsStatus === '1') {
        return true
      } else {
        return false
      }
    } else {
      return true
    }
  })

  const onFooter = () => <Button onClick={() => onCancelStatusDetailed()}>
    {l('button.back')}
  </Button>

  return (
    <Modal title={OpsStatusTitle[opsStatus]} width={800} onCancel={() => onCancelStatusDetailed()} footer={onFooter()}
           visible={statusDetailedVisible}>
      <Table rowKey={'id'} dataSource={statusDetailedList} columns={columns}/>
    </Modal>)
}

export default StatusDetailedModal

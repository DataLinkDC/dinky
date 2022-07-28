import React from 'react';
import {Modal, Table, Button, Tooltip} from 'antd'

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
    title: '是否选择最新点位',
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
    返回
  </Button>

  return (
    <Modal title={OpsStatusTitle[opsStatus]} width={800} onCancel={() => onCancelStatusDetailed()} footer={onFooter()}
           visible={statusDetailedVisible}>
      <Table rowKey={'id'} dataSource={statusDetailedList} columns={columns}/>
    </Modal>)
}

export default StatusDetailedModal

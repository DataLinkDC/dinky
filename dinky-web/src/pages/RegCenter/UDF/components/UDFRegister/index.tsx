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

import {EditableProTable, ProColumns, ProForm} from "@ant-design/pro-components";
import {UDFRegisterInfo} from "@/types/RegCenter/data";
import {EditBtn} from "@/components/CallBackButton/EditBtn";
import {PopconfirmDeleteBtn} from "@/components/CallBackButton/PopconfirmDeleteBtn";
import {Key, useRef, useState} from "react";
import {ActionType} from "@ant-design/pro-table";
import {ProFormInstance} from "@ant-design/pro-form/lib";


const intidata: UDFRegisterInfo[] = [{
  id: 1,
  name: 'test',
  className: 'test',
  enable: true,
  dialect: 'JAVA',
  source: 'test',
  fileName: 'test.java',
  updateTime: new Date()
}]

const UDFRegister = () => {
  const formRef = useRef<ProFormInstance<UDFRegisterInfo>>();
  const actionRef = useRef<ActionType>();
  const [udfRegisterState, setUDFRegisterState] = useState<{
    editableKeys: Key[];
    isEdit: boolean;
    isAdd: boolean;
    dataSource: UDFRegisterInfo[];
  }>({
    editableKeys: [],
    dataSource: [],
    isEdit: false,
    isAdd: false,
  });

  const editableKeysChange = (editableKeys: Key[]) => {
    setUDFRegisterState(prevState => ({ ...prevState, editableKeys }));
  }


  const columns: ProColumns<UDFRegisterInfo>[] = [
    {
      title: '名称',
      dataIndex: 'name',
      width: '10%',
      // readonly: udfRegisterState.isEdit && !udfRegisterState.isAdd,
      formItemProps: {
        rules: [
          {
            required: true,
            message: '请输入名称',
          },
        ],
      },
    },
    {
      title: '类名',
      dataIndex: 'className',
      // readonly: !udfRegisterState.isEdit && udfRegisterState.isAdd,
      width: '15%',
    },
    {
      title: '语言',
      dataIndex: 'dialect',
      // readonly: !udfRegisterState.isEdit && udfRegisterState.isAdd,
      width: '8%',
    },
    {
      title: '来源',
      dataIndex: 'source',
      // readonly: !udfRegisterState.isEdit && udfRegisterState.isAdd,
      width: '10%',
    },
    {
      title: '文件名',
      dataIndex: 'fileName',
      // readonly: !udfRegisterState.isEdit && udfRegisterState.isAdd,
      width: '20%',
    },
    {
      title: '更新时间',
      dataIndex: 'updateTime',
      // readonly: !udfRegisterState.isEdit && udfRegisterState.isAdd,
      valueType: 'dateTime',
      width: '15%',
    },
    {
      title: '操作',
      valueType: 'option',
      width: '10%',
      render: (text, record, _, action) => {
        return [
         <EditBtn key={`${record.id}_edit`}  onClick={() => {
           action?.startEditable?.(record.id);
           setUDFRegisterState(prevState => ({ ...prevState, isEdit: true,isAdd: false }));
         }} />,
          <PopconfirmDeleteBtn key={`${record.id}_delete`} onClick={() => {}}  description={"确定删除吗???"}/>,
        ];
      },
    }
  ]


  return (
    <>

      <EditableProTable<UDFRegisterInfo>
        rowKey="id"
        headerTitle={false}
        maxLength={5}
        pagination={{
          showQuickJumper: true,
          pageSize: 5,
        }}
        controlled
        recordCreatorProps={{
          position: 'top',
          newRecordType: 'cache',
          creatorButtonText: '新增',
          record: ((index: number, dataSource: UDFRegisterInfo[]) => {
            // setUDFRegisterState(prevState => ({ ...prevState, isAdd: true, isEdit: false }));
            return dataSource[index]
          }),
        }}
        actionRef={actionRef}
        editable={{
          type: 'single',
          editableKeys: udfRegisterState.editableKeys,
          onChange: editableKeysChange,
        }}
        columns={columns}
        request={async () => ({
          data: intidata,
          total: 3,
          success: true,
        })}
        value={intidata}
        // onChange={setDataSource}

      />
    </>
  )
}

export default UDFRegister

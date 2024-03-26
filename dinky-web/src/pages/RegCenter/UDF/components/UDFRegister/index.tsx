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

import { EditBtn } from '@/components/CallBackButton/EditBtn';
import { PopconfirmDeleteBtn } from '@/components/CallBackButton/PopconfirmDeleteBtn';
import { BackIcon } from '@/components/Icons/CustomIcons';
import UDFRegisterModal from '@/pages/RegCenter/UDF/components/UDFRegister/UDFRegisterModal';
import { API_CONSTANTS } from '@/services/endpoints';
import { UDFRegisterInfo, UDFRegisterInfoParent } from '@/types/RegCenter/data';
import { l } from '@/utils/intl';
import { useRequest } from '@@/plugin-request';
import { SaveTwoTone } from '@ant-design/icons';
import { ProColumns } from '@ant-design/pro-components';
import ProTable, { ActionType } from '@ant-design/pro-table';
import React, { Key, useEffect, useRef, useState } from 'react';
import { add, update } from './service';

type UDFRegisterProps = {
  showEdit: boolean;
  showEditChange: (showEdit: boolean) => void;
};

const UDFRegister: React.FC<UDFRegisterProps> = (props) => {
  const { showEdit, showEditChange } = props;

  const udfRegisterInfoRequest = useRequest<{
    data: UDFRegisterInfo[];
  }>({
    url: API_CONSTANTS.UDF_LIST
  });

  const resourceInfoRequest = useRequest({
    url: API_CONSTANTS.UDF_RESOURCES_LIST
  });
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
    isAdd: false
  });

  const [targetKeys, setTargetKeys] = useState<Key[]>([]);
  useEffect(() => {
    setTargetKeys([...new Set(udfRegisterInfoRequest.data?.map((x) => x.resourcesId))]);
  }, [udfRegisterInfoRequest.data]);
  const editableKeysChange = (editableKeys: Key[]) => {
    setUDFRegisterState((prevState) => ({ ...prevState, editableKeys }));
  };

  const groupData: {
    [p: string]: UDFRegisterInfo[] | undefined;
  } = Object.fromEntries(
    Array.from(new Set(udfRegisterInfoRequest.data?.map(({ fileName }) => fileName))).map(
      (type) => [type, udfRegisterInfoRequest.data?.filter((item) => item.fileName === type)]
    )
  );
  const parentData: UDFRegisterInfoParent[] = Object.keys(groupData)?.map((key) => {
    const d = groupData[key] ?? [];
    return {
      resourcesId: d[0].resourcesId,
      dialect: d[0].dialect,
      source: d[0].source,
      fileName: key,
      num: d.length
    };
  });

  const columnsParent: ProColumns<UDFRegisterInfoParent>[] = [
    {
      title: l('rc.udf.register.file.name'),
      width: '25%',
      dataIndex: 'fileName'
    },
    {
      title: l('rc.udf.register.parse.count'),
      width: '25%',
      sorter: true,
      dataIndex: 'num'
    },
    {
      title: l('rc.udf.register.source'),
      width: '25%',
      dataIndex: 'source',
      valueEnum: {
        resources: { text: 'resources' },
        develop: { text: 'develop' }
      }
    },
    {
      title: l('rc.udf.register.language'),
      width: '25%',
      dataIndex: 'dialect',
      valueEnum: {
        java: { text: 'Java' },
        python: { text: 'Python' }
      }
    }
  ];
  const expandedRowRender = (expandedRow: UDFRegisterInfoParent) => {
    const columns: ProColumns<UDFRegisterInfo>[] = [
      {
        title: l('rc.udf.register.name'),
        dataIndex: 'name',
        width: '10%'
      },
      {
        title: l('rc.udf.register.className'),
        dataIndex: 'className',
        readonly: true,
        width: '15%'
      },
      {
        title: l('global.table.updateTime'),
        dataIndex: 'updateTime',
        readonly: true,
        valueType: 'dateTime',
        width: '15%'
      },
      {
        title: l('global.table.operate'),
        valueType: 'option',
        width: '10%',
        render: (_text, record, _, action) => {
          return [
            <EditBtn
              key={`${record.id}_edit`}
              onClick={() => {
                action?.startEditable?.(record.id);
                setUDFRegisterState((prevState) => ({ ...prevState, isEdit: true, isAdd: false }));
              }}
            />,
            record.source == 'develop' ? (
              <PopconfirmDeleteBtn
                key={`${record.id}_delete`}
                onClick={() => {}}
                description={l('rc.udf.register.deleteConfirm')}
              />
            ) : (
              <></>
            )
          ];
        }
      }
    ];

    const handleOnSave = async (row: UDFRegisterInfo) => {
      await update(row.id, row.name);
      await udfRegisterInfoRequest.refresh();
      actionRef.current?.reload();
    };

    return (
      <ProTable
        rowKey={'id'}
        columns={columns}
        search={false}
        options={false}
        dataSource={groupData[expandedRow.fileName]}
        pagination={false}
        actionRef={actionRef}
        editable={{
          deleteText: false,
          type: 'single',
          saveText: <SaveTwoTone title={l('button.save')} />,
          cancelText: <BackIcon title={l('button.back')} />,
          editableKeys: udfRegisterState.editableKeys,
          onChange: editableKeysChange,
          onSave: async (_, row) => handleOnSave(row),
          actionRender: (_, _2, defaultDom) => [defaultDom.save, defaultDom.cancel]
        }}
      />
    );
  };

  /**
   * submit register udf
   */
  const handleUdfRefresh = () => {
    add(targetKeys).then(() => {
      udfRegisterInfoRequest.refresh().then(() => {
        showEditChange(false);
      });
    });
  };

  return (
    <>
      <ProTable<UDFRegisterInfoParent>
        dataSource={parentData}
        columns={columnsParent}
        rowKey='resourcesId'
        size={'small'}
        pagination={{
          hideOnSinglePage: true,
          showQuickJumper: true
        }}
        expandable={{ expandedRowRender }}
        search={false}
        dateFormatter='string'
        options={false}
      />

      <UDFRegisterModal
        showEdit={showEdit}
        openChange={showEditChange}
        onOk={() => handleUdfRefresh()}
        targetKeys={targetKeys}
        targetKeyChange={setTargetKeys}
        treeData={resourceInfoRequest.data ?? []}
      />
    </>
  );
};

export default UDFRegister;

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

import { TreeTransfer } from '@/components/Transfer/TreeTransfer';
import { buildResourceTreeData } from '@/pages/RegCenter/Resource/components/FileTree/function';
import { l } from '@/utils/intl';
import { Modal } from 'antd';
import { TransferDirection } from 'antd/es/transfer';
import React, { Key, memo } from 'react';

type UDFRegisterModalProps = {
  showEdit: boolean;
  openChange: (showEdit: boolean) => void;
  onOk: () => void;
  targetKeys: Key[];
  targetKeyChange: (targetKeys: Key[], direction: TransferDirection, moveKeys: string[]) => void;
  treeData: any[];
};

const UDFRegisterModal: React.FC<UDFRegisterModalProps> = (props) => {
  const { showEdit, openChange, treeData, targetKeys, targetKeyChange, onOk } = props;

  return (
    <>
      <Modal
        width={'70%'}
        bodyStyle={{ height: 600, overflow: 'auto' }}
        open={showEdit}
        title={l('rc.udf.register')}
        destroyOnClose
        closable
        maskClosable={false}
        okButtonProps={{ htmlType: 'submit', autoFocus: true }}
        onCancel={() => openChange(false)}
        onOk={() => onOk()}
      >
        <TreeTransfer
          dataSource={buildResourceTreeData(treeData, true, ['jar', 'zip'])}
          targetKeys={targetKeys}
          onChange={targetKeyChange}
        />
      </Modal>
    </>
  );
};

export default memo(UDFRegisterModal);

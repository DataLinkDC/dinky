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

import TenantTransfer from '@/pages/AuthCenter/Tenant/components/TenantModalTransfer/TenantTransfer';
import { UserBaseInfo } from '@/types/AuthCenter/data';
import { l } from '@/utils/intl';
import { Modal } from 'antd';
import React, { useState } from 'react';

type TableTransferFromProps = {
  tenant: Partial<UserBaseInfo.Tenant>;
  onChange: (values: string[]) => void;
  onSubmit: (values: string[]) => void;
  onCancel: () => void;
  modalVisible: boolean;
};

const TenantModalTransfer: React.FC<TableTransferFromProps> = (props) => {
  const { tenant, modalVisible, onCancel, onSubmit: handleSubmit, onChange: handleChange } = props;
  const [targetKeys, setTargetKeys] = useState<string[]>([]);

  const handleValueChange = (value: string[]) => {
    handleChange(value);
    setTargetKeys(value);
  };

  return (
    <Modal
      title={l('tenant.AssignUser')}
      open={modalVisible}
      destroyOnClose
      maskClosable={false}
      width={'75%'}
      onCancel={() => onCancel()}
      okButtonProps={{
        htmlType: 'submit',
        autoFocus: true
      }}
      onOk={() => handleSubmit(targetKeys)}
    >
      <TenantTransfer tenant={tenant} onChange={(value) => handleValueChange(value)} />
    </Modal>
  );
};
export default TenantModalTransfer;

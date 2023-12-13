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

import TenantCard from '@/pages/Other/Login/ChooseModal/TenantCard';
import { NORMAL_MODAL_OPTIONS } from '@/services/constants';
import { UserBaseInfo } from '@/types/AuthCenter/data';
import { setTenantStorageAndCookie } from '@/utils/function';
import { l } from '@/utils/intl';
import { CheckGroupValueType } from '@ant-design/pro-card/es/components/CheckCard/Group';
import { Modal } from 'antd';
import React, { useState } from 'react';

type ChooseModalProps = {
  tenantVisible: boolean;
  handleTenantVisible: () => void;
  submitting: boolean;
  handleChooseTenant: (tenantId: number) => void;
  tenant: UserBaseInfo.Tenant[];
};

const ChooseModal: React.FC<ChooseModalProps> = (props) => {
  const { tenantVisible, handleTenantVisible, submitting, tenant, handleChooseTenant } = props;
  const [check, setChecked] = useState<boolean>(true);
  const [checkedTenantId, setCheckedTenantId] = useState<any>();

  const tenantChange = (value: CheckGroupValueType) => {
    if (value) {
      //  if no tenant is selected, the confirm button is disabled
      setChecked(false);
      setCheckedTenantId(value);
      setTenantStorageAndCookie(value as number);
      // setTenantIdParams(value as number);
    } else {
      setChecked(true);
    }
  };

  return (
    <Modal
      title={l('login.chooseTenant')}
      open={tenantVisible}
      {...NORMAL_MODAL_OPTIONS}
      onCancel={handleTenantVisible}
      onOk={() => handleChooseTenant(checkedTenantId)}
      okText={l('button.confirm')}
      okButtonProps={{
        disabled: check,
        htmlType: 'submit',
        autoFocus: true,
        loading: submitting
      }}
    >
      <TenantCard tenant={tenant} handleChangeTenant={tenantChange} />
    </Modal>
  );
};

export default ChooseModal;

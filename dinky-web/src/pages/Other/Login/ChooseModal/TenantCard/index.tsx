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

import { UserBaseInfo } from '@/types/AuthCenter/data';
import { CheckGroupValueType } from '@ant-design/pro-card/es/components/CheckCard/Group';
import { CheckCard } from '@ant-design/pro-components';
import React from 'react';

type TenantCardProps = {
  tenant: UserBaseInfo.Tenant[];
  handleChangeTenant: (value: CheckGroupValueType) => void;
};

const TenantCard: React.FC<TenantCardProps> = (props) => {
  const { tenant, handleChangeTenant } = props;

  const chooseCard = () => {
    let cardList: any[] = [];
    //  if no tenant is selected, the confirm button is disabled
    if (tenant) {
      cardList = tenant.map((item: any) => {
        return (
          <CheckCard
            size={'default'}
            key={item.id}
            avatar='/icons/tenant_default.svg'
            title={item.tenantCode}
            value={item.id}
            description={item.note}
          />
        );
      });
    }
    return cardList;
  };

  return (
    <>
      <CheckCard.Group multiple={false} onChange={handleChangeTenant}>
        {chooseCard()}
      </CheckCard.Group>
    </>
  );
};

export default TenantCard;

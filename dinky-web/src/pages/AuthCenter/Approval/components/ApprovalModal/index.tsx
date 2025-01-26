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

import { ModalForm, ProFormSelect, ProFormTextArea } from '@ant-design/pro-components';
import React from 'react';
import { OperationType } from '@/types/AuthCenter/data.d';
import { l } from '@/utils/intl';
import { API_CONSTANTS } from '@/services/endpoints';
import { getTenantByLocalStorage } from '@/utils/function';
import { getData } from '@/services/api';

type ApprovalModelProps = {
  open: boolean;
  title: string;
  activeId: number;
  operationType: OperationType;
  onOpenChange: (open: boolean) => void;
  handleSubmit: (record) => void;
};

const ApprovalModal: React.FC<ApprovalModelProps> = (props) => {
  const getReviewerList = async () => {
    const reviewers = (
      await getData(API_CONSTANTS.GET_REVIEWERS, { tenantId: getTenantByLocalStorage() })
    ).data;
    return reviewers.map((t) => ({ label: t.username, value: t.id }));
  };

  const approvalRender = () => {
    if (props.operationType == OperationType.SUBMIT) {
      return (
        <>
          <ProFormSelect
            name='reviewer'
            label={l('approval.reviewerName')}
            request={async () => getReviewerList()}
            placeholder={l('approval.reviewer.required')}
            rules={[{ required: true }]}
          />
          <ProFormTextArea name='comment' label={l('approval.submit.comment')} />
        </>
      );
    } else {
      return (
        <>
          <ProFormTextArea name='comment' label={l('approval.review.comment')} />
        </>
      );
    }
  };
  return (
    <ModalForm
      open={props.open}
      onOpenChange={props.onOpenChange}
      modalProps={{ okText: props.title }}
      onFinish={async (record) => {
        record.id = props.activeId;
        await props.handleSubmit(record);
      }}
    >
      {approvalRender()}
    </ModalForm>
  );
};

export default ApprovalModal;

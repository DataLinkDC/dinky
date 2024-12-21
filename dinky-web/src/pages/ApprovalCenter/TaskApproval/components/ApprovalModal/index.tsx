import {ModalForm, ProFormSelect, ProFormTextArea} from "@ant-design/pro-components";
import React, {useRef, useState} from "react";
import {ApprovalOperationInfo, OperationType} from "@/types/ApprovalCenter/data.d";
import {l} from "@/utils/intl";
import {API_CONSTANTS} from "@/services/endpoints";
import {getValueFromLocalStorage} from "@/utils/function";
import {TENANT_ID} from "@/services/constants";
import {getData} from "@/services/api";
import {handleOption} from "@/services/BusinessCrud";
import {ActionType} from "@ant-design/pro-table";

type ApprovalModelProps = {
  open: boolean,
  title: string,
  activeId: number,
  operationType: OperationType,
  onOpenChange: (open: boolean) => void;
  handleSubmit: (record) => void;
};

const ApprovalModal: React.FC<ApprovalModelProps> = (props) => {

  const getReviewerList = async () => {
    const reviewers = (await getData(API_CONSTANTS.GET_REVIEWERS, {tenantId: getValueFromLocalStorage(TENANT_ID)})).data;
    return reviewers.map((t) => ({label: t.username, value: t.id}));
  }

  const approvalRender = () => {
    if (props.operationType == OperationType.SUBMIT) {
      return (
        <>
          <ProFormSelect
            name='reviewer'
            label={l('approval.reviewerName')}
            request={async () => getReviewerList()}
            placeholder={l('approval.reviewer.required')}
            rules={[{required: true}]}
          />
          <ProFormTextArea name='comment' label={l('approval.submit.comment')}/>
        </>
      )
    } else {
      return (
        <>
          <ProFormTextArea name='comment' label={l('approval.review.comment')}/>
        </>
      )
    }
  };
  return (
    <ModalForm
      open={props.open}
      onOpenChange={props.onOpenChange}
      modalProps={{okText: props.title}}
      onFinish={async (record) => {
        record.id = props.activeId
        await props.handleSubmit(record);
      }}
    >
      {approvalRender()}
    </ModalForm>
  );
};

export default ApprovalModal;

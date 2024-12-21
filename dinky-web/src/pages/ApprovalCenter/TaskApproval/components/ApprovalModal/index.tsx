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
  handleSubmit: () => void;
};

const ApprovalModal: React.FC<ApprovalModelProps> = (props) => {

  const [loading, setLoading] = useState<boolean>(false);
  const actionRef = useRef<ActionType>(); // table action

  const executeAndCallbackRefresh = async (callback: () => void) => {
    setLoading(true);
    await callback();
    setLoading(false);
    actionRef.current?.reload?.();
  };

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

  const submitForm = async (record: ApprovalOperationInfo) => {
    record.id = props.activeId;
    switch (props.operationType) {
      case OperationType.SUBMIT:
        await handleOption(API_CONSTANTS.APPROVAL_SUBMIT, l('approval.operation.submit'), record);
        break;
      case OperationType.APPROVE:
        await handleOption(API_CONSTANTS.APPROVAL_APPROVE, l('approval.operation.approve'), record);
        break;
      case OperationType.WITHDRAW:
        await handleOption(API_CONSTANTS.APPROVAL_WITHDRAW, l('approval.operation.withdraw'), record);
        break;
      case OperationType.REJECT:
        await handleOption(API_CONSTANTS.APPROVAL_REJECT, l('approval.operation.reject'), record);
        break;
    }
    props.onOpenChange(false);
  };

  return (
    <ModalForm
      open={props.open}
      onOpenChange={props.onOpenChange}
      modalProps={{okText: props.title}}
      onFinish={async (record) => {
        await submitForm(record);
      }}
      loading={loading}
    >
      {approvalRender()}
    </ModalForm>
  );
};

export default ApprovalModal;

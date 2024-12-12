import {ModalForm, ProFormSelect, ProFormTextArea} from "@ant-design/pro-components";
import React from "react";
import {ApprovalOperationInfo, OperationType} from "@/types/ApprovalCenter/data.d";
import {l} from "@/utils/intl";

type ApprovalModelProps = {
  open: boolean,
  title: string,
  activeId: number,
  operationType: OperationType,
  onOpenChange: (open: boolean) => void;
  onFinish: (operation: ApprovalOperationInfo, operationType: OperationType) => void;
};

const ApprovalModal: React.FC<ApprovalModelProps> = (props) => {

  const reviewer = {
    1: 'admin',
    2: 'reviewer'
  }

  const approvalRender = () => {
    if (props.operationType == OperationType.SUBMIT) {
      return (
        <>
          <ProFormSelect
            name='reviewer'
            label={l('approval.reviewerName')}
            valueEnum={reviewer}
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
    props.onFinish(record, props.operationType);
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
    >
      {approvalRender()}
    </ModalForm>
  );
};

export default ApprovalModal;

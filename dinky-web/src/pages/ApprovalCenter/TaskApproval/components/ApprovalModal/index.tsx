import {ModalForm} from "@ant-design/pro-components";
import React, {useEffect, useState} from "react";

type ApprovalModelProps = {
   viable: boolean
}

const ApprovalModal: React.FC<ApprovalModelProps> = (props) => {

  return (
    <ModalForm
      open={props.viable}
      // TODO 从父节点传处理方法进来 onOpenChange={setOpenState}
    >
      <h1>test</h1>
    </ModalForm>
  );
}

export default ApprovalModal;

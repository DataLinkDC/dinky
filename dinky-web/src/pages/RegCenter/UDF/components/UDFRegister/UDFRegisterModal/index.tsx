import {TreeTransfer} from "@/components/Transfer/TreeTransfer";
import {buildResourceTreeData} from "@/pages/RegCenter/Resource/components/FileTree/function";
import {Modal} from "antd";
import React, {Key, memo} from "react";
import {TransferDirection} from "antd/es/transfer";
import {l} from "@/utils/intl";


type UDFRegisterModalProps = {
  showEdit: boolean;
  openChange: (showEdit: boolean) => void;
  onOk: () => void;
  targetKeys: Key[];
  targetKeyChange: (targetKeys: Key[], direction: TransferDirection, moveKeys: string[]) => void;
  treeData: any[];

}

const UDFRegisterModal: React.FC<UDFRegisterModalProps> = (props) => {

  const {showEdit, openChange,treeData,targetKeys,targetKeyChange,onOk} = props;

  return <>
    <Modal
      width={'70%'}
      bodyStyle={{height: 600, overflow: 'auto'}}
      open={showEdit}
      title={l('rc.udf.register')}
      destroyOnClose
      closable
      maskClosable={false}
      onCancel={() => openChange(false)}
      onOk={() => onOk()}
    >
      <TreeTransfer
        dataSource={buildResourceTreeData(treeData, true,['jar','zip'])}
        targetKeys={targetKeys}
        onChange={targetKeyChange}
      />
    </Modal>
  </>
}

export default memo(UDFRegisterModal);

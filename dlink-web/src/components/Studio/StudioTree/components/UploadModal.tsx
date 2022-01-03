import React from 'react';
import {Button, message,Modal,Upload} from "antd";
import {UploadOutlined} from "@ant-design/icons";

interface UploadModalProps {
  visible: boolean;
  action: string;
  handleOk: ()=>void;
  onCancel: ()=>void;
  buttonTitle: string;
}

const UploadModal:React.FC<UploadModalProps> = (props:any) => {
  const {visible,handleOk,onCancel,action,buttonTitle} = props;
  const handlers = {
    name: 'file',
    action: action,
    maxCount: 1,
    multiple: true,
    onChange(info:any) {
      if (info.file.status !== 'uploading') {
        console.log(info.file, info.fileList);
      }
      if (info.file.status === 'done') {
        console.log('info:',info);
        if(info.file.response.code === 1){
          message.error(`${info.file.response.msg} `);
        }else{
          message.success(`${info.file.name} file uploaded successfully`);
        }
      } else if (info.file.status === 'error') {
        message.error(`${info.file.name} file upload failed.`);
      }
    },
  };
  return (
    <div>
      <Modal title="上传文件"
             visible={visible}
             onOk={handleOk}
             onCancel={onCancel}
             maskClosable={false}
      >
        <Upload {...handlers}>
          <Button size="small" icon={<UploadOutlined />}>{buttonTitle}</Button>
        </Upload>
      </Modal>
    </div>
  );
};

export default UploadModal;

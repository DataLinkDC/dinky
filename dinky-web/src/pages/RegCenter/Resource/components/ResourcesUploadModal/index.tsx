import React from 'react';
import {InboxOutlined} from '@ant-design/icons';
import type {UploadProps} from 'antd';
import {message, Modal, Upload} from 'antd';
import {Resource} from "@/pages/RegCenter/Resource/components/ResourceOverView";
import {ModalForm} from "@ant-design/pro-components";

const {Dragger} = Upload;

type ResourcesUploadModalProps = {
  onUpload: {url:string,pid:string,description:string};
  visible: boolean;
  onClose: () => void;
  onOk: () => void;
}


const ResourcesUploadModal: React.FC<ResourcesUploadModalProps> = (props) => {

  const {onUpload, onClose, onOk, visible} = props;
  const {url,pid,description}=onUpload;
  const uploadProps: UploadProps = {
    name: 'file',
    multiple: true,
    action: url+"?pid="+pid,
    onChange(info) {
      const {status} = info.file;
      if (status !== 'uploading') {
        console.log(info.file, info.fileList);
      }
      if (status === 'done') {
        message.success(`${info.file.name} file uploaded successfully.`);
      } else if (status === 'error') {
        message.error(`${info.file.name} file upload failed.`);
      }
    },
    onDrop(e) {
      console.log('Dropped files', e.dataTransfer.files);
    },
  };
  return (
    <Modal
      title={'Upload File'}
      onOk={onOk}
      onCancel={onClose}
      open={visible}
    >
      <Dragger {...uploadProps}>
        <p className="ant-upload-drag-icon">
          <InboxOutlined/>
        </p>
        <p className="ant-upload-text">Click or drag file to this area to upload</p>
        <p className="ant-upload-hint">
          Support for a single or bulk upload. Strictly prohibited from uploading company data or other
          banned files.
        </p>
      </Dragger>
    </Modal>
  )
}

export default ResourcesUploadModal;

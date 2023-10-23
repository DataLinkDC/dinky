import { l } from '@/utils/intl';
import { ErrorMessageAsync, SuccessMessageAsync } from '@/utils/messages';
import { InboxOutlined } from '@ant-design/icons';
import type { UploadProps } from 'antd';
import { Modal, Upload } from 'antd';
import React from 'react';

const { Dragger } = Upload;

type ResourcesUploadModalProps = {
  onUpload: { url: string; pid: string; description: string };
  visible: boolean;
  onClose: () => void;
  onOk: () => void;
};

const ResourcesUploadModal: React.FC<ResourcesUploadModalProps> = (props) => {
  const { onUpload, onClose, onOk, visible } = props;
  const { url, pid, description } = onUpload;

  const uploadProps: UploadProps = {
    name: 'file',
    multiple: true,
    action: url + '?pid=' + pid,
    onChange: async (info) => {
      const { status } = info.file;
      if (status !== 'uploading') {
        console.log(info.file, info.fileList);
      }
      if (status === 'done') {
        await SuccessMessageAsync(
          l('rc.resource.upload.success', '', { fileName: info.file.name })
        );
      } else if (status === 'error') {
        await ErrorMessageAsync(l('rc.resource.upload.fail', '', { fileName: info.file.name }));
      }
    },
    onDrop(e) {
      console.log('Dropped files', e.dataTransfer.files);
    }
  };
  return (
    <Modal title={l('rc.resource.upload')} onOk={onOk} onCancel={onClose} open={visible}>
      <Dragger {...uploadProps}>
        <p className='ant-upload-drag-icon'>
          <InboxOutlined />
        </p>
        <p className='ant-upload-text'>{l('rc.resource.upload.tip1')}</p>
        <p className='ant-upload-hint'>{l('rc.resource.upload.tip2')}</p>
      </Dragger>
    </Modal>
  );
};

export default ResourcesUploadModal;

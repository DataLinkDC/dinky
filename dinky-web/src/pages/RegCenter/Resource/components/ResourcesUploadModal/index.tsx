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
      const { status, response } = info.file;
      if (status !== 'uploading') {
        console.log(info.file, info.fileList);
      }
      if (status === 'done') {
        if (response.success) {
          await SuccessMessageAsync(
            l('rc.resource.upload.success', '', { fileName: info.file.name })
          );
        } else {
          await ErrorMessageAsync(response.msg);
        }
      } else if (status === 'error') {
        await ErrorMessageAsync(l('rc.resource.upload.fail', '', { fileName: info.file.name }));
      }
    },
    onDrop(e) {
      console.log('Dropped files', e.dataTransfer.files);
    }
  };
  return (
    <Modal
      title={l('rc.resource.upload')}
      okButtonProps={{ htmlType: 'submit', autoFocus: true }}
      onOk={onOk}
      onCancel={onClose}
      open={visible}
    >
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

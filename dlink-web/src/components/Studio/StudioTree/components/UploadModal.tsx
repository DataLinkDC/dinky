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


import React from 'react';
import {Button, message, Modal, Upload} from "antd";
import {UploadOutlined} from "@ant-design/icons";
import {l} from "@/utils/intl";

interface UploadModalProps {
  visible: boolean;
  action: string;
  handleOk: () => void;
  onCancel: () => void;
  buttonTitle: string;
}

const UploadModal: React.FC<UploadModalProps> = (props: any) => {


  const {visible, handleOk, onCancel, action, buttonTitle} = props;
  const handlers = {
    name: 'file',
    action: action,
    maxCount: 1,
    multiple: true,
    onChange(info: any) {
      if (info.file.status !== 'uploading') {
        console.log(info.file, info.fileList);
      }
      if (info.file.status === 'done') {
        console.log('info:', info);
        if (info.file.response.code === 1) {
          message.error(`${info.file.response.msg} `);
        } else {
          message.success(`${info.file.name}` + l('app.request.upload.success'));
        }
      } else if (info.file.status === 'error') {
        message.error(`${info.file.name} `+ l('app.request.upload.failed'));
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
          <Button size="small" icon={<UploadOutlined/>}>{buttonTitle}</Button>
        </Upload>
      </Modal>
    </div>
  );
};

export default UploadModal;

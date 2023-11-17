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

import { CodeTreeShow } from '@/pages/RegCenter/GitProject/components/CodeTree/CodeTreeShow';
import { GitProject } from '@/types/RegCenter/data';
import { l } from '@/utils/intl';
import { Modal } from 'antd';
import React from 'react';

type CodeTreeProps = {
  onCancel: (flag?: boolean) => void;
  modalVisible: boolean;
  values: Partial<GitProject>;
};
export const CodeTree: React.FC<CodeTreeProps> = (props) => {
  const { values, modalVisible, onCancel } = props;

  return (
    <Modal
      title={l('rc.gp.codeTree')}
      width={'95%'}
      styles={{
        body: {
          height: 'calc(100vh - 300px)',
          overflow: 'auto'
        }
      }}
      open={modalVisible}
      maskClosable={false}
      onCancel={() => onCancel()}
      cancelText={l('button.close')}
      okButtonProps={{ style: { display: 'none' } }}
    >
      <CodeTreeShow values={values} />
    </Modal>
  );
};

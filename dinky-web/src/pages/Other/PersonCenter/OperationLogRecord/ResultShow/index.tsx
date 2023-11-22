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

import CodeShow from '@/components/CustomEditor/CodeShow';
import { NORMAL_MODAL_OPTIONS } from '@/services/constants';
import { l } from '@/utils/intl';
import { Modal } from 'antd';
import React from 'react';

type ResultShowProps = {
  config: {
    open: boolean;
    data: string;
  };
  onCancel: () => void;
};

const ResultShow: React.FC<ResultShowProps> = (props) => {
  const {
    config: { open, data },
    onCancel
  } = props;

  return (
    <Modal
      title={l('global.result')}
      open={open}
      {...NORMAL_MODAL_OPTIONS}
      onCancel={onCancel}
      okButtonProps={{ hidden: true }}
      cancelText={l('button.close')}
    >
      <CodeShow language={'json'} height={'60vh'} code={data} />
    </Modal>
  );
};

export default ResultShow;

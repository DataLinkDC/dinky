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
import { Modal } from 'antd';
import React from 'react';

type ShowLogProps = {
  type: string;
  log: string;
  visibleViewLog: boolean;
  cancelViewLog: () => void;
};
/**
 * code edit props
 */
const CodeEditProps: any = {
  height: '70vh',
  width: '60vw',
  lineNumbers: 'on',
  language: 'java'
};

const ShowLog: React.FC<ShowLogProps> = (props) => {
  const { type, log, visibleViewLog, cancelViewLog } = props;

  return (
    <Modal
      {...NORMAL_MODAL_OPTIONS}
      title={type}
      open={visibleViewLog}
      onCancel={cancelViewLog}
      okButtonProps={{ style: { display: 'none' } }}
    >
      <CodeShow {...CodeEditProps} code={log} showFloatButton />
    </Modal>
  );
};

export default ShowLog;

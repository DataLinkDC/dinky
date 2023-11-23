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

import CodeEdit from '@/components/CustomEditor/CodeEdit';
import { StartButton } from '@/components/StyledComponents';
import { l } from '@/utils/intl';
import { PlayCircleOutlined } from '@ant-design/icons';
import { Col, Row } from 'antd';
import React, { memo } from 'react';

const CodeEditProps = {
  height: '26vh',
  lineNumbers: 'on',
  language: 'sql'
  // autoRange: true, // todo: 使用此配置项,后续可以自动识别光标位置,根据;分割,得到 sql 片段区间, 在左侧自动添加 执行按钮 和 区间选中效果, 规划内,暂未实现
};

type EditorProps = {
  loading: boolean;
  execCallback: () => void;
  handleInputChange: (value: string) => void;
  inputValue: string;
};
// todo: 代码编辑器, 代码片段执行, 代码片段选中, 代码片段执行按钮, 代码片段执行结果, 代码片段执行结果展示
const Editor: React.FC<EditorProps> = (props) => {
  const { loading, execCallback, handleInputChange, inputValue } = props;

  return (
    <>
      <Row>
        <Col flex='auto'>
          <StartButton>
            <PlayCircleOutlined
              title={l('rc.ds.console.exec')}
              spin={loading}
              onClick={execCallback}
              disabled={loading}
            />
          </StartButton>
          <CodeEdit
            {...CodeEditProps}
            onChange={(value) => handleInputChange(value ?? '')}
            code={inputValue}
          />
        </Col>
      </Row>
    </>
  );
};
export default memo(Editor);

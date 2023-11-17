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
import { GitProjectTreeNode } from '@/types/RegCenter/data';
import { renderLanguage, unSupportView } from '@/utils/function';
import { l } from '@/utils/intl';
import { Empty } from 'antd';
import { EditorLanguage } from 'monaco-editor/esm/metadata';
import React from 'react';

/**
 * CodeContentProps
 */
type CodeContentProps = {
  code: string;
  current: GitProjectTreeNode;
};

/**
 * code edit props
 */
const CodeEditProps = {
  width: '100%',
  lineNumbers: 'on'
};

export const CodeContent: React.FC<CodeContentProps> = (props) => {
  /**
   * Get the code and current node from props
   */
  const { code, current } = props;

  /**
   * Get the language according to the file suffix rules
   * @returns {string}
   */
  const getLanguage = () => {
    return renderLanguage(current.name, '.') as EditorLanguage;
  };

  /**
   * Render the code display component
   * @returns {JSX.Element}
   */
  const render = () => {
    if (unSupportView(current.name)) {
      return (
        <Empty className={'code-content-empty'} description={l('rc.gp.codeTree.unSupportView')} />
      );
    } else if (code === '' || code === null) {
      return <Empty className={'code-content-empty'} description={l('rc.gp.codeTree.clickShow')} />;
    } else {
      return (
        <CodeShow
          {...{ ...CodeEditProps, height: parent.innerHeight - 320 }}
          language={getLanguage()}
          showFloatButton
          code={code}
        />
      );
    }
  };

  /**
   * If the code is empty, display the empty component, otherwise display the code
   */
  return <>{render()}</>;
};

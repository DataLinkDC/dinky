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
import { renderLanguage, unSupportView } from '@/utils/function';
import { l } from '@/utils/intl';
import { Empty, Space, Typography } from 'antd';
import React from 'react';
import { NavLink } from 'react-router-dom';

const { Text } = Typography;

const CodeEditProps = {
  height: '88vh',
  width: '100%',
  lineNumbers: 'on'
};

type FileShowProps = {
  item: any;
  code: string;
  onChange: (val: string) => void;
};

const FileShow: React.FC<FileShowProps> = (props) => {
  const {
    item: { name, isLeaf },
    code,
    onChange
  } = props;

  /**
   * code show props
   * @type {{code: string, onChange: (val: string) => void, language: string, showFloatButton: boolean, refreshLogCallback: () => void}}
   */
  const restCodeShowProps: any = {
    showFloatButton: true,
    code,
    onChange: onChange,
    language: renderLanguage(name, '.')
  };

  /**
   * render content
   * @returns {JSX.Element}
   */
  const renderContent = () => {
    if (name && unSupportView(name) && isLeaf) {
      return (
        <Empty
          style={{ alignItems: 'center', justifyContent: 'center', top: '50%', height: '100%' }}
          imageStyle={{
            marginTop: '20vh'
          }}
          description={l('rc.gp.codeTree.unSupportView')}
        />
      );
    } else if (code === '' || code === null || code === undefined) {
      return (
        <>
          <Empty
            style={{ alignItems: 'center', justifyContent: 'center', top: '50%', height: '100%' }}
            imageStyle={{
              marginTop: '20vh'
            }}
            description={
              <>
                <Space direction={'vertical'}>
                  <Text type='success' strong>
                    {' '}
                    {l('rc.resource.click')}
                  </Text>
                  <Text type='danger' strong>
                    {l('rc.resource.click.tip1')}
                  </Text>
                  <Text mark ellipsis italic>
                    {l('rc.resource.click.tip2')}
                  </Text>
                  <Text mark ellipsis italic>
                    {l('rc.resource.click.tip3')}{' '}
                    <NavLink to={'/settings/globalsetting'}>{l('menu.settings')}</NavLink>
                  </Text>
                </Space>
              </>
            }
          />
        </>
      );
    } else {
      return <CodeShow {...restCodeShowProps} {...CodeEditProps} />;
    }
  };

  return <>{renderContent()}</>;
};

export default FileShow;

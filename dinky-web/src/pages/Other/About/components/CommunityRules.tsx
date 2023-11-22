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

import { AlertRestProps, TypographyRestProps } from '@/pages/Other/About';
import { l } from '@/utils/intl';
import { Alert, Typography } from 'antd';

export const CommunityRules = () => {
  return (
    <>
      <Alert message={l('about.communityRules')} type='success' {...AlertRestProps} />
      <Typography.Text {...TypographyRestProps}>
        <ul>
          <li>{l('about.communityRules.1')}</li>
          <li>{l('about.communityRules.2')}</li>
          <li>{l('about.communityRules.3')}</li>
          <li>{l('about.communityRules.4')}</li>
          <li>
            <a
              href={'https://github.com/DataLinkDC/dinky/issues/66'}
              target={'_blank'}
              rel='noreferrer'
            >
              Issue
            </a>
            {l('about.communityRules.5')}
          </li>
        </ul>
      </Typography.Text>
    </>
  );
};

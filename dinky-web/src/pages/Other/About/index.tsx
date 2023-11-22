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

import { CommunityRules } from '@/pages/Other/About/components/CommunityRules';
import { QRCode } from '@/pages/Other/About/components/QRCode';
import { UsingHelp } from '@/pages/Other/About/components/UsingHelp';
import { PageContainer, ProCard } from '@ant-design/pro-components';
import React from 'react';
import { Precautions } from './components/Precautions';

export const AlertRestProps = {
  banner: true,
  showIcon: true,
  style: {
    margin: -12,
    marginBottom: 24
  }
};

export const ImageRestProps = {
  height: 300,
  width: 250
};

export const TypographyRestProps = {
  ellipsis: true,
  strong: true,
  style: {
    marginRight: '2vw'
  }
};

const About: React.FC = () => {
  return (
    <>
      <PageContainer title={false}>
        <ProCard ghost split='vertical'>
          <ProCard wrap ghost colSpan='55%'>
            <ProCard>
              <QRCode />
            </ProCard>
            <ProCard>
              <Precautions />
            </ProCard>
          </ProCard>

          <ProCard wrap ghost>
            <ProCard>
              <UsingHelp />
            </ProCard>

            <ProCard>
              <CommunityRules />
            </ProCard>
          </ProCard>
        </ProCard>
      </PageContainer>
    </>
  );
};

export default About;

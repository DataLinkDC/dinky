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

import { QUICK_GUIDE } from '@/pages/DataStudio/MiddleContainer/QuickGuide/constant';
import { l } from '@/utils/intl';
import { Link } from '@umijs/max';
import { Divider, Typography } from 'antd';
import { Fragment } from 'react';

const { Title, Paragraph } = Typography;

const QuickGuide = () => {
  const buildQuickGuide = () => {
    return QUICK_GUIDE.map((item, index) => {
      return (
        <Fragment key={index}>
          {index % 5 === 0 ? (
            <>
              <Divider plain orientationMargin={0} />
              <Link to={item.link} key={item.key}>
                {item.label}
              </Link>
            </>
          ) : (
            <>
              <Divider type='vertical' />
              <Link to={item.link} key={item.key}>
                {item.label}
              </Link>
            </>
          )}
        </Fragment>
      );
    });
  };

  return (
    <Typography style={{ padding: '2px', textAlign: 'center', border: 'salmon' }}>
      <Title level={4}>{l('datastudio.middle.qg')}</Title>
      <Paragraph style={{ padding: 0, margin: 0, textAlign: 'center', border: 'salmon' }}>
        {buildQuickGuide()}
      </Paragraph>
    </Typography>
  );
};

export default QuickGuide;

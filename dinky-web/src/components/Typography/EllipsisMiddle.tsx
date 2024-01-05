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

import { Tooltip, Typography } from 'antd';

const { Paragraph, Text } = Typography;

type EllipsisMiddleProps = {
  maxCount: number;
  children: string;
  copyable?: boolean;
};

const EllipsisMiddle: React.FC<EllipsisMiddleProps> = (props) => {
  const { maxCount, children, copyable = true } = props;
  let start = '';
  let end;
  let tip = '';
  if (!children || children.length <= maxCount) {
    start = children;
  } else {
    const half = maxCount / 2;
    start = children.slice(0, half).trim();
    end = children.slice(-half).trim();
    tip = children;
  }
  return (
    <Tooltip title={tip}>
      <Text copyable={copyable}>
        {start}
        {end ? `......${end}` : ''}
      </Text>
    </Tooltip>
  );
};

export default EllipsisMiddle;

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

import { Flex, Typography } from 'antd';
import React, { useEffect, useState } from 'react';

const { Text } = Typography;

type MoreInfoProps = {
  maxRows: number;
  children: string;
  type?: 'secondary' | 'success' | 'warning' | 'danger';
};

const MoreInfo: React.FC<MoreInfoProps> = (props) => {
  const { maxRows, children, type = 'secondary' } = props;

  const [expanded, setExpanded] = useState(false);
  const [rows, setRows] = useState(maxRows);

  useEffect(() => {
    if (expanded) {
      setRows(10);
    }
  }, [expanded]);

  return (
    <Flex gap={16} vertical>
      <Typography.Paragraph
        type={type}
        ellipsis={{
          tooltip: children,
          rows,
          expandable: true,
          expanded,
          onExpand: (_, info) => setExpanded(info.expanded)
        }}
      >
        {children}
      </Typography.Paragraph>
    </Flex>
  );
};

export default MoreInfo;

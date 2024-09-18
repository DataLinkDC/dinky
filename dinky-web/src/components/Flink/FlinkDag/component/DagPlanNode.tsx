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

import { Card, Typography } from 'antd';

const { Text, Paragraph } = Typography;

const DagPlanNode = (props: any) => {
  const { node } = props;
  const data: any = node?.getData();

  return (
    <Card
      style={{ width: 'inherit', padding: 0, margin: 0, height: 'inherit' }}
      bordered={false}
      size={'small'}
      type={'inner'}
      hoverable={true}
      title={data.description}
      extra={<Text keyboard>{data.parallelism}</Text>}
    >
      <Paragraph
        ellipsis={{
          tooltip: { title: data.description, zIndex: 2000 },
          rows: 3
        }}
      >
        <blockquote>
          <Text>{data.description}</Text>
        </blockquote>
      </Paragraph>
    </Card>
  );
};
export default DagPlanNode;

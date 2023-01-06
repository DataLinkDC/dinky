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


import {Badge, Descriptions} from 'antd';

const Tables = (props: any) => {

  const {table} = props;

  return (<Descriptions bordered>
    <Descriptions.Item label="Name">{table.name}</Descriptions.Item>
    <Descriptions.Item label="Schema">{table.schema}</Descriptions.Item>
    <Descriptions.Item label="Catalog">{table.catalog}</Descriptions.Item>
    <Descriptions.Item label="Rows">{table.rows}</Descriptions.Item>
    <Descriptions.Item label="Type">{table.type}</Descriptions.Item>
    <Descriptions.Item label="Engine">{table.engine}</Descriptions.Item>
    <Descriptions.Item label="Options" >
      {table.options}
    </Descriptions.Item>
    <Descriptions.Item label="Status"><Badge status="processing" text="Running" /></Descriptions.Item>
    <Descriptions.Item label="CreateTime">{table.createTime}</Descriptions.Item>
    <Descriptions.Item label="UpdateTime">{table.updateTime}</Descriptions.Item>
    <Descriptions.Item label="Comment" span={3}>{table.comment}</Descriptions.Item>
  </Descriptions>)
};

export default Tables;

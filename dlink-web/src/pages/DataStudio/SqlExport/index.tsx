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


import {Typography} from 'antd';
import {useEffect, useState} from "react";
import {getData} from "@/components/Common/crud";
import CodeShow from "@/components/Common/CodeShow";

const {Paragraph} = Typography;

const SqlExport = (props: any) => {

  const {id} = props;
  const [statement, setStatement] = useState<string>('');

  const refreshStatement = async () => {
    const msg = await getData('api/task/exportSql', {id: id});
    setStatement(msg.datas);
  };

  useEffect(() => {
    refreshStatement();
  }, []);

  return (<>
    <Paragraph copyable={{text: statement}}>
    </Paragraph>
    <Paragraph>
      <CodeShow code={statement} language='sql'
                height='500px' theme="vs-dark"/>
    </Paragraph></>)
};

export default SqlExport;

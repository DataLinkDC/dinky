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


import {Input, Button, Space, Empty} from "antd";
import {StateType} from "@/pages/DataStudio/model";
import {connect} from "umi";
import {useState} from "react";
import { SearchOutlined } from '@ant-design/icons';
import ProTable from '@ant-design/pro-table';
import DTable from "@/components/Common/DTable";

const StudioPreview = (props:any) => {

  const {result} = props;

  const getColumns=(columns:[])=>{
    let datas:any=[];
    columns.map((item)=> {
      datas.push({
        field: item,
      });
    });
    return datas;
  };

  return (
    <div style={{width: '100%'}}>
      {result&&result.jobId&&!result.isDestroyed&&result.rowData&&result.columns?
        (<DTable dataSource={result.rowData} columns={getColumns(result.columns)}
                   pagination={{
                     pageSize: 5,
                   }}
      />):(<Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />)}
    </div>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  current: Studio.current,
  // result: Studio.result,
}))(StudioPreview);

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


import {Button, Empty, Tag} from "antd";
import {StateType} from "@/pages/DataStudio/model";
import {connect} from "umi";
import {FireOutlined, SearchOutlined} from '@ant-design/icons';
import {showJobData} from "@/components/Studio/StudioEvent/DQL";
import {isSql} from "@/components/Studio/conf";
import DTable from "@/components/Common/DTable";
import {l} from "@/utils/intl";

const StudioTable = (props: any) => {

  const {current, dispatch} = props;

  const getColumns = (columns: []) => {
    let datas: any = [];
    columns?.map((item) => {
      datas.push({
        field: item,
      });
    });
    return datas;
  };

  const showDetail = () => {
    showJobData(current.key, current.console.result.jobId, dispatch)
  };

  const renderFlinkSQLContent = () => {
    return (<>
      {(current.console.result.jobId && (current.console.result.jobId.indexOf('unknown') === -1)) ? (<>
        <Button type="primary" onClick={showDetail} icon={<SearchOutlined/>}>
          获取最新数据
        </Button> &nbsp;
        <Tag color="blue" key={current.console.result.jobId}>
          <FireOutlined/> {current.console.result.jobId}
        </Tag></>) : undefined}
      {current.console.result.result && current.console.result.result.columns ?
        <DTable dataSource={current.console.result.result.rowData}
                columns={getColumns(current.console.result.result.columns)}/>
        : (<Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>)
      }
    </>)
  }

  const renderSQLContent = () => {
    return (<>
      {current.console.result.result ?
        <DTable dataSource={current.console.result.result.rowData}
                columns={getColumns(current.console.result.result.columns)}/>
        : (<Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>)
      }
    </>)
  }

  return (
    <div style={{width: '100%'}}>
      {current ? (isSql(current.task.dialect) ? renderSQLContent() : renderFlinkSQLContent()) : undefined}
    </div>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  current: Studio.current,
}))(StudioTable);

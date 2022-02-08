import {Input, Button, Space, Empty} from "antd";
import {StateType} from "@/pages/FlinkSqlStudio/model";
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

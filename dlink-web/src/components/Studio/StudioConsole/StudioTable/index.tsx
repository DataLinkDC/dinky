import {Button, Tag, Empty} from "antd";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {connect} from "umi";
import {FireOutlined, SearchOutlined} from '@ant-design/icons';
import {showJobData} from "@/components/Studio/StudioEvent/DQL";
import {isSql} from "@/components/Studio/conf";
import DTable from "@/components/Common/DTable";

const StudioTable = (props:any) => {

  const {current,result,dispatch} = props;

  const getColumns=(columns:[])=>{
    let datas:any=[];
    columns.map((item)=> {
      datas.push({
        field: item,
      });
    });
    return datas;
  };

  const showDetail=()=>{
    showJobData(current.console.result.jobId,dispatch)
  };

  const renderFlinkSQLContent = () => {
    return (<>
      <Button type="primary" onClick={showDetail} icon={<SearchOutlined/>}>
        获取最新数据
      </Button> &nbsp;
      {current.console.result.jobId && (<Tag color="blue" key={current.console.result.jobId}>
        <FireOutlined /> {current.console.result.jobId}
      </Tag>)}
      {result.columns?
        <DTable dataSource={result.rowData} columns={getColumns(result.columns)}/>
        :(<Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />)
      }
    </>)
  }

  const renderSQLContent = () => {
    return (<>
      {current.console.result.result?
        <DTable dataSource={current.console.result.result.rowData} columns={getColumns(current.console.result.result.columns)}/>
        :(<Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />)
      }
    </>)
  }

  return (
    <div style={{width: '100%'}}>
      {isSql(current.task.dialect)?renderSQLContent():renderFlinkSQLContent()}
    </div>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  current: Studio.current,
  result: Studio.result,
}))(StudioTable);

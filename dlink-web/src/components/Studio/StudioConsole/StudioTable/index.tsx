import {Button, Tag, Empty} from "antd";
import {StateType} from "@/pages/DataStudio/model";
import {connect} from "umi";
import {FireOutlined, SearchOutlined} from '@ant-design/icons';
import {showJobData} from "@/components/Studio/StudioEvent/DQL";
import {isSql} from "@/components/Studio/conf";
import DTable from "@/components/Common/DTable";

const StudioTable = (props: any) => {

  const {current,dispatch} = props;

  const getColumns=(columns: [])=>{
    let datas: any=[];
    columns?.map((item) => {
      datas.push({
        field: item,
      });
    });
    return datas;
  };

  const showDetail=()=>{
    showJobData(current.key,current.console.result.jobId,dispatch)
  };

  const renderFlinkSQLContent = () => {
    return (<>
      {(current.console.result.jobId&&(current.console.result.jobId.indexOf('unknown') === -1)) ? (<>
      <Button type="primary" onClick={showDetail} icon={<SearchOutlined/>}>
        获取最新数据
      </Button> &nbsp;
      <Tag color="blue" key={current.console.result.jobId}>
        <FireOutlined /> {current.console.result.jobId}
      </Tag></>):undefined}
      {current.console.result.result&&current.console.result.result.columns?
        <DTable dataSource={current.console.result.result.rowData} columns={getColumns(current.console.result.result.columns)}/>
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
      {current?(isSql(current.task.dialect)?renderSQLContent():renderFlinkSQLContent()):undefined}
    </div>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  current: Studio.current,
}))(StudioTable);

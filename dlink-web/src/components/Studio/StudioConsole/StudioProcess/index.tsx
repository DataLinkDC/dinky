import {Input, Tag, Divider, Empty, message, Select} from "antd";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {connect} from "umi";
import {useState} from "react";
import { SearchOutlined } from '@ant-design/icons';
import ProTable from '@ant-design/pro-table';
import {showFlinkJobs} from "../../StudioEvent/DDL";

const { Option } = Select;

const StudioProcess = (props:any) => {

  const {cluster} = props;
  const [jobsData, setJobsData] = useState<any>({});

  const getColumns=()=>{
    let columns: any = [{
      title: "JobId",
      dataIndex: "id",
      key: "id",
      sorter: true,
    },{
      title: "status",
      dataIndex: "status",
      sorter: true,
    }, {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => [
        <a
          onClick={() => {
            message.success('敬请期待');
          }}
        >
          详情
        </a>, <Divider type="vertical"/>, <a
          onClick={() => {
            message.success('敬请期待');
          }}
        >
          停止
        </a>
      ],
    },];
    return columns;
  };

  const getClusterOptions = ()=>{
    let itemList = [];
    for(let item of cluster){
      let tag =(<><Tag color={item.enabled?"processing":"error"}>{item.type}</Tag>{item.alias}</>);
      itemList.push(<Option value={item.id} label={tag}>
        {tag}
      </Option>)
    }
    return itemList;
  };

  const onChangeCluster= (value:number)=>{
    let res = showFlinkJobs(value);
    res.then((result) => {
      setJobsData(result.datas);
    });
  };

  return (
    <div style={{width: '100%'}}>
      <Select
        style={{ width: '100%' }}
        placeholder="选择Flink集群"
        optionLabelProp="label"
        onChange={onChangeCluster}
      >
        {getClusterOptions()}
      </Select>
      {jobsData.length>0?
        (<ProTable dataSource={jobsData} columns={getColumns()} search={false}
        />):(<Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />)}
    </div>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  cluster: Studio.cluster,
}))(StudioProcess);

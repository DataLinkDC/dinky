import {Typography, Input, Button, Space, Table, Select, Tag, Form, Empty,Tooltip} from "antd";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {connect} from "umi";
import React, {useState} from "react";
// import Highlighter from 'react-highlight-words';
import {FireOutlined, SearchOutlined} from '@ant-design/icons';
import {showJobData} from "@/components/Studio/StudioEvent/DQL";
import ProTable from '@ant-design/pro-table';
import {DIALECT, isSql} from "@/components/Studio/conf";

const { Option } = Select;
const { Title, Paragraph, Text, Link } = Typography;


const StudioTable = (props:any) => {

  const {current,result,dispatch} = props;
  const [searchText,setSearchText] = useState<string>('');
  const [searchedColumn,setSearchedColumn] = useState<string>('');

  const getColumnSearchProps = (dIndex) => ({
    filterDropdown: ({ setSelectedKeys, selectedKeys, confirm, clearFilters }) => (
      <div style={{ padding: 8 }}>
        <Input
          placeholder={`Search ${dIndex}`}
          value={selectedKeys[0]}
          onChange={e => setSelectedKeys(e.target.value ? [e.target.value] : [])}
          onPressEnter={() => handleSearch(selectedKeys, confirm, dIndex)}
          style={{ marginBottom: 8, display: 'block' }}
        />
        <Space>
          <Button
            type="primary"
            onClick={() => handleSearch(selectedKeys, confirm, dIndex)}
            icon={<SearchOutlined />}
            size="small"
            style={{ width: 90 }}
          >
            搜索
          </Button>
          <Button onClick={() => handleReset(clearFilters)} size="small" style={{ width: 90 }}>
            重置
          </Button>
          <Button
            type="link"
            size="small"
            onClick={() => {
              setSearchText(selectedKeys[0]);
              setSearchedColumn(dIndex);
            }}
          >
            过滤
          </Button>
        </Space>
      </div>
    ),
    filterIcon: filtered => <SearchOutlined style={{ color: filtered ? '#1890ff' : undefined }} />,
    onFilter: (value, record) =>
      record[dIndex]
        ? record[dIndex].toString().toLowerCase().includes(value.toLowerCase())
        : '',
    /*render: text =>
      searchedColumn === dIndex ? (
        <Highlighter
          highlightStyle={{ backgroundColor: '#ffc069', padding: 0 }}
          searchWords={[searchText]}
          autoEscape
          textToHighlight={text ? text.toString() : ''}
        />
      ) : (
        text
      ),*/
  });

  const handleSearch = (selectedKeys, confirm, dIndex) => {
    confirm();
    setSearchText(selectedKeys[0]);
    setSearchedColumn(dIndex);
  };

  const handleReset = (clearFilters) => {
    clearFilters();
    setSearchText('');
  };

  const getColumns=(columns:[])=>{
    let datas:any=[];
    columns.map((item)=> {
      datas.push({
        title: item,
        dataIndex: item,
        key: item,
        // sorter: true,
        ...getColumnSearchProps(item),
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
        <ProTable dataSource={result.rowData} columns={getColumns(result.columns)} search={false}
                  options={{
                    search: false,
                  }}/>
        :(<Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />)
      }
    </>)
  }

  const renderSQLContent = () => {
    return (<>
      {current.console.result.result?
        <ProTable dataSource={current.console.result.result.rowData} columns={getColumns(current.console.result.result.columns)} search={false}
                  options={{
                    search: false,
                  }}/>
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

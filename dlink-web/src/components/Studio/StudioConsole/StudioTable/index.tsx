import {Typography, Input, Button, Space, Table, Select, Tag, Form, Empty,Tooltip} from "antd";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {connect} from "umi";
import {useState} from "react";
// import Highlighter from 'react-highlight-words';
import { SearchOutlined } from '@ant-design/icons';

const { Option } = Select;
const { Title, Paragraph, Text, Link } = Typography;


const StudioTable = (props:any) => {

  const {current} = props;
  const [dataIndex,setDataIndex] = useState<number>(0);
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
        sorter: true,
        ...getColumnSearchProps(item),
      });
    });
    return datas;
  };

  const onChange=(val:number)=>{
    setDataIndex(val);
  };

  return (
    <Typography>
      <Form.Item label="当前执行记录" tooltip="选择最近的执行记录，仅包含成功的记录">
      <Select
        style={{ width: '100%' }}
        placeholder="选择最近的执行记录"
        optionLabelProp="label"
        onChange={onChange}
      >
        {current.console.result.map((item,index)=> {
          if(item.success) {
            let tag = (<> <Tooltip placement="topLeft" title={item.statement}><Tag color="processing">{item.finishDate}</Tag>
              <Text underline>[{item.sessionId}:{item.flinkHost}:{item.flinkPort}]</Text>
              {item.jobName&&<Text code>{item.jobName}</Text>}
              {item.jobId&&<Text code>{item.jobId}</Text>}
              <Text keyboard>{item.time}ms</Text>
              {item.statement}</Tooltip></>);
            return (<Option value={index} label={tag}>
              {tag}
            </Option>)
          }
        })}
        </Select>
      </Form.Item>
      {current.console.result[dataIndex]&&current.console.result[dataIndex].result?(<Table dataSource={current.console.result[dataIndex].result.rowData} columns={getColumns(current.console.result[dataIndex].result.columns)} />):(<Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />)}
    </Typography>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  current: Studio.current,
}))(StudioTable);

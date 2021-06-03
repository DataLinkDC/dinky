import {message, Input, Button, Space, Table,  Dropdown, Menu, Empty,Divider} from "antd";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {connect} from "umi";
import {useState} from "react";
// import Highlighter from 'react-highlight-words';
import { SearchOutlined,DownOutlined,TableOutlined } from '@ant-design/icons';
import React from "react";
import {executeDDL} from "@/pages/FlinkSqlStudio/service";


const StudioConnector = (props:any) => {

  const {current} = props;
  const [tableData,setTableData] = useState<[]>([]);
  const [loadings,setLoadings] = useState<boolean[]>([]);
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

  const MoreBtn: React.FC<{
    item:any
  }> = ({item}) => (
    <Dropdown
      overlay={
        <Menu onClick={({key}) => keyEvent(key, item)}>
          <Menu.Item key="desc">描述</Menu.Item>
          <Menu.Item key="delete">删除</Menu.Item>
        </Menu>
      }
    >
      <a>
        更多 <DownOutlined/>
      </a>
    </Dropdown>
  );

  const keyEvent=(key, item)=>{
    if(key=='delete'){
      let newLoadings = [...loadings];
      newLoadings[1] = true;
      setLoadings(newLoadings);
      const res = executeDDL({
        statement:"drop table "+item.tablename,
        clusterId: current.task.clusterId,
        session:current.task.session,
      });
      res.then((result)=>{
        if(result.datas.success){
          let newTableData = tableData;
          for (let i=0; i<newTableData.length; i++) {
            if (newTableData[i].tablename == item.tablename) {
              newTableData.splice(i, 1);
              setTableData(newTableData);
              break;
            }
          }
        }
        let newLoadings = [...loadings];
        newLoadings[1] = false;
        setLoadings(newLoadings);
      });
    }else{
      message.warn("敬请期待");
    }
  };

  const getTables = () => {
    let newLoadings = [...loadings];
    newLoadings[0] = true;
    setLoadings(newLoadings);
    const res = executeDDL({
      statement:"show tables",
      clusterId: current.task.clusterId,
      session:current.task.session,
    });
    res.then((result)=>{
      if(result.datas.result.rowData.length>0){
        setTableData(result.datas.result.rowData);
      }else {
        setTableData([]);
      }
      let newLoadings = [...loadings];
      newLoadings[0] = false;
      setLoadings(newLoadings);
    });
  };

  const getColumns=()=>{
    let columns:any=[{
      title: "表名",
      dataIndex: "tablename",
      key: "tablename",
      sorter: true,
      ...getColumnSearchProps("tablename"),
    },{
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => [
        <a
          onClick={() => {
            message.warn('敬请期待');
          }}
        >
          描述
        </a>,<Divider type="vertical" />,<a
          onClick={() => {
            keyEvent('delete',record);
          }}
        >
          删除
        </a>
      ],
    },];
    return columns;
  };

  return (
    <>
      <Button
        type="primary"
        icon={<TableOutlined />}
        loading={loadings[0]}
        onClick={() => getTables()}
      >
        获取Connectors
      </Button>
      {tableData.length>0?(<Table dataSource={tableData} columns={getColumns()} />):(<Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />)}
    </>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  current: Studio.current,
}))(StudioConnector);

import {message, Input, Button, Space, Table,  Dropdown, Menu, Empty,Divider,
  Tooltip,Breadcrumb} from "antd";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {connect} from "umi";
import {useState} from "react";
import styles from "./index.less";
import { SearchOutlined,DownOutlined,DeleteOutlined,CommentOutlined } from '@ant-design/icons';
import React from "react";
import {executeDDL} from "@/pages/FlinkSqlStudio/service";
import {handleRemove} from "@/components/Common/crud";
import {removeTable, showTables,clearSession} from "@/components/Studio/StudioEvent/DDL";


const StudioConnector = (props:any) => {

  const {current,dispatch,currentSessionCluster} = props;
  const [tableData,setTableData] = useState<[]>([]);
  const [loadings,setLoadings] = useState<boolean[]>([]);
  const [searchText,setSearchText] = useState<string>('');
  const [searchedColumn,setSearchedColumn] = useState<string>('');
  const [modalVisit, setModalVisit] = useState(false);
  const [row, setRow] = useState<{}>();

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
      removeTable(item.tablename,current.task,dispatch);
    }else{
      message.warn("敬请期待");
    }
  };

  const getTables = () => {
    showTables(current.task,dispatch);
  };

  const onClearSession = () => {
    /*let newLoadings = [...loadings];
    newLoadings[2] = true;
    setLoadings(newLoadings);
    let session = {
      id:current.task.clusterId+'_'+current.task.session,
    };
    const res = handleRemove('/api/studio/clearSession',[session]);
    res.then((result)=>{
      getTables();
      let newLoadings = [...loadings];
      newLoadings[2] = false;
      setLoadings(newLoadings);
    });*/
    clearSession(current.task,dispatch);
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
      <div style={{float: "right"}}>
        <Tooltip title="刷新连接器">
          <Button
            type="text"
            icon={<SearchOutlined />}
            onClick={getTables}
          />
        </Tooltip>
        <Tooltip title="清空连接器">
          <Button
            type="text"
            icon={<DeleteOutlined />}
            onClick={onClearSession}
          />
        </Tooltip>
      </div>
      <Breadcrumb className={styles["session-path"]}>
        <CommentOutlined />
        <Divider type="vertical" />
        <Breadcrumb.Item>{currentSessionCluster.session}</Breadcrumb.Item>
      </Breadcrumb>
      {currentSessionCluster.connectors&&currentSessionCluster.connectors.length>0?(<Table dataSource={currentSessionCluster.connectors} columns={getColumns()} size="small" />):(<Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />)}
      </>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  current: Studio.current,
  currentSessionCluster: Studio.currentSessionCluster,
}))(StudioConnector);

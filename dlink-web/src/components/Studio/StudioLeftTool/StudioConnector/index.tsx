import {message, Input, Button, Space, Table,  Dropdown, Menu, Empty,Divider,
  Tooltip,Breadcrumb} from "antd";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {connect} from "umi";
import {useState} from "react";
import styles from "./index.less";
import { SearchOutlined,DownOutlined,DeleteOutlined,CommentOutlined ,MessageOutlined,PlusOutlined} from '@ant-design/icons';
import React from "react";
import {removeTable, showTables,clearSession} from "@/components/Studio/StudioEvent/DDL";
import {
  ModalForm,
} from '@ant-design/pro-form';
import ProDescriptions from '@ant-design/pro-descriptions';
import ProTable from '@ant-design/pro-table';
import {getData, handleAddOrUpdate} from "@/components/Common/crud";
import SessionForm from "@/components/Studio/StudioLeftTool/StudioConnector/components/SessionForm";

const StudioConnector = (props:any) => {

  const {current,dispatch,currentSessionCluster} = props;
  const [tableData,setTableData] = useState<[]>([]);
  const [loadings,setLoadings] = useState<boolean[]>([]);
  const [searchText,setSearchText] = useState<string>('');
  const [searchedColumn,setSearchedColumn] = useState<string>('');
  const [modalVisit, setModalVisit] = useState(false);
  const [type, setType] = useState<number>();
  const [sessionData, setSessionData] = useState<{}>();
  const [createSessionModalVisible, handleCreateSessionModalVisible] = useState<boolean>(false);


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

  const keySessionsEvent=(key, item)=>{
    if(key=='delete'){
      clearSession(item.session,current.task,dispatch);
    }else if(key=='connect'){

    }else{
      message.warn("敬请期待");
    }
  };

  const getTables = () => {
    showTables(current.task,dispatch);
  };

  const onClearSession = () => {
    clearSession(current.task.session,current.task,dispatch);
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

  const getSessionsColumns=()=>{
    let columns:any=[{
      title: "会话 Key",
      dataIndex: "session",
      key: "session",
      sorter: true,
      ...getColumnSearchProps("session"),
    },{
      title: "执行模式",
      key: "useRemote",
      sorter: true,
      ...getColumnSearchProps("useRemote"),
      render:function(text, record, index) {
        return record.sessionConfig.useRemote?'远程':'本地';
      }
    },{
      title: "集群名",
      key: "clusterName",
      sorter: true,
      ...getColumnSearchProps("clusterName"),
      render:function(text, record, index) {
        return record.sessionConfig.clusterName;
      }
    },{
      title: "JobManager地址",
      key: "address",
      sorter: true,
      ...getColumnSearchProps("address"),
      render:function(text, record, index) {
        return record.sessionConfig.address;
      }
    },{
      title: "创建人",
      dataIndex: "createUser",
      key: "createUser",
      sorter: true,
      ...getColumnSearchProps("createUser"),
    },{
      title: "创建时间",
      dataIndex: "createTime",
      key: "createTime",
      sorter: true,
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
          连接
        </a>,<Divider type="vertical" />,<a
          onClick={() => {
            keySessionsEvent('delete',record);
          }}
        >
          删除
        </a>
      ],
    },];
    return columns;
  };

  const createSessions=()=>{
    handleCreateSessionModalVisible(true);
  };

  const showSessions=()=>{
    setModalVisit(true);
    setType(1);
    const res = getData("api/studio/listSession");
    res.then((result)=>{
      setSessionData(result.datas);
    });
  };

  return (
    <>
      <div style={{float: "right"}}>
        <Tooltip title="切换会话">
          <Button
            type="text"
            icon={<CommentOutlined />}
            onClick={showSessions}
          />
        </Tooltip>
        <Tooltip title="新建会话">
          <Button
            type="text"
            icon={<PlusOutlined />}
            onClick={createSessions}
          />
        </Tooltip>
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
        <MessageOutlined />
        <Divider type="vertical" />
        <Breadcrumb.Item>{currentSessionCluster.session}</Breadcrumb.Item>
      </Breadcrumb>
      {currentSessionCluster.connectors&&currentSessionCluster.connectors.length>0?(<Table dataSource={currentSessionCluster.connectors} columns={getColumns()} size="small" />):(<Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />)}
      <ModalForm
        // title="新建表单"
        visible={modalVisit}
        onFinish={async () => {
          setSessionData(undefined);
        }}
        onVisibleChange={setModalVisit}
        submitter={{
          submitButtonProps: {
            style: {
              display: 'none',
            },
          },
        }}
      >
        {type==1&&
        (<ProDescriptions
            column={2}
            title='全部共享会话'
          >
            <ProDescriptions.Item  span={2} >
              {sessionData?
                (<Table dataSource={sessionData} columns={getSessionsColumns()} size="small"
                />):(<Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />)}
            </ProDescriptions.Item>
          </ProDescriptions>
        )
        }
      </ModalForm>
      <SessionForm
        onSubmit={async (value) => {
          console.log(value);
          const success = await handleAddOrUpdate("api/studio/createSession",value);
          if (success) {
            handleCreateSessionModalVisible(false);
          }
        }}
        onCancel={() => {
          handleCreateSessionModalVisible(false);
        }}
        updateModalVisible={createSessionModalVisible}
        values={{}}
      />
      </>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  current: Studio.current,
  currentSessionCluster: Studio.currentSessionCluster,
}))(StudioConnector);

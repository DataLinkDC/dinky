import {
  message, Input, Button, Space, Table, Dropdown, Menu, Empty, Divider,
  Tooltip
} from "antd";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {connect} from "umi";
import {useState} from "react";
import styles from "./index.less";
import {
  SearchOutlined,
  ReloadOutlined,
  DownOutlined,
  DeleteOutlined,
  CommentOutlined,
  PoweroffOutlined,
  PlusOutlined
} from '@ant-design/icons';
import React from "react";
import {
  removeTable, showTables, clearSession, changeSession, quitSession,
  createSession, listSession
} from "@/components/Studio/StudioEvent/DDL";
import {
  ModalForm,
} from '@ant-design/pro-form';
import ProDescriptions from '@ant-design/pro-descriptions';
import SessionForm from "@/components/Studio/StudioLeftTool/StudioConnector/components/SessionForm";
import { Scrollbars } from 'react-custom-scrollbars';

const StudioConnector = (props: any) => {

  const {current,toolHeight, dispatch, currentSession, session} = props;
  const [tableData, setTableData] = useState<[]>([]);
  const [loadings, setLoadings] = useState<boolean[]>([]);
  const [searchText, setSearchText] = useState<string>('');
  const [searchedColumn, setSearchedColumn] = useState<string>('');
  const [modalVisit, setModalVisit] = useState(false);
  const [type, setType] = useState<number>();
  const [sessionData, setSessionData] = useState<{}>();
  const [createSessionModalVisible, handleCreateSessionModalVisible] = useState<boolean>(false);


  const getColumnSearchProps = (dIndex) => ({
    filterDropdown: ({setSelectedKeys, selectedKeys, confirm, clearFilters}) => (
      <div style={{padding: 8}}>
        <Input
          placeholder={`Search ${dIndex}`}
          value={selectedKeys[0]}
          onChange={e => setSelectedKeys(e.target.value ? [e.target.value] : [])}
          onPressEnter={() => handleSearch(selectedKeys, confirm, dIndex)}
          style={{marginBottom: 8, display: 'block'}}
        />
        <Space>
          <Button
            type="primary"
            onClick={() => handleSearch(selectedKeys, confirm, dIndex)}
            icon={<SearchOutlined/>}
            size="small"
            style={{width: 90}}
          >
            搜索
          </Button>
          <Button onClick={() => handleReset(clearFilters)} size="small" style={{width: 90}}>
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
    filterIcon: filtered => <SearchOutlined style={{color: filtered ? '#1890ff' : undefined}}/>,
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
    item: any
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

  const keyEvent = (key, item) => {
    if (key == 'delete') {
      removeTable(item.tablename, currentSession.session, dispatch);
    } else {
      message.warn("敬请期待");
    }
  };

  const keySessionsEvent = (key, item) => {
    if (key == 'delete') {
      clearSession(item.session, dispatch);
    } else if (key == 'connect') {
      changeSession(item, dispatch);
      message.success('连接共享会话【' + item.session + '】成功！');
      setModalVisit(false);
    } else {
      message.warn("敬请期待");
    }
  };

  const getTables = () => {
    showTables(currentSession.session, dispatch);
  };

  const onClearSession = () => {
    clearSession(currentSession.session, dispatch);
  };

  const getColumns = () => {
    let columns: any = [{
      title: "表名",
      dataIndex: "tablename",
      key: "tablename",
      sorter: true,
      ...getColumnSearchProps("tablename"),
    }, {
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
        </a>, <Divider type="vertical"/>, <a
          onClick={() => {
            keyEvent('delete', record);
          }}
        >
          删除
        </a>
      ],
    },];
    return columns;
  };

  const getSessionsColumns = () => {
    let columns: any = [{
      title: "会话 Key",
      dataIndex: "session",
      key: "session",
      sorter: true,
      ...getColumnSearchProps("session"),
    }, {
      title: "执行模式",
      key: "useRemote",
      sorter: true,
      ...getColumnSearchProps("useRemote"),
      render: function (text, record, index) {
        return record.sessionConfig.useRemote ? '远程' : '本地';
      }
    }, {
      title: "集群名",
      key: "clusterName",
      sorter: true,
      ...getColumnSearchProps("clusterName"),
      render: function (text, record, index) {
        return record.sessionConfig.clusterName;
      }
    }, {
      title: "创建人",
      dataIndex: "createUser",
      key: "createUser",
      sorter: true,
      ...getColumnSearchProps("createUser"),
    }, {
      title: "创建时间",
      dataIndex: "createTime",
      key: "createTime",
      sorter: true,
    }, {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => [
        <a
          onClick={() => {
            keySessionsEvent('connect', record);
          }}
        >
          连接
        </a>, <Divider type="vertical"/>, <a
          onClick={() => {
            keySessionsEvent('delete', record);
          }}
        >
          删除
        </a>
      ],
    },];
    return columns;
  };

  const createSessions = () => {
    handleCreateSessionModalVisible(true);
  };

  const showSessions = () => {
    setModalVisit(true);
    setType(1);
    listSession(dispatch);
  };

  const quitSessions = () => {
    quitSession(dispatch);
    message.success('退出共享会话成功！');
  };

  return (
    <>
      <Tooltip title="新建会话">
        <Button
          type="text"
          icon={<PlusOutlined/>}
          onClick={createSessions}
        />
      </Tooltip>
      <div style={{float: "right"}}>
        {session.length > 0 ? (
          <Tooltip title="切换会话">
            <Button
              type="text"
              icon={<CommentOutlined/>}
              onClick={showSessions}
            />
          </Tooltip>
        ) : ''}
        {currentSession.session && (
          <>
            <Tooltip title="退出会话">
              <Button
                type="text"
                icon={<PoweroffOutlined/>}
                onClick={quitSessions}
              />
            </Tooltip>
            <Tooltip title="刷新连接器">
              <Button
                type="text"
                icon={<ReloadOutlined/>}
                onClick={getTables}
              />
            </Tooltip>
            <Tooltip title="注销会话">
              <Button
                type="text"
                icon={<DeleteOutlined/>}
                onClick={onClearSession}
              />
            </Tooltip>
          </>)}
      </div>
      <Scrollbars style={{height: (toolHeight - 32)}}>
      {currentSession.connectors && currentSession.connectors.length > 0 ? (
        <Table dataSource={currentSession.connectors} columns={getColumns()} size="small"/>) : (
        <Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>)}
      <ModalForm
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
        {type == 1 &&
        (<ProDescriptions
            column={2}
            title='全部共享会话'
          >
            <ProDescriptions.Item span={2}>
              {session.length > 0 ?
                (<Table dataSource={session} columns={getSessionsColumns()} size="small"
                />) : (<Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>)}
            </ProDescriptions.Item>
          </ProDescriptions>
        )
        }
      </ModalForm>
      <SessionForm
        onSubmit={async (value) => {
          createSession(value, dispatch);
          handleCreateSessionModalVisible(false);
        }}
        onCancel={() => {
          handleCreateSessionModalVisible(false);
        }}
        updateModalVisible={createSessionModalVisible}
        values={{
          session: '',
          type: 'PUBLIC',
          useRemote: true,
        }}
      />
      </Scrollbars>
    </>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  current: Studio.current,
  currentSession: Studio.currentSession,
  session: Studio.session,
  toolHeight: Studio.toolHeight,
}))(StudioConnector);

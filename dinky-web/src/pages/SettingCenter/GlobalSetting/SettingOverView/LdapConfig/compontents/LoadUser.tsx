import React, {useState} from "react";
import {l} from "@/utils/intl";
import {Table, Tag} from "antd";
import {UserSwitchOutlined} from "@ant-design/icons";
import {handleOption} from "@/services/BusinessCrud";
import {API_CONSTANTS} from "@/services/constants";
import {UserBaseInfo} from "@/types/User/data";
import {getData} from "@/services/api";
import {ColumnsType} from "antd/es/table";
import {ModalForm} from "@ant-design/pro-components";


const columns: ColumnsType<UserBaseInfo.User> = [
  {
    title: l("user.username"),
    dataIndex: "username",
  },
  {
    title: l("user.nickname"),
    dataIndex: "nickname",
  },
  {
    title: l("sys.ldap.settings.loadable"),
    dataIndex: "enabled",
    render:(_,record) =>record.enabled?l('rc.ai.isSimple.yes'):l('rc.ai.isSimple.no')
  },
];


export const LoadUser = () => {

  const [loading, setLoading] = useState(false);
  const [selectedUsers, setSelectedUsers] = useState<UserBaseInfo.User[]>([]);
  const [users, setUsers] = useState<UserBaseInfo.User[]>([]);

  const fetchUserData = async () => {
    setLoading(true);
    const res = await getData(API_CONSTANTS.LDAP_LIST_USER);
    setUsers(res.datas);
    setLoading(false);
  }

  const importUser = async () => {
    await handleOption(API_CONSTANTS.LDAP_IMPORT_USERS,l("sys.ldap.settings.loadUser"),selectedUsers);
    await fetchUserData();
    setSelectedUsers([]);
  }

  return <>
    <ModalForm
      title={l("sys.ldap.settings.loadUser")}
      width={800}
      onFinish={()=>importUser()}
      trigger={
        <Tag
          icon={<UserSwitchOutlined/>}
          color="#f50"
          onClick={() => fetchUserData()}
        >
          {l("sys.ldap.settings.loadUser")}
        </Tag>
      }
    >
      <Table<UserBaseInfo.User>
        loading={loading}
        size={"small"}
        columns={columns}
        rowSelection={{
          onChange: (_,rows)=>setSelectedUsers(rows),
          getCheckboxProps: (record) => ({disabled: !record.enabled,}),
        }}
        dataSource={users}
        rowKey={"username"}
      />
    </ModalForm>
  </>;
};

import {UserBaseInfo} from "@/types/User/data";
import TableTransfer from "@/components/TableTransfer";
import {ColumnsType} from "antd/es/table";
import {API_CONSTANTS} from "@/services/constants";
import {getData} from "@/services/api";
import {useEffect, useState} from "react";
import {l} from "@/utils/intl";


type TransferFromProps = {
    user: Partial<UserBaseInfo.Role>;
    onChange: (values: string[]) => void;
};


const RoleTransfer = (props: TransferFromProps) => {
    /**
     * status
     */
    const {user, onChange: handleChange} = props;
    const [targetKeys, setTargetKeys] = useState<string[]>([]);
    const [roleTableList, setRoleTableList] = useState<UserBaseInfo.Role[]>([]);
    const [selectedKeys, setSelectedKeys] = useState<string[]>([]);

    /**
     * select change
     * @param sourceSelectedKeys
     * @param targetSelectedKeys
     */
    const onSelectChange = (
        sourceSelectedKeys: string[],
        targetSelectedKeys: string[],
    ) => {
        const newSelectedKeys = [...sourceSelectedKeys, ...targetSelectedKeys];
        setSelectedKeys(newSelectedKeys);
    };

    /**
     * get data
     */
    useEffect(() => {
        getData(API_CONSTANTS.GET_ROLES_BY_USERID, {id: user.id}).then(result => {
            setRoleTableList(result.datas.roles);
            setTargetKeys(result.datas.roleIds);
            handleChange(result.datas.roleIds);
        });
    }, []);

    /**
     * table columns
     */
    const columns: ColumnsType<UserBaseInfo.Role> = [
        {
            dataIndex: 'roleCode',
            title: l('role.roleCode'),
        },
        {
            dataIndex: 'roleName',
            title: l('role.roleName'),
        },
        {
            dataIndex: 'note',
            title: l('global.table.note'),
            ellipsis: true,
        },
    ];


    /**
     * transfer change
     * @param nextTargetKeys
     */
    const onChange = (nextTargetKeys: string[]) => {
        setTargetKeys(nextTargetKeys);
        handleChange(nextTargetKeys);
    };


    /**
     * render
     */
    return (<>
            <TableTransfer
                dataSource={roleTableList}
                targetKeys={targetKeys}
                selectedKeys={selectedKeys}
                rowKey={item => item.id as any}
                onChange={onChange}
                onSelectChange={onSelectChange}
                filterOption={(inputValue, item) =>
                    item.roleCode!.indexOf(inputValue) !== -1 || item.roleName!.indexOf(inputValue) !== -1
                }
                leftColumns={columns}
                rightColumns={columns}
            />
        </>
    );
};

export default RoleTransfer;
/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */


import React, {useCallback, useEffect, useState} from "react";
import {Button, Drawer, Empty, Input, Space, Spin, Tree} from "antd";
import {l} from "@/utils/intl";
import {UserBaseInfo} from "@/types/User/data";
import {SysMenu} from "@/types/RegCenter/data";
import {buildMenuTree} from "@/pages/AuthCenter/Menu/function";
import {Key} from "@ant-design/pro-components";
import {queryDataByParams} from "@/services/BusinessCrud";
import {API_CONSTANTS} from "@/services/constants";

type AssignMenuProps = {
    values: Partial<UserBaseInfo.Role>,
    open: boolean,
    onClose: () => void,
    onSubmit: (values: Key[]) => void,
}

const {DirectoryTree} = Tree;


const AssignMenu: React.FC<AssignMenuProps> = (props) => {

    const {open, onClose, onSubmit, values } = props

    const [loading, setLoading] = useState(false);
    const [searchValue, setSearchValue] = useState('');
    const [selectValue, handleSelectValue] = useState<Key[]>([]);
    const [menuData, setMenuData] = useState<{ menus: SysMenu[], selectedMenuIds: number[] }>({
        menus: [],
        selectedMenuIds: [],
    });

    useEffect(()=> {
        setLoading(true)
        open && queryDataByParams(API_CONSTANTS.ROLE_MENU_LIST, {id: values.id}).then((res) => setMenuData(res))
        setLoading(false)
    },[values,open])


    /**
     * close
     * @type {() => void}
     */
    const handleClose = useCallback( () => {
        onClose()
        setMenuData({menus:[],selectedMenuIds: []})
    }, [values]);

    /**
     * when submit menu , use loading
     */
    const handleSubmit = async () => {
        setLoading(true)
        await onSubmit(selectValue)
        setLoading(false)
        setMenuData({menus:[],selectedMenuIds: []})
    }




    /**
     * render extra buttons
     * @returns {JSX.Element}
     */
    const renderExtraButtons = () => {
        return <>
            <Space>
                <Button onClick={handleClose}>{l('button.cancel')}</Button>
                <Button type="primary" loading={loading} onClick={handleSubmit}>{l('button.submit')}</Button>
            </Space>
        </>
    }

    /**
     * search tree node
     * @type {(e: {target: {value: React.SetStateAction<string>}}) => void}
     */
    const onSearchChange = useCallback((e: { target: { value: React.SetStateAction<string>; }; }) => {
        setSearchValue(e.target.value)
    }, [searchValue])

    const onCheck = (selectKeys: any) => {
        handleSelectValue(selectKeys)
    };


    return <>
        <Drawer
            title={l('role.assignMenu','',{roleName: values.roleName})}
            open={open}
            width={'55%'}
            maskClosable={false}
            onClose={handleClose}
            extra={renderExtraButtons()}
        >
            {
                (menuData.menus.length > 0) ?
                    <>
                        <Input
                            placeholder={l('global.search.text')}
                            allowClear
                            style={{marginBottom: 16}}
                            value={searchValue}
                            onChange={onSearchChange}
                        />
                        <Spin spinning={loading}>
                            <DirectoryTree
                                selectable defaultCheckedKeys={[...menuData.selectedMenuIds]}
                                checkable defaultExpandAll
                                onCheck={(keys) => onCheck(keys)}
                                multiple={true}
                                className={'treeList'}
                                treeData={buildMenuTree(menuData.menus, searchValue)}
                            />
                        </Spin>
                    </> : <Empty className={'code-content-empty'}/>
            }

        </Drawer>
    </>
}

export default AssignMenu

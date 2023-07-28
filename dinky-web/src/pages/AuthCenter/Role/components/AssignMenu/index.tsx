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
import {Button, Drawer, Empty, Input, Space, Tree} from "antd";
import {l} from "@/utils/intl";
import {UserBaseInfo} from "@/types/User/data";
import {queryDataByParams} from "@/services/BusinessCrud";
import {buildMenuTreeData} from "@/pages/AuthCenter/Role/components/AssignMenu/function";
import {Menu} from "@/types/RegCenter/data";
import {TableOutlined} from "@ant-design/icons";
import {random} from "lodash";

type AssignMenuProps = {
    values: Partial<UserBaseInfo.Role>,
    open: boolean,
    onClose: () => void
    onSubmit: (value: any) => void
}

const {DirectoryTree} = Tree;


const buildCc = (parentId : number) => {
    let cc: Menu[] = []
    for (let j = 0; j < 10; j++) {
        cc.push({
            id: j,
            name: `ssasss${j}`,
            parentId: random(1, 10),
            component: "",
            createTime: new Date(),
            enabled: false,
            icon: <><TableOutlined/></>,
            isFrame: false,
            menuType: "",
            orderNum: 0,
            path: "/api" + j,
            perms: "",
            status: "",
            superFlag: 0,
            updateTime: new Date(),
            visible: "",
            childrens: []
        })
    }
    return cc
}

const mockData = () => {
    let menu : Menu[] =[]
    for (let i = 0; i < 10; i++) {
        menu.push({
            id: i,
            name: `name${i}`,
            parentId: random(1,10),
            component: "",
            createTime: new Date(),
            enabled: false,
            icon: <><TableOutlined/></>,
            isFrame: false,
            menuType: "",
            orderNum: 0,
            path: "/api" + i,
            perms: "",
            status: "",
            superFlag: 0,
            updateTime: new Date(),
            visible: "",
            childrens: buildCc(i)
        })
    }
    return menu
}



const AssignMenu: React.FC<AssignMenuProps> = (props) => {

    const {open, onClose, onSubmit , values} = props

    const [searchValue, setSearchValue] = useState('');
    const [menuData, setMenuData] = useState<Menu[]>(mockData());

    useEffect(()=>{
        // todo: 获取菜单列表 通过角色 id
        // queryDataByParams('/api/menu/listByRoleId', values.id).then((res)=>setMenuData(res))
    },[values])


    /**
     * render extra buttons
     * @returns {JSX.Element}
     */
    const renderExtraButtons = () => {
        return <>
            <Space>
                <Button onClick={onClose}>Cancel</Button>
                <Button type="primary" onClick={onSubmit}>OK</Button>
            </Space>
        </>
    }

    /**
     * search tree node
     * @type {(e: {target: {value: React.SetStateAction<string>}}) => void}
     */
    const onSearchChange = useCallback((e: { target: { value: React.SetStateAction<string>; }; }) => {
        setSearchValue(e.target.value)
    },[searchValue])

    const onCheck = (checkedKeys: any, e: any) => {
        console.log(checkedKeys, e,'0000')
    }


    return <>
        <Drawer
            title={'分配菜单权限'}
            open={open}
            width={'45%'}
            maskClosable={false}
            onClose={onClose}
            extra={renderExtraButtons()}
        >
            {
                (menuData.length > 0) ?
                    <>
                        <Input
                            placeholder={l('global.search.text')}
                            allowClear
                            style={{marginBottom: 8}}
                            value={searchValue}
                            onChange={onSearchChange}
                        />
                        <DirectoryTree
                            // expandedKeys={expandKeys}
                            // selectedKeys={selectKeys}
                            // onExpand={(keys) => onExpand(keys)}
                            checkable defaultExpandAll
                            onCheck={onCheck}
                            multiple={true}
                            className={'treeList'}
                            // onSelect={onNodeClick}
                            treeData={buildMenuTreeData(menuData, searchValue)}
                        />
                    </> : <Empty className={'code-content-empty'}/>
            }

        </Drawer>
    </>
}

export default AssignMenu
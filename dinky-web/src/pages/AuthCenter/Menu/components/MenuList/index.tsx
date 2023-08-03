/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */


import React, {useEffect, useRef, useState} from "react";
import {ActionType} from "@ant-design/pro-table";
import {Button, Dropdown, Menu, Space} from 'antd';
import {handleAddOrUpdate, handleRemoveById, queryDataByParams} from "@/services/BusinessCrud";
import {API_CONSTANTS} from "@/services/constants";
import {getTenantByLocalStorage} from "@/utils/function";
import {ProCard} from "@ant-design/pro-components";
import {Resizable} from "re-resizable";
import {MenuInfo} from "rc-menu/es/interface";
import {RIGHT_CONTEXT_MENU} from "@/pages/AuthCenter/Menu/components/MenuList/constants";
import {SysMenu} from "@/types/RegCenter/data";
import MenuTree from "@/pages/AuthCenter/Menu/components/MenuTree";
import {PlusSquareTwoTone} from "@ant-design/icons";
import MenuCardForm from "@/pages/AuthCenter/Menu/components/MenuCardForm";


const MenuList: React.FC = () => {
        /**
         * status
         */
        const [formValues, setFormValues] = useState<Partial<SysMenu>>({});
        const [modalVisible, handleModalVisible] = useState<boolean>(false);
        const [updateModalVisible, handleUpdateModalVisible] = useState<boolean>(false);
        const [loading, setLoading] = useState<boolean>(false);
        const [contextMenuPosition, setContextMenuPosition] = useState({});
        const [selectedKeys, setSelectedKeys] = useState([]);
        const [contextMenuVisible, setContextMenuVisible] = useState(false);
        const [rightClickedNode, setRightClickedNode] = useState<any>();
        const [clickedNode, setClickedNode] = useState({});
        const [treeData, setTreeData] = useState<SysMenu[]>([]);

        /**
         * query
         */
        const queryMenuData = async () => {
          await  queryDataByParams('/api/menu/listMenus').then(res => setTreeData(res))
        }

        useEffect(() => {
            queryMenuData()
        }, []);

        const actionRef = useRef<ActionType>();

        const executeAndCallbackRefresh = async (callback: () => void) => {
            setLoading(true);
            await callback();
            setLoading(false);
            actionRef.current?.reload?.();
        }

        /**
         * delete role by id
         * @param id role id
         */
        const handleDeleteSubmit = async (id: number) => {
            await executeAndCallbackRefresh(async () => {
                await handleRemoveById(API_CONSTANTS.ROLE_DELETE, id);
            });
        }

        /**
         * add or update role submit callback
         * @param value
         */
        const handleAddOrUpdateSubmit = async (value: any) => {
            await executeAndCallbackRefresh(async () => {
                // TODO: added or update role interface is use /api/role/addedOrUpdateRole  , because of the backend interface 'saveOrUpdate' is repeat , in the future, we need to change the interface to /api/role (ROLE)
                await handleAddOrUpdate(API_CONSTANTS.ROLE_ADDED_OR_UPDATE, {
                    ...value,
                    tenantId: getTenantByLocalStorage()
                });
                handleModalVisible(false);
            });
        }


        /**
         * edit role status
         * @param record
         */
        const handleEditVisible = (record: Partial<SysMenu>) => {
            setFormValues(record);
            handleUpdateModalVisible(true);
        }

        /**
         * cancel
         */
        const handleCancel = () => {
            handleModalVisible(false);
            handleUpdateModalVisible(false);
        }

        const handleMenuClick = (node: MenuInfo) => {
            switch (node.key) {
                case 'createFolder':
                    // handleCreateFolder();
                    break;
                default:
                    break;
            }
        };

        /**
         * the right click event
         * @param info
         */
        const handleRightClick = (info: any) => {
            // 获取右键点击的节点信息
            const {node, event} = info;
            setSelectedKeys([node.key] as any);
            setRightClickedNode(node);
            setContextMenuVisible(true);
            setContextMenuPosition({
                position: 'fixed',
                cursor: 'context-menu',
                width: '10vw',
                left: event.clientX + 20, // + 20 是为了让鼠标不至于在选中的节点上 && 不遮住当前鼠标位置
                top: event.clientY + 20, // + 20 是为了让鼠标不至于在选中的节点上
                zIndex: 888,
            });
        };
        /**
         * render the right click menu
         * @returns {JSX.Element}
         */
        const renderRightClickMenu = () => {
            const menu = <Menu onClick={handleMenuClick} items={RIGHT_CONTEXT_MENU()}/>
            return <>
                <Dropdown
                    arrow
                    trigger={['contextMenu']}
                    overlayStyle={{...contextMenuPosition}}
                    overlay={menu}
                    open={contextMenuVisible}
                    onVisibleChange={setContextMenuVisible}
                >
                    {/*占位*/}
                    <div style={{...contextMenuPosition}}/>
                </Dropdown>
            </>
        }

        const handleNodeClick = async (info: any) => {
            const {node: {id, isLeaf, key,}, node} = info;
            setSelectedKeys([key] as any);
            setClickedNode(node);
            if (isLeaf) {
                // await queryContent(id);
            } else {
                // setContent('');
            }
        };

        /**
         * render
         */
        return <>
            <ProCard size={'small'}>
                <Resizable
                    defaultSize={{
                        width: 500,
                        height: '100%'
                    }}
                    minWidth={500}
                    maxWidth={1200}
                >
                    <ProCard
                        extra={[
                            <Button key={'added-menu'} icon={<PlusSquareTwoTone/>} type={'primary'} onClick={() => handleModalVisible(true)}>新增根菜单</Button>,
                        ]}
                        title={'菜单列表'}
                        ghost hoverable colSpan={'18%'} className={"siderTree schemaTree"}>
                        <MenuTree
                            selectedKeys={selectedKeys}
                            treeData={treeData}
                            onRightClick={handleRightClick}
                            onNodeClick={(info: any) => handleNodeClick(info)}
                        />
                        {contextMenuVisible && renderRightClickMenu()}
                    </ProCard>
                </Resizable>
                <ProCard.Divider type={"vertical"}/>
                <ProCard
                    extra={
                    <>
                        {
                            (!updateModalVisible && formValues.id)  && <Button type={'primary'} onClick={() => handleModalVisible(true)}>编辑</Button>
                        }
                    </>
                    }
                    title={ (formValues.id && updateModalVisible) ? '修改菜单' :  (formValues.id && modalVisible) ? '新增菜单' : ''}
                    ghost hoverable className={"schemaTree"}>
                    {
                        (formValues.id && updateModalVisible) &&
                        <MenuCardForm modalVisible={updateModalVisible} values={formValues} onCancel={handleCancel}
                            onSubmit={(value: any) => handleAddOrUpdateSubmit(value)}/>
                    }
                    {
                        (!formValues.id && modalVisible) &&
                        <MenuCardForm modalVisible={updateModalVisible} values={formValues} onCancel={handleCancel}
                                      onSubmit={(value: any) => handleAddOrUpdateSubmit(value)}/>

                    }
                </ProCard>
            </ProCard>
        </>
}
;

export default MenuList;

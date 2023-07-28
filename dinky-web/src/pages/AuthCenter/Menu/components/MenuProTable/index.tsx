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


import React, {useRef, useState} from "react";
import ProTable, {ActionType, ProColumns} from "@ant-design/pro-table";
import {Tag} from 'antd';
import {l} from "@/utils/intl";
import {handleAddOrUpdate, handleRemoveById} from "@/services/BusinessCrud";
import {queryList} from "@/services/api";
import {API_CONSTANTS, PROTABLE_OPTIONS_PUBLIC} from "@/services/constants";
import {getTenantByLocalStorage} from "@/utils/function";
import {CreateBtn} from "@/components/CallBackButton/CreateBtn";
import {PopconfirmDeleteBtn} from "@/components/CallBackButton/PopconfirmDeleteBtn";
import {EditBtn} from "@/components/CallBackButton/EditBtn";
import {Menu} from "@/types/RegCenter/data";
import MenuModalForm from "@/pages/AuthCenter/Menu/components/MenuModalForm";


const MenuProTable: React.FC = () => {
        /**
         * status
         */
        const [formValues, setFormValues] = useState<Partial<Menu>>({});
        const [modalVisible, handleModalVisible] = useState<boolean>(false);
        const [updateModalVisible, handleUpdateModalVisible] = useState<boolean>(false);
        const [loading, setLoading] = useState<boolean>(false);

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
                await handleAddOrUpdate(API_CONSTANTS.ROLE_ADDED_OR_UPDATE, {...value, tenantId: getTenantByLocalStorage()});
                handleModalVisible(false);
            });
        }


        const handleAssignMenuSubmit = async (value: any) => {
            // TODO : SAVE DATA
            setAssignMenu(false)
        }



        /**
         * edit role status
         * @param record
         */
        const handleEditVisible = (record: Partial<UserBaseInfo.Role>) => {
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



        /**
         * columns
         */
        const columns: ProColumns<Menu>[] = [
          {
            title: 'index',
            valueType: 'indexBorder',
          },
          {
            title: l('menu.id'),
            dataIndex: 'id',
          },
          {
            title: l('menu.name'),
            dataIndex: 'name',
          },
          {
            title: l('menu.type'),
            dataIndex: 'type',
          },
          {
            title: l('menu.path'),
            dataIndex: 'path',
          },
          {
            title: l('menu.component'),
            dataIndex: 'component',
          },
          {
            title: l('menu.icon'),
            dataIndex: 'icon',
          },
          {
              title: l('global.table.operate'),
              valueType: 'option',
              width: "10vh",
              render: (_:any, record: Menu) => [
                  <EditBtn key={`${record.id}_edit`} onClick={() => handleEditVisible(record)}/>,
                  <>{record.id !== 1 &&
                      <PopconfirmDeleteBtn key={`${record.id}_delete`} onClick={() => handleDeleteSubmit(record.id)}
                                           description={l("role.deleteConfirm")}/>}
                  </>
              ],
          },
        ];


        /**
         * render
         */
        return <>
            <ProTable<Menu>
                {...PROTABLE_OPTIONS_PUBLIC}
                headerTitle={l('menu.management')}
                actionRef={actionRef}
                loading={loading}
                toolBarRender={() => [<CreateBtn key={"toolBarRender"} onClick={() => handleModalVisible(true)}/>,]}
                request={(params, sorter, filter: any) => queryList(API_CONSTANTS.MENU, {...params, sorter, filter})}
                columns={columns}
            />
            {/* create  */}
            <MenuModalForm
                onSubmit={(value: any) => handleAddOrUpdateSubmit(value)}
                onCancel={() => handleCancel()}
                modalVisible={modalVisible}
                values={{}}
            />
            {/* modify */}
            <MenuModalForm
                onSubmit={(value: any) => handleAddOrUpdateSubmit(value)}
                onCancel={() => handleCancel()}
                modalVisible={updateModalVisible}
                values={formValues}
            />

        </>
    }
;

export default MenuProTable;

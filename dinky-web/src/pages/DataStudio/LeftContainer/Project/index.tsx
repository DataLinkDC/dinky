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


import React, {Key, useState} from "react";
import JobTree from "@/pages/DataStudio/LeftContainer/Project/JobTree";
import {connect} from "umi";
import {StateType} from "@/pages/DataStudio/model";
import {getTaskDetails} from "@/pages/DataStudio/LeftContainer/Project/service";
import RightContextMenu from "@/components/RightContextMenu";
import {MenuInfo} from "rc-menu/es/interface";
import {FOLDER_RIGHT_MENU, JOB_RIGHT_MENU} from "@/pages/DataStudio/LeftContainer/Project/constants";
import {MenuItemType} from "antd/es/menu/hooks/useItems";
import {handleAddOrUpdate, handleOption, handleRemoveById} from "@/services/BusinessCrud";
import {Catalogue} from "@/types/Studio/data";
import {Modal, Typography} from "antd";
import {l} from "@/utils/intl";
import FolderModal from "@/pages/DataStudio/LeftContainer/Project/FolderModal";
import JobModal from "@/pages/DataStudio/LeftContainer/Project/JobModal";

const {Text} = Typography;


const Project: React.FC = (props: connect) => {
    const {dispatch} = props;

    const [rightActiveKey, setRightActiveKey] = useState<string>('');
    const [contextMenu, setContext] = useState<{
        visible: boolean;
        position: any;
        item: MenuItemType[];
        selectedKeys: Key[];
        rightClickedNode?: any;
    }>({
        visible: false,
        position: {
            position: 'fixed',
            cursor: 'context-menu',
            width: '12vw',
            left: 0,
            top: 0,
            zIndex: 999, // 搞大点, 用来置顶
        },
        item: [],
        selectedKeys: [],
        rightClickedNode: null,
    });

    const [modalAllVisible, setModalAllVisible] = useState<{
        isCreateSub: boolean;
        isEdit: boolean;
        isRename: boolean;
        isCreateTask: boolean;
        value: any;
    }>({
        isCreateSub: false,
        isEdit: false,
        isRename: false,
        value: {},
        isCreateTask: false,
    })

    /**
     * the right click event
     * @param info
     */
    const handleRightClick = (info: any) => {
        const {node: {isLeaf, key, fullInfo}, node, event} = info;
        // 设置右键菜单
        setContext((prevState) => ({
            ...prevState,
            visible: true,
            position: {
                ...prevState.position,
                left: event.clientX + 10,
                top: event.clientY + 5,
            },
            selectedKeys: [key],
            rightClickedNode: node,
            item: isLeaf ? JOB_RIGHT_MENU : FOLDER_RIGHT_MENU
        }))
        setModalAllVisible((prevState) => ({...prevState, value: fullInfo}))
    };


    /**
     * click the node event
     * @param info
     */
    const onNodeClick = (info: any) => {
        // 选中的key
        const {node: {isLeaf, name, type, parentId, path, key, taskId}} = info;
        setContext((prevState) => ({
            ...prevState,
            visible: false,
            selectedKeys: [key],
        }));
        if (isLeaf) {
            // const queryParams =  {id: selectDatabaseId , schemaName, tableName};
            getTaskDetails(taskId).then(res => {
                path.pop()
                dispatch({
                    type: "Studio/addTab",
                    payload: {
                        icon: type,
                        id: parentId + name,
                        breadcrumbLabel: path.join("/"),
                        label: name,
                        params: {taskId: taskId, taskData: {...res, useResult: true, maxRowNum: 100}},
                        type: "project",
                        subType: type.toLowerCase()
                    }
                })
            })
        }
    };

    const handleContextCancel = () => {
        setContext((prevState) => ({
            ...prevState,
            visible: false,
        }));
    };

    /**
     * create or update sub folder
     */
    const handleCreateSubFolder = () => {
        setModalAllVisible((prevState) => ({...prevState, isCreateSub: true,}));
        handleContextCancel();
    };

    /**
     * 创建目录, 并刷新目录树
     * @param {Catalogue} values
     * @returns {Promise<void>}
     */
    const handleSubmit = async (values: Catalogue) => {
        let options = {
            url: '',
            isLeaf: modalAllVisible.isCreateSub ? false : modalAllVisible.value.isLeaf,
            parentId: modalAllVisible.isCreateSub ? contextMenu.selectedKeys[0] : modalAllVisible.value.parentId,
        }
        if (rightActiveKey === 'createTask') {
            options.url = '/api/catalogue/createTask'
            options.parentId = modalAllVisible.isCreateTask && modalAllVisible.value.id
        } else {
            options.url = '/api/catalogue/saveOrUpdateCatalogue'
        }
        await handleAddOrUpdate(options.url, {
            ...values,
            isLeaf: options.isLeaf,
            parentId: options.parentId,
        }, () => {
            setModalAllVisible((prevState) => ({...prevState, isCreateSub: false, isRename: false, isEdit: false,isCreateTask: false}));
            dispatch({type: 'Studio/queryProject'});
            if (modalAllVisible.isRename) {
                // todo: 如果是重命名/修改(修改了名字), 则需要 tag

            }
        });

    };

    /**
     * 删除目录, 并刷新目录树
     * @param {MenuInfo} node
     * @returns {Promise<void>}
     */
    const handleDeleteSubmit = async () => {
        const {key, isLeaf, name, type,taskId} = contextMenu.rightClickedNode;
        handleContextCancel();
        if (!isLeaf) {
            await handleRemoveById('/api/catalogue/deleteCatalogueById', key, () => {
                dispatch({type: 'Studio/queryProject'});
                // TODO: 如果打开的 tag 中包含了这个 key 则更新 dav 的 tag 数据 删除此项
            });
        } else {
            const renderContent = () => {
                return <>
                    <Text className={'needWrap'} type="danger">
                        此操作会将该任务的执行历史, 以及任务的所有信息全部删除.{'\n'}
                        请谨慎操作! 该操作不可逆!!!{'\n'}
                    </Text>
                    确认删除吗?
                </>
            };

            Modal.confirm({
                title: '删除 [' + type + '] 作业 [' + name + ']',
                width: '30%',
                content: renderContent(),
                okText: l('button.confirm'),
                cancelText: l('button.cancel'),
                onOk: async () => {
                    await handleRemoveById('/api/catalogue/deleteCatalogueById', key, () => {
                        // TODO: 如果打开的 tag 中包含了这个 key 则更新 dav 的 tag 数据 删除此项
                        dispatch({type: 'Studio/removeTag', payload: taskId});
                        dispatch({type: 'Studio/queryProject'});
                    });
                },
            });
        }
    }


    /**
     * rename
     * @returns {Promise<void>}
     */
    const handleRename = async () => {
        setModalAllVisible(prevState => ({...prevState, isRename: true}));
        handleContextCancel();
    }

    const handleCreateTask = () => {
        setModalAllVisible(prevState => ({...prevState, isCreateTask: true}));
        handleContextCancel();
    };

    const handleEdit = () => {
        setModalAllVisible(prevState => ({...prevState, isEdit: true}));
        handleContextCancel();
    }

    const handleCopy = async () => {
       await handleOption('/api/catalogue/copyTask', '拷贝',{
              ...modalAllVisible.value,
       },()=>{
           dispatch({type: 'Studio/queryProject'});
       });
        handleContextCancel();
    }

    const handleMenuClick = async (node: MenuInfo) => {
        setRightActiveKey(node.key);
        switch (node.key) {
            case 'addSubFolder':
                await handleCreateSubFolder();
                break;
            case 'createTask':
                await handleCreateTask();
                break;
            case 'delete':
                await handleDeleteSubmit();
                break;
            case 'renameFolder':
                await handleRename();
                break;
            case 'edit':
                await handleEdit();
                break;
            case 'exportJson':
                // await handleCancel();
                break;
            case 'copy':
                await handleCopy();
                break;
            case 'cut':
                // await handleCancel();
                break;
            case 'paste':
                // await handleCancel();
                break;
            default:
                await handleContextCancel();
                break;
        }
    };


    return (
        <div style={{paddingInline: 5}}>
            <JobTree selectedKeys={contextMenu.selectedKeys} onRightClick={handleRightClick}
                     onNodeClick={(info: any) => onNodeClick(info)} treeData={props.data}/>
            <RightContextMenu
                contextMenuPosition={contextMenu.position} open={contextMenu.visible}
                openChange={() => setContext((prevState) => ({...prevState, visible: false}))} items={contextMenu.item}
                onClick={handleMenuClick}
            />
            {/*  added  sub folder  */}
            <FolderModal
                title={l('right.menu.createSubFolder')}
                values={{}}
                modalVisible={modalAllVisible.isCreateSub}
                onCancel={() => setModalAllVisible((prevState) => ({...prevState, isCreateSub: false,}))}
                onSubmit={handleSubmit}
            />

            {/*  rename  */}
            <FolderModal
                title={l('right.menu.rename')}
                values={modalAllVisible.value}
                modalVisible={modalAllVisible.isRename}
                onCancel={() => setModalAllVisible((prevState) => ({...prevState, isRename: false,}))}
                onSubmit={handleSubmit}
            />

            {/*  create task  */}
            <JobModal
                title={l('right.menu.createTask')}
                values={{}}
                modalVisible={modalAllVisible.isCreateTask}
                onCancel={() => setModalAllVisible((prevState) => ({...prevState, isCreateTask: false,}))}
                onSubmit={handleSubmit}
            />
            {/*  edit task  */}
            <JobModal
                title={l('button.edit')}
                values={modalAllVisible.value}
                modalVisible={modalAllVisible.isEdit}
                onCancel={() => setModalAllVisible((prevState) => ({...prevState, isEdit: false,}))}
                onSubmit={handleSubmit}
            />

        </div>
    )
};

export default connect(({Studio}: { Studio: StateType }) => ({
    data: Studio.project.data,
    tabs: Studio.tabs,
}))(Project);

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


import React, {Key, useEffect, useState} from "react";
import JobTree from "@/pages/DataStudio/LeftContainer/Project/JobTree";
import {connect} from "umi";
import {StateType, STUDIO_MODEL, STUDIO_MODEL_SYNC} from "@/pages/DataStudio/model";
import {getTaskDetails} from "@/pages/DataStudio/LeftContainer/Project/service";
import RightContextMenu from "@/components/RightContextMenu";
import {MenuInfo} from "rc-menu/es/interface";
import {FOLDER_RIGHT_MENU, JOB_RIGHT_MENU} from "@/pages/DataStudio/LeftContainer/Project/constants";
import {MenuItemType} from "antd/es/menu/hooks/useItems";
import {handleAddOrUpdate, handleOption, handlePutDataByParams, handleRemoveById} from "@/services/BusinessCrud";
import {Catalogue} from "@/types/Studio/data";
import {Modal, Typography} from "antd";
import {l} from "@/utils/intl";
import FolderModal from "@/pages/DataStudio/LeftContainer/Project/FolderModal";
import JobModal from "@/pages/DataStudio/LeftContainer/Project/JobModal";
import {
  API_CONSTANTS
} from "@/services/constants";

const {Text} = Typography;


const Project: React.FC = (props: connect) => {
    const {dispatch} = props;

    const [rightActiveKey, setRightActiveKey] = useState<string>('');
    const [cutId , setCutId] = useState<number>();
    const [contextMenu, setContextMenu] = useState<{
        visible: boolean;
        position: any;
        item: MenuItemType[];
        selectedKeys: Key[];
        isLeaf: boolean;
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
        isLeaf: false,
        item: [],
        selectedKeys: [],
        rightClickedNode: null,
    });

    const [modalAllVisible, setModalAllVisible] = useState<{
        isCreateSub: boolean;
        isEdit: boolean;
        isRename: boolean;
        isCreateTask: boolean;
        isCut: boolean;
        value: any;
    }>({
        isCreateSub: false,
        isEdit: false,
        isRename: false,
        value: {},
        isCreateTask: false,
        isCut: false,
    })

    useEffect(() => {
        setContextMenu((prevState) => ({
            ...prevState,
            item: prevState.isLeaf ?
              JOB_RIGHT_MENU(modalAllVisible.isCut && cutId !== undefined) :
              FOLDER_RIGHT_MENU(modalAllVisible.isCut && cutId !== undefined)
        }))
    }, [modalAllVisible.isCut,cutId])


    /**
     * the right click event
     * @param info
     */
    const handleRightClick = (info: any) => {
        const {node: {isLeaf, key, fullInfo}, node, event} = info;
        // 设置右键菜单
        setContextMenu((prevState) => ({
            ...prevState,
            visible: true,
            position: {
                ...prevState.position,
                left: event.clientX + 10,
                top: event.clientY + 5,
            },
            isLeaf: isLeaf,
            selectedKeys: [key],
            rightClickedNode: {...node, ...fullInfo},
            item: isLeaf ? JOB_RIGHT_MENU(modalAllVisible.isCut) : FOLDER_RIGHT_MENU(modalAllVisible.isCut)
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
      setContextMenu((prevState) => ({
            ...prevState,
            visible: false,
            selectedKeys: [key],
        }));

      if (!isLeaf) {
        return;
      }

      getTaskDetails(taskId).then(res => {
        path.pop()
        dispatch({
          type: STUDIO_MODEL.addTab,
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
    };

    const handleContextCancel = () => {
        setContextMenu((prevState) => ({
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
        const options = {
            url: '',
            isLeaf: modalAllVisible.isCreateSub ? false : modalAllVisible.value.isLeaf,
            parentId: modalAllVisible.isCreateSub ? contextMenu.selectedKeys[0] : modalAllVisible.value.parentId,
        }

        if (rightActiveKey === 'createTask') {
            options.url = API_CONSTANTS.CREATE_TASK_URL
            options.parentId = modalAllVisible.isCreateTask && modalAllVisible.value.id
        } else {
            options.url = API_CONSTANTS.SAVE_OR_UPDATE_CATALOGUE_URL
        }

        await handleAddOrUpdate(options.url, {
            ...values,
            isLeaf: options.isLeaf,
            parentId: options.parentId,
        }, () => {
            setModalAllVisible((prevState) => ({...prevState, isCreateSub: false, isRename: false, isEdit: false,isCreateTask: false,isCut: false}));
            dispatch({type: STUDIO_MODEL_SYNC.queryProject});
            if (modalAllVisible.isRename) {
                // todo: 如果是重命名/修改(修改了名字), 则需要 更新 tab 的 label

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
        await handleRemoveById(API_CONSTANTS.DELETE_CATALOGUE_BY_ID_URL, key, () => {
          dispatch({type: STUDIO_MODEL_SYNC.queryProject});
          // TODO: 如果打开的 tag 中包含了这个 key 则更新 dav 的 tag 数据 删除此项 && 有一个 bug Dinky/src/pages/DataStudio/RightContainer/JobInfo/index.tsx:55 -> Cannot read properties of undefined (reading 'id')
        });
        return;
      }

      const renderContent = () => {
        return (
          <Text className={'needWrap'} type="danger">
            {l('datastudio.project.delete.job.confirm')}
          </Text>);
      };

      Modal.confirm({
        title: l('datastudio.project.delete.job', '', {type, name}),
        width: '30%',
        content: renderContent(),
        okText: l('button.confirm'),
        cancelText: l('button.cancel'),
        onOk: async () => {
          await handleRemoveById(API_CONSTANTS.DELETE_CATALOGUE_BY_ID_URL, key, () => {
            // TODO: 如果打开的 tag 中包含了这个 key 则更新 dav 的 tag 数据 删除此项
            dispatch({type: STUDIO_MODEL.removeTag, payload: taskId});
            dispatch({type: STUDIO_MODEL_SYNC.queryProject});
          });
        },
      });
    }


    /**
     * rename handle
     * @returns {Promise<void>}
     */
    const handleRename = async () => {
        setModalAllVisible(prevState => ({...prevState, isRename: true}));
        handleContextCancel();
    }

    /**
     * create task handle
     */
    const handleCreateTask = () => {
        setModalAllVisible(prevState => ({...prevState, isCreateTask: true}));
        handleContextCancel();
    };

    /**
     * edit task handle
     */
    const handleEdit = () => {
        setModalAllVisible(prevState => ({...prevState, isEdit: true}));
        handleContextCancel();
    }

    /**
     * copy task handle and submit to server and refresh the tree
     * @returns {Promise<void>}
     */
    const handleCopy = async () => {
       await handleOption(API_CONSTANTS.COPY_TASK_URL, l('right.menu.copy'),{
              ...modalAllVisible.value,
       },()=>{
           dispatch({type: STUDIO_MODEL_SYNC.queryProject});
       });
        handleContextCancel();
    }

    /**
     * cut task handle
     * @returns {Promise<void>}
     */
    const handleCut = async () => {
        setModalAllVisible(prevState => ({...prevState, isCut: true}));
        setCutId(contextMenu.rightClickedNode.key);
        handleContextCancel();
    }

    /**
     * paste task handle and submit to server and refresh the tree
     * @returns {Promise<void>}
     */
    const handlePaste = async () => {
        await handlePutDataByParams(API_CONSTANTS.MOVE_CATALOGUE_URL, l('right.menu.paste'),{
           originCatalogueId: cutId,
           targetParentId: contextMenu.selectedKeys[0],
        },()=>{
            dispatch({type: STUDIO_MODEL_SYNC.queryProject});
            // 重置 cutId
            setCutId(undefined);
            setModalAllVisible(prevState => ({...prevState, isCut: false}));
        });
        handleContextCancel();
    }

    /**
     *  all context menu click handle
     * @param {MenuInfo} node
     * @returns {Promise<void>}
     */
    const handleMenuClick = async (node: MenuInfo) => {
        setRightActiveKey(node.key);
        switch (node.key) {
            case 'addSubFolder':
                handleCreateSubFolder();
                break;
            case 'createTask':
                handleCreateTask();
                break;
            case 'delete':
                await handleDeleteSubmit();
                break;
            case 'renameFolder':
                await handleRename();
                break;
            case 'edit':
                handleEdit();
                break;
            case 'exportJson':
                // todo: 导出 json
                // await handleCancel();
                break;
            case 'copy':
                await handleCopy();
                break;
            case 'cut':
                await handleCut();
                break;
            case 'paste':
                await handlePaste();
                break;
            default:
                handleContextCancel();
                break;
        }
    };


    return (
        <div style={{paddingInline: 5}}>
            <JobTree selectedKeys={contextMenu.selectedKeys} onRightClick={handleRightClick}
                     onNodeClick={(info: any) => onNodeClick(info)} treeData={props.data}/>
            <RightContextMenu
                contextMenuPosition={contextMenu.position} open={contextMenu.visible}
                openChange={() => setContextMenu((prevState) => ({...prevState, visible: false}))} items={contextMenu.item}
                onClick={handleMenuClick}
            />
            {/*  added  sub folder  */}
            <FolderModal
                title={l('right.menu.createSubFolder')}
                values={{}}
                modalVisible={modalAllVisible.isCreateSub}
                onCancel={() => setModalAllVisible((prevState) => ({...prevState, isCreateSub: false,value: {}}))}
                onSubmit={handleSubmit}
            />

            {/*  rename  */}
            <FolderModal
                title={l('right.menu.rename')}
                values={modalAllVisible.value}
                modalVisible={modalAllVisible.isRename}
                onCancel={() => setModalAllVisible((prevState) => ({...prevState, isRename: false,value: {}}))}
                onSubmit={handleSubmit}
            />

            {/*  create task  */}
            <JobModal
                title={l('right.menu.createTask')}
                values={{}}
                modalVisible={modalAllVisible.isCreateTask}
                onCancel={() => setModalAllVisible((prevState) => ({...prevState, isCreateTask: false,value: {}}))}
                onSubmit={handleSubmit}
            />
            {/*  edit task  */}
            {
                Object.keys(modalAllVisible.value).length > 0 &&
                    <JobModal
                        title={l('button.edit')}
                        values={modalAllVisible.value}
                        modalVisible={modalAllVisible.isEdit}
                        onCancel={() => setModalAllVisible((prevState) => ({...prevState, isEdit: false,value: {}}))}
                        onSubmit={handleSubmit}
                    />
            }
        </div>
    )
};

export default connect(({Studio}: { Studio: StateType }) => ({
    data: Studio.project.data,
    tabs: Studio.tabs,
}))(Project);

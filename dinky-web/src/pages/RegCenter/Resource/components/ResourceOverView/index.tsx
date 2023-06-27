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
import {ProCard} from "@ant-design/pro-components";
import FileTree from "@/pages/RegCenter/Resource/components/FileTree";
import FileShow from "@/pages/RegCenter/Resource/components/FileShow";
import {Dropdown, Menu} from "antd";
import {DeleteOutlined, UploadOutlined} from "@ant-design/icons";
import {MenuInfo} from "rc-menu/es/interface";
import {l} from "@/utils/intl";
import {API_CONSTANTS} from "@/services/constants";
import {queryDataByParams} from "@/services/BusinessCrud";


type RightClickMenu = {
    pageX: number,
    pageY: number,
    id: number,
    name: string
};
const ResourceOverView: React.FC = () => {

    const [treeData, setTreeData] = useState<Partial<any[]>>([]);
    const [rightClickNodeTreeItem, setRightClickNodeTreeItem] = useState<RightClickMenu>();
    const [content, setContent] = useState<string>('');
    const [clickedNode, setClickedNode] = useState<any>({});


    useEffect(() => {
        queryDataByParams(API_CONSTANTS.RESOURCE_SHOW_TREE, {pid: 0}).then(res => {
            setTreeData(res)
        })
    }, [])


    /**
     * query content by id
     * @type {(id: number) => Promise<void>}
     */
    const queryContent = useCallback(async (id: number) => {
        await queryDataByParams(API_CONSTANTS.RESOURCE_GET_CONTENT_BY_ID, {id}).then(res => {
            setContent(res)
        })
    }, [clickedNode])


    /**
     * the node click event
     * @param info
     * @returns {Promise<void>}
     */
    const handleNodeClick = async (info: any) => {
        const {node: {id, isLeaf}, node} = info;
        setClickedNode(node);
        if (isLeaf) {
            await queryContent(id);
        }else {
            setContent('');
        }
    };


    const [rightClickedNode, setRightClickedNode] = useState(null);


    const contextMenuItems = [
        {
            key: 'upload',
            icon: <UploadOutlined/>,
            label: l('button.upload'),
        },
        {
            key: 'delete',
            icon: <DeleteOutlined/>,
            label: l('button.delete'),
        },
    ]
    const handleUpload = () => {
        if (rightClickedNode) {
            // todo: upload
        }
    };


    const handleDelete = () => {
        if (rightClickedNode) {
            // todo: delete
        }
    };
    const handleMenuClick = (node: MenuInfo) => {
        switch (node.key) {
            case 'upload':
                handleUpload();
                break;
            case 'delete':
                handleDelete();
                break;
            default:
                break;
        }
    };

    const renderContextMenu = () => {
        return (
            <Menu
                disabledOverflow
                onClick={handleMenuClick}
                items={contextMenuItems}
            />
        );
    };


    const handleRightClick = (info: any) => {
        // 获取右键点击的节点信息
        const {node: {path, isLeaf}, node, event} = info;

        const {pageX, pageY} = {...rightClickNodeTreeItem};

        setRightClickNodeTreeItem({
            pageX: event.pageX,
            pageY: event.pageY,
            id: node.id,
            name: node.name
        });

        const tmpStyle: any = {
            position: 'fixed',
            left: pageX,
            top: pageY,
        };
        setRightClickedNode(node);

        return <>
            <Dropdown arrow autoAdjustOverflow forceRender open overlayStyle={tmpStyle} overlay={renderContextMenu()}
                      trigger={['contextMenu']}/>
        </>
    };

    /**
     * the content change
     * @param value
     */
    const handleContentChange = (value: any) => {
        setContent(value);
        // todo: save content
    }


    /**
     * render
     */
    return <>
        <ProCard size={'small'}>
            <ProCard ghost hoverable colSpan={'18%'} className={"schemaTree"}>
                <FileTree
                    treeData={treeData}
                    onRightClick={handleRightClick}
                    onNodeClick={(info: any) => handleNodeClick(info)}
                />
            </ProCard>
            <ProCard.Divider type={"vertical"}/>
            <ProCard ghost hoverable className={"schemaTree"}>
                <FileShow
                    onChange={handleContentChange}
                    code={content}
                    item={clickedNode}
                />
            </ProCard>
        </ProCard>
    </>
}

export default ResourceOverView;

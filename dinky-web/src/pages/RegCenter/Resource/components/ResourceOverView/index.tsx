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


import React, {useState} from "react";
import {ProCard} from "@ant-design/pro-components";
import FileTree from "@/pages/RegCenter/Resource/components/FileTree";
import FileShow from "@/pages/RegCenter/Resource/components/FileShow";
import {Dropdown, Menu} from "antd";
import {DeleteOutlined, UploadOutlined} from "@ant-design/icons";
import {MenuInfo} from "rc-menu/es/interface";
import {l} from "@/utils/intl";

const ResourceOverView: React.FC = () => {

    const [treeData, setTreeData] = useState<Partial<any>[]>([]);

    const handleNodeClick = async (info: any) => {
        const {node: {path, isLeaf}} = info;
        if (isLeaf) {
            console.log(path)
            // setClickFileName(path);
            // await queryLogContent(path);
        }
    };


    const [rightClickedNode, setRightClickedNode] = useState(null);

    const handleRightClick = (info: any) => {
        // 阻止默认的右键菜单事件
        info.event.preventDefault();
        // 获取右键点击的节点信息
        const {node} = info;
        setRightClickedNode(node);
    };


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


    const renderContextMenu = () => {
        return (
            <Menu
                disabledOverflow
                onClick={handleMenuClick}
                items={contextMenuItems}
            />
        );
    };


    return <>
        <ProCard>
            <ProCard hoverable colSpan={'18%'} className={"schemaTree"}>
                <Dropdown overlay={renderContextMenu}>
                    <FileTree
                        treeData={treeData}
                        onRightClick={handleRightClick}
                        onNodeClick={(info: any) => handleNodeClick(info)}
                    />
                </Dropdown>
            </ProCard>
            <ProCard.Divider type={"vertical"}/>
            <ProCard hoverable className={"schemaTree"}>
                <FileShow
                    code={''}
                    refreshLogCallback={() => {
                    }}
                />
            </ProCard>
        </ProCard>
    </>
}

export default ResourceOverView;

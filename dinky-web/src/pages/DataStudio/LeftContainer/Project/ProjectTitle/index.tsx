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
import {Space} from "antd";
import Title from "@/components/Front/Title";
import {l} from "@/utils/intl";
import JobModal from "@/pages/DataStudio/LeftContainer/Project/JobModal";
import {connect} from "@umijs/max";
import {StateType} from "@/pages/DataStudio/model";
import {Catalogue} from "@/types/Studio/data";
import {handleAddOrUpdate} from "@/services/BusinessCrud";
import {PlusOutlined} from "@ant-design/icons";
import {CircleBtn} from "@/components/CallBackButton/CircleBtn";

const ProjectTitle: React.FC<StateType & connect> = (props) => {

    const {
        leftContainer: {selectKey},
        dispatch,
    } = props;

    const [createModalVisible, handleModalVisible] = useState<boolean>(false);


    const handleCancelCreate = async () => {
        handleModalVisible(false);

    }

    const handleCreateClick = async () => {
        handleModalVisible(true);
    }

    /**
     * 创建根目录, 并刷新目录树
     * @param {Catalogue} values
     * @returns {Promise<void>}
     */
    const handleSubmit = async (values: Catalogue) => {
        await handleAddOrUpdate('/api/catalogue/saveOrUpdateCatalogue', {
            ...values,
            isLeaf: false,
            parentId: 0,
        }, ()=>{
            handleCancelCreate();
            dispatch({type: 'Studio/queryProject'});
        });
    }


    /**
     * 渲染侧边栏标题
     * @returns {JSX.Element}
     */
    const renderTitle = () => {
        if (selectKey && selectKey === "menu.datastudio.project") {
            return <Space>
                <Title>{l(selectKey)}</Title>
                <CircleBtn title={l("datastudio.project.create.rootFolder")} icon={<PlusOutlined/>}
                           onClick={() => handleCreateClick()}/>
                <JobModal modalVisible={createModalVisible} onCancel={handleCancelCreate} onSubmit={handleSubmit}/>
            </Space>
        } else {
            return <Title>{l(selectKey)}</Title>
        }
    }


    return <>
        {renderTitle()}
    </>
}


export default connect(({Studio}: { Studio: StateType }) => ({
    leftContainer: Studio.leftContainer,
}))(ProjectTitle);
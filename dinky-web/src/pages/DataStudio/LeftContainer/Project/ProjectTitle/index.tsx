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

import Title from '@/components/Front/Title';
import { BtnRoute, useTasksDispatch } from '@/pages/DataStudio/LeftContainer/BtnContext';
import FolderModal from '@/pages/DataStudio/LeftContainer/Project/FolderModal';
import { StateType, STUDIO_MODEL_ASYNC } from '@/pages/DataStudio/model';
import { handleAddOrUpdate } from '@/services/BusinessCrud';
import { API_CONSTANTS } from '@/services/endpoints';
import { Catalogue } from '@/types/Studio/data';
import { l } from '@/utils/intl';
import { connect } from '@umijs/max';
import { Space } from 'antd';
import React, { useState } from 'react';

const ProjectTitle: React.FC<StateType & connect> = (props) => {
  const {
    leftContainer: { selectKey },
    dispatch
  } = props;

  const [createModalVisible, handleModalVisible] = useState<boolean>(false);
  const btnDispatch = useTasksDispatch();

  const handleCancelCreate = async () => {
    handleModalVisible(false);
  };

  const handleCreateClick = async () => {
    handleModalVisible(true);
  };

  /**
   * 创建根目录, 并刷新目录树
   * @param {Catalogue} values
   * @returns {Promise<void>}
   */
  const handleSubmit = async (values: Catalogue) => {
    await handleAddOrUpdate(
      API_CONSTANTS.SAVE_OR_UPDATE_CATALOGUE_URL,
      {
        ...values,
        isLeaf: false,
        parentId: 0
      },
      () => {},
      () => {
        handleCancelCreate();
        dispatch({ type: STUDIO_MODEL_ASYNC.queryProject });
      }
    );
  };

  const currentTabName = 'menu.datastudio.project';
  const btn = BtnRoute[currentTabName];
  btn[0].onClick = () => handleCreateClick();
  btnDispatch({
    type: 'change',
    selectKey: currentTabName,
    payload: btn
  });

  /**
   * 渲染侧边栏标题
   * @returns {JSX.Element}
   */
  const renderTitle = () => {
    if (selectKey && selectKey === currentTabName) {
      return (
        <Space>
          <Title>{l(selectKey)}</Title>
          <FolderModal
            title={l('right.menu.createRoot')}
            modalVisible={createModalVisible}
            onCancel={handleCancelCreate}
            onSubmit={handleSubmit}
            values={{}}
          />
        </Space>
      );
    } else {
      return <Title>{l(selectKey)}</Title>;
    }
  };

  return <>{renderTitle()}</>;
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  leftContainer: Studio.leftContainer
}))(ProjectTitle);

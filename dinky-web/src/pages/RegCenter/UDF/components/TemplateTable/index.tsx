/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {ProTable} from "@ant-design/pro-components";
import React, {useRef, useState} from "react";
import {l} from "@/utils/intl";
import {API_CONSTANTS, PROTABLE_OPTIONS_PUBLIC} from "@/services/constants";
import {ActionType, ProColumns} from "@ant-design/pro-table";
import {UDFTemplate} from "@/types/RegCenter/data";
import {queryList} from "@/services/api";
import {
  CODE_TYPE_ENUM,
  CODE_TYPE_FILTER,
  FUNCTION_TYPE_ENUM,
  FUNCTION_TYPE_FILTER
} from "@/pages/RegCenter/UDF/constants";
import {CreateBtn} from "@/components/CallBackButton/CreateBtn";
import {EditBtn} from "@/components/CallBackButton/EditBtn";
import {PopconfirmDeleteBtn} from "@/components/CallBackButton/PopconfirmDeleteBtn";
import {handleAddOrUpdate, handleRemoveById, updateEnabled} from "@/services/BusinessCrud";
import {EnableSwitchBtn} from "@/components/CallBackButton/EnableSwitchBtn";
import TemplateModal from "@/pages/RegCenter/UDF/components/TemplateModal";
import UDFTemplateDrawer from "../UDFTemplateDrawer";
import CodeShow from "@/components/CustomEditor/CodeShow";


const CodeShowProps = {
  height: "40vh",
  width: "40vw",
  lineNumbers: "on",
  language: "java",
};


const TemplateTable: React.FC = () => {

  const actionRef = useRef<ActionType>();
  const [loading, setLoading] = useState<boolean>(false);
  const [modalVisible, handleModalVisible] = useState<boolean>(false);
  const [updateModalVisible, handleUpdateModalVisible] = useState<boolean>(false);
  const [drawer, setDrawer] = useState<boolean>(false);
  const [formValues, setFormValues] = useState<Partial<UDFTemplate>>({});


  /**
   * execute and callback function
   * @param {() => void} callback
   * @returns {Promise<void>}
   */
  const executeAndCallback = async (callback: () => void) => {
    setLoading(true);
    await callback();
    setLoading(false);
    actionRef.current?.reload?.();
  };

  /**
   * cancel all status
   */
  const handleCancel = () => {
    setFormValues({});
    handleModalVisible(false);
    handleUpdateModalVisible(false);
    setDrawer(false);
    actionRef.current?.reload?.();
  };

  /**
   * change ModalVisible
   * @param value
   */
  const handleEdit = async (value: Partial<UDFTemplate>) => {
    setFormValues(value);
    handleUpdateModalVisible(true);
  };

  /**
   * added or update submit
   * @param {Partial<UDFTemplate>} value
   * @returns {Promise<void>}
   */
  const handleAddOrUpdateSubmit = async (value: Partial<UDFTemplate>) => {
    await handleAddOrUpdate(API_CONSTANTS.UDF_TEMPLATE_ADD_UPDATE, value);
    handleCancel();
  };

  /**
   * update enabled
   * @param {Partial<GitProject>} value
   * @returns {Promise<void>}
   */
  const handleChangeEnable = async (value: Partial<UDFTemplate>) => {
    await executeAndCallback(async () => await updateEnabled(API_CONSTANTS.UDF_TEMPLATE_ENABLE, {id: value.id}));
  };

  /**
   * delete udf template by id
   * @param {number} id
   * @returns {Promise<void>}
   */
  const handleDeleteSubmit = async (id: number) => {
    await executeAndCallback(async () => await handleRemoveById(API_CONSTANTS.UDF_TEMPLATE_DELETE, id));
  };


  /**
   * handle open drawer
   * @param {Partial<UDFTemplate>} record
   */
  const handleOpenDrawer = (record: Partial<UDFTemplate>) => {
    setFormValues(record)
    setDrawer(true)
  }

  const columns: ProColumns<UDFTemplate>[] = [
    {
      title: l("rc.template.name"),
      dataIndex: "name",
      render: (dom, record) => {
        return <a onClick={() => handleOpenDrawer(record)}>{dom}</a>;
      }    },
    {
      title: l("rc.template.codeType"),
      dataIndex: "codeType",
      filters: CODE_TYPE_FILTER,
      valueEnum: CODE_TYPE_ENUM,
      filterMultiple: false,
      onFilter: false,
    },
    {
      title: l("rc.template.functionType"),
      dataIndex: "functionType",
      filters: FUNCTION_TYPE_FILTER,
      valueEnum: FUNCTION_TYPE_ENUM,
      filterMultiple: false,
      onFilter: false,
    },
    {
      title: l("rc.template.templateCode"),
      dataIndex: "templateCode",
      filters: FUNCTION_TYPE_FILTER,
      valueEnum: FUNCTION_TYPE_ENUM,
      hideInTable: true,
      filterMultiple: false,
      onFilter: false,
      render : (dom, record) => {
        return <CodeShow {...CodeShowProps} code={record.templateCode} showFloatButton />;
      }
    },
    {
      title: l("global.table.isEnable"),
      dataIndex: "enabled",
      hideInSearch: true,
      render: (_, record) => {
        return <EnableSwitchBtn key={`${record.id}_enable`} disabled={drawer} record={record}
                                onChange={() => handleChangeEnable(record)}/>;
      },
    },
    {
      title: l("global.table.createTime"),
      dataIndex: "createTime",
      valueType: "dateTime",
      sorter: true,
      hideInSearch: true,
    },
    {
      title: l("global.table.updateTime"),
      dataIndex: "updateTime",
      valueType: "dateTime",
      sorter: true,
      hideInSearch: true,
    },
    {
      title: l("global.table.operate"),
      width: "10vh",
      hideInSearch: true,
      hideInDescriptions: true,
      render: (text, record) => [
        <EditBtn key={`${record.id}_edit`} onClick={() => handleEdit(record)}/>,
        <PopconfirmDeleteBtn key={`${record.id}_delete`} onClick={() => handleDeleteSubmit(record.id)}
                             description={l("rc.template.deleteConfirm")}/>,
      ]
    }
  ];


  return <>
    <ProTable<UDFTemplate>
      {...PROTABLE_OPTIONS_PUBLIC}
      loading={loading}
      actionRef={actionRef}
      headerTitle={l("rc.udf.management")}
      toolBarRender={() => [<CreateBtn key={"template"} onClick={() => handleModalVisible(true)}/>]}
      request={(params, sorter, filter: any) => queryList(API_CONSTANTS.UDF_TEMPLATE, {...params, sorter, filter})}
      columns={columns}
    />
    {/* added */}
    {modalVisible && <TemplateModal values={formValues} visible={modalVisible} onCancel={handleCancel}
                                    onSubmit={(value: Partial<UDFTemplate>) => handleAddOrUpdateSubmit(value)}/>}

    {/* modify */}
    {updateModalVisible && <TemplateModal values={formValues} visible={updateModalVisible} onCancel={handleCancel}
                                          onSubmit={(value: Partial<UDFTemplate>) => handleAddOrUpdateSubmit(value)}/>}

    {/* drawer */}
    {drawer &&
      <UDFTemplateDrawer
      onCancel={() => handleCancel()}
      values={formValues}
      modalVisible={drawer}
      columns={columns}
    />}
  </>;
};

export default TemplateTable;

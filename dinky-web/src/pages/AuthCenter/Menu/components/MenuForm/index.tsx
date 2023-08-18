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

import { FormContextValue } from '@/components/Context/FormContext';
import {
  MENU_ICON_OPTIONS,
  MENU_TYPE_OPTIONS,
} from '@/pages/AuthCenter/Menu/components/MenuList/constants';
import {buildMenuFormTree, sortTreeData} from '@/pages/AuthCenter/Menu/function';
import { FORM_LAYOUT_PUBLIC } from '@/services/constants';
import { SysMenu } from '@/types/RegCenter/data';
import { l } from '@/utils/intl';
import {
  Key,
  ProForm,
  ProFormRadio,
  ProFormSelect,
  ProFormText,
  ProFormTextArea,
  ProFormTreeSelect,
  ProFormDigit
} from '@ant-design/pro-components';
import { Form, Space } from 'antd';
import React, { useEffect, useState } from 'react';
import {Values} from "async-validator";

type MenuFormProps = {
  onCancel: (flag?: boolean) => void;
  onSubmit: (values: SysMenu) => void;
  values: Partial<SysMenu>;
  open: boolean;
  disabled?: boolean;
  selectedKeys: Key[];
  treeData: SysMenu[];
  isRootMenu?: boolean;
  clickNode?: any
};

const MenuForm: React.FC<MenuFormProps> = (props) => {
  const [searchValue, setSearchValue] = useState('');

    /**
     * init props
     */
    const {
        onSubmit: handleSubmit,
        onCancel: handleModalVisible,
        values,
        open,
        disabled = false,
        isRootMenu = false,
        treeData,
        selectedKeys,
        clickNode: {nextOrderNum},
    } = props;


  /**
   * init form
   */
  const [form] = Form.useForm();
  /**
   * init form context
   */
  const formContext = React.useMemo<FormContextValue>(
    () => ({
      resetForm: () => form.resetFields(), // 定义 resetForm 方法
    }),
    [form],
  );



  /**
   * when modalVisible or values changed, set form values
   */
  useEffect(() => {
    if (open) form.resetFields();
    form.setFieldsValue(values);
  }, [open, values, form]);

  /**
   * handle cancel
   */
  const handleCancel = () => {
    handleModalVisible();
    formContext.resetForm();
  };

  /**
   * reset form data
   */
  const handleReset = () => {
    formContext.resetForm();
  };

  /**
   * submit form
   */
  const submitForm = async (formData: SysMenu) => {
    await form.validateFields();
    handleSubmit({ ...values, ...formData });
    handleCancel();
  };


    // const generateOrderNum = (level, baseOrderNum) => {
    //     return baseOrderNum + level * 100;
    // };

    /**
     * 动态构建orderNum , 规则: 按照层级关系 , 从上到下, 递归获取 先获取层级 list 最后一个元素的 orderNum, 然后加上 100
     *
     * @param {SysMenu[]} list
     * @returns {number}
     */
    // const generateTreeWithOrderNum = (tree, level = 0, baseOrderNum = 0) => {
    //     return tree.map((node) => {
    //         const orderNum = generateOrderNum(level, baseOrderNum);
    //         const newNode = { ...node, orderNum };
    //
    //         if (node.children && node.children.length > 0) {
    //             newNode.children = generateTreeWithOrderNum(node.children, level + 1, orderNum);
    //         }
    //
    //         return newNode;
    //     });
    // };


  /**
   * construct role form
   * @constructor
   */
  const renderMenuForm = () => {
    return (
      <>
        <ProFormTreeSelect
          initialValue={selectedKeys}
          shouldUpdate
          name={'parentId'}
          label={l('menu.parentId')}
          rules={[{ required: true, message: l('menu.parentIdPlaceholder') }]}
          placeholder={l('menu.parentIdPlaceholder')}
          fieldProps={{
            labelInValue: false,
            treeData: [{ label: <>Root  <span style={{color: 'grey'}}>&nbsp;&nbsp;&nbsp;Root Folder</span></>, value: '-1' ,children: buildMenuFormTree(sortTreeData(treeData), searchValue, true)}],
            onSearch: (value) => setSearchValue(value),
            treeDefaultExpandAll: true,
          }}
        />
        <ProFormText
          name="name"
          label={l('menu.name')}
          placeholder={l('menu.namePlaceholder')}
          rules={[{ required: true, message: l('menu.namePlaceholder') }]}
        />
        <ProFormText
          name="component"
          label={l('menu.component')}
          placeholder={l('menu.componentPlaceholder')}
        />
        <ProFormText
          name="path"
          label={l('menu.path')}
          placeholder={l('menu.pathPlaceholder')}
          rules={[{ required: true, message: l('menu.pathPlaceholder') }]}
        />

          <ProFormRadio.Group
              label={l('menu.type')}
              name={'type'}
              radioType="button"
              rules={[{ required: true, message: l('menu.typePlaceholder') }]}
              placeholder={l('menu.typePlaceholder')}
              options={MENU_TYPE_OPTIONS}
          />

        <ProFormText
          name="perms"
          label={l('menu.perms')}
          placeholder={l('menu.permsPlaceholder')}
        />

        <ProFormSelect
          addonAfter={
            <a key={'reference'} href={'https://ant.design/components/icon-cn'}>
              {l('menu.icon.reference')}
            </a>
          }
          cacheForSwr
          name="icon"
          allowClear
          showSearch
          mode={'single'}
          width={'md'}
          options={MENU_ICON_OPTIONS()}
          label={l('menu.icon')}
          placeholder={l('menu.iconPlaceholder')}
        />

        <ProFormDigit
          name="orderNum" disabled
          label={l('menu.orderNum')}
          initialValue={nextOrderNum ?? undefined}
        />

        <ProFormTextArea
          name="note"
          label={l('global.table.note')}
          placeholder={l('role.EnterNote')}
          allowClear
        />
      </>
    );
  };

  /**
   * render
   */
  return (
    <>
      <ProForm
        disabled={disabled}
        {...FORM_LAYOUT_PUBLIC}
        form={form}
        initialValues={{ ...values}} // init form data
        onReset={handleReset}
        onFinish={submitForm}
        submitter={{
          render: (_, dom) => <Space style={{ display: 'flex', justifyContent: 'center' }}>{dom}</Space>,
        }}
        layout={'horizontal'}
      >
        {renderMenuForm()}
      </ProForm>
    </>
  );
};
export default MenuForm;

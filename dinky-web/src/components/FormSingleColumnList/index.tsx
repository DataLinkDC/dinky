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

import { DangerDeleteIcon } from '@/components/Icons/CustomIcons';
import { l } from '@/utils/intl';
import { ErrorMessageAsync } from '@/utils/messages';
import { PlusOutlined } from '@ant-design/icons';
import { Button, Divider, Form, Input, Space, Tooltip } from 'antd';
import { FormInstance } from 'antd/es/form/hooks/useForm';
import { Values } from 'async-validator';
import { Rule } from 'rc-field-form/lib/interface';
import React from 'react';

interface FormSingleColumnListProps {
  max: number; // 最多能有多少个
  min: number; // 最少能有多少个
  namePath: string | string[]; // 字段
  rules?: Rule[]; // 校验
  title?: React.ReactNode | string; // 标签
  inputPlaceholder: string; // 输入框提示信息
  form: FormInstance<Values>;
  plain: boolean;
}

export const FormSingleColumnList = (props: FormSingleColumnListProps) => {
  const { max, min, namePath, rules, title, inputPlaceholder, plain, form } = props;

  return (
    <>
      {title && (
        <Divider
          plain={plain}
          orientation={'center'}
          style={{ margin: '5px 0' }}
          type={'horizontal'}
        >
          <Tooltip
            align={{
              autoArrow: true
            }}
            title={'1'}
          >
            {' '}
            {title}
          </Tooltip>
        </Divider>
      )}
      <Form.List initialValue={['']} name={namePath}>
        {(fields, { add, remove }) => (
          <>
            <div style={{ display: 'inline-block', marginRight: '10px' }}>
              {fields.map((field) => (
                <Space
                  key={field.key}
                  align={'baseline'}
                  size={'middle'}
                  style={{ marginInlineEnd: 20 }}
                >
                  <Form.Item
                    validateTrigger={['onChange', 'onBlur', 'onFinish']}
                    name={[field.name]}
                    style={{ width: '100%' }}
                    rules={rules}
                  >
                    <Input placeholder={inputPlaceholder} />
                  </Form.Item>
                  {fields.length === min ? (
                    <></>
                  ) : (
                    <DangerDeleteIcon
                      onClick={async () => {
                        if (fields.length <= min) {
                          await ErrorMessageAsync(`最少必须有[${min}]个`);
                          return;
                        }
                        remove(field.name);
                      }}
                    />
                  )}
                </Space>
              ))}
            </div>
            {fields.length > max ? (
              <></>
            ) : (
              <>
                <Button
                  type='dashed'
                  icon={<PlusOutlined />}
                  onClick={async () => {
                    // 获取前一项的值
                    const lastItem = fields[fields.length === 0 ? 0 : fields.length - 1];
                    const addBeforePreItem = form.getFieldValue([...namePath, lastItem.name]);
                    if (!addBeforePreItem) {
                      await ErrorMessageAsync('前一项必须输入,才能添加下一项');
                      return;
                    } else if (fields.length >= max) {
                      console.log(fields.length);
                      await ErrorMessageAsync(`最多只能有[${max}]个`);
                      return;
                    } else {
                      add();
                    }
                  }}
                  block
                >
                  {l('button.add')}
                </Button>
              </>
            )}
          </>
        )}
      </Form.List>
    </>
  );
};

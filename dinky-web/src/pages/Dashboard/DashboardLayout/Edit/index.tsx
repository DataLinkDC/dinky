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

import { CascaderProps, Modal } from 'antd';
import React, { useEffect, useRef, useState } from 'react';
import { DefaultOptionType } from 'antd/es/select';
import useHookRequest from '@/hooks/useHookRequest';
import { getMetricsLayoutByCascader } from '@/pages/Dashboard/service';
import { ProFormCascader } from '@ant-design/pro-form/lib';
import {
  ProForm,
  ProFormInstance,
  ProFormSegmented,
  ProFormText
} from '@ant-design/pro-components';
import { EchartsOptions, getRandomData, LayoutChartData } from '@/pages/Dashboard/data';
import {
  AreaChartOutlined,
  BarChartOutlined,
  FieldNumberOutlined,
  LineChartOutlined
} from '@ant-design/icons';
import ChartShow from '@/pages/Dashboard/DashboardLayout/ChartShow';
import { l } from '@/utils/intl';

interface EditProps {
  open: boolean;
  chartTheme: string;
  onCancel: () => void;
  onOk: (data: { title: string; layouts: LayoutChartData[] }) => Promise<void>;
  title: string;
  defaultValue?: {
    title: string;
    layouts: LayoutChartData[];
  };
}

export default (props: EditProps) => {
  const {
    open = true,
    title: defaultTitle,
    chartTheme = 'dark',
    defaultValue,
    onCancel,
    onOk
  } = props;

  const filter = (inputValue: string, path: DefaultOptionType[]) =>
    path.some(
      (option) => (option.label as string).toLowerCase().indexOf(inputValue.toLowerCase()) > -1
    );
  const { data, refresh, loading } = useHookRequest<any, any>(getMetricsLayoutByCascader, {
    defaultParams: []
  });

  const [selectOptions, setSelectOptions] = useState<LayoutChartData[]>(
    defaultValue?.layouts ?? []
  );

  const [title, setTitle] = useState(defaultValue?.title || '');
  const form = useRef<ProFormInstance>();

  useEffect(() => {
    setTitle(defaultValue?.title || '');

    setSelectOptions(
      defaultValue?.layouts.map((x) => {
        return {
          ...x,
          data: getRandomData(5)
        };
      }) || []
    );
    form?.current?.setFieldsValue({
      title: defaultValue?.title || '',
      layouts: defaultValue?.layouts?.map((x) => getAllPath(data, x.id)) || []
    });
  }, [defaultValue]);

  const options = [
    { label: 'Line', value: 'Line', icon: <LineChartOutlined /> },
    { label: 'Area', value: 'Area', icon: <AreaChartOutlined /> },
    { label: 'Bar', value: 'Bar', icon: <BarChartOutlined /> }
  ];
  if (selectOptions.length < 2) {
    options.push({ label: 'Statistic', value: 'Statistic', icon: <FieldNumberOutlined /> });
  }

  const onChange: CascaderProps<DefaultOptionType>['onChange'] = (value, selectedOptions) => {
    setSelectOptions(
      selectedOptions
        .filter((x) => x.length === 3)
        .map((x) => {
          const d = x[x.length - 1];
          return {
            type: 'Line',
            name: d.label,
            id: Number.parseInt(d.value),
            data: getRandomData(5)
          };
        })
    );
  };

  return (
    <>
      <Modal
        open={open}
        loading={loading}
        width={'60%'}
        title={defaultTitle}
        onOk={async () =>
          await onOk({
            title: title,
            layouts: selectOptions.map((x) => {
              return { type: x.type, name: x.name, id: x.id };
            })
          })
        }
        onCancel={onCancel}
        onClose={onCancel}
      >
        <ProForm formRef={form} submitter={false} layout={'horizontal'}>
          <ProFormText
            label={l('dashboard.chart.name')}
            name={'title'}
            fieldProps={{
              defaultValue: title,
              onChange: (v) => setTitle(v.currentTarget.value)
            }}
          />
          <ProFormCascader
            fieldProps={{
              onChange: onChange,
              showSearch: { filter },
              options: data,
              // @ts-ignore
              multiple: true
            }}
            name={'layouts'}
            label={l('dashboard.chart.select')}
          />
          {selectOptions.length > 0 && (
            <>
              {selectOptions.map((x) => {
                return (
                  <ProFormSegmented
                    key={x.id}
                    label={x.name}
                    fieldProps={{
                      options: options,
                      defaultValue: x.type,
                      onChange: (v) => {
                        const index = selectOptions.findIndex((y) => y.id === x.id);
                        selectOptions[index] = {
                          ...(selectOptions[index] as LayoutChartData),
                          type: v as string
                        };
                        setSelectOptions([...selectOptions]);
                      }
                    }}
                  />
                );
              })}
              <p>以下数据为随机生成，不是真实数据</p>
              {
                <div style={{ height: '30vh', width: '100%' }}>
                  <ChartShow
                    chartTheme={chartTheme}
                    chartOptions={EchartsOptions(selectOptions, title)}
                    title={title}
                    value={selectOptions[0].data?.slice(-1)[0]?.value}
                    type={selectOptions[0]?.type}
                    fontSize={30}
                  />
                </div>
              }
            </>
          )}
        </ProForm>
      </Modal>
    </>
  );
};

const getAllPath = (data: DefaultOptionType[], id: number | string) => {
  for (const d1 of data) {
    for (const d2 of d1.children || []) {
      for (const d3 of d2.children || []) {
        if (d3.value == id) {
          return [d1.value, d2.value, d3.value];
        }
      }
    }
  }
  return [];
};

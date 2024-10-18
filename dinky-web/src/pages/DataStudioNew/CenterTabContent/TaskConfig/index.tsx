import {Tabs, TabsProps} from "antd";
import {ProForm, ProFormDigit, ProFormGroup, ProFormSwitch} from "@ant-design/pro-components";
import {l} from "@/utils/intl";
import React from "react";
import {InfoCircleOutlined} from "@ant-design/icons";
import {SWITCH_OPTIONS} from "@/services/constants";
import {TempData} from "@/pages/DataStudioNew/type";
import {BasicConfig} from "@/pages/DataStudioNew/CenterTabContent/TaskConfig/BasicConfig";
import {FlinkSQLState} from "@/pages/DataStudioNew/CenterTabContent/FlinkSQL";

export default (props: { tempData: TempData,data:FlinkSQLState,onValuesChange?: (changedValues: any, values: FlinkSQLState) => void }) => {

  const items: TabsProps['items'] = [
    {
      key: 'basicConfig', label: '基础配置', children: <BasicConfig tempData={props.tempData} data={props.data} onValuesChange={props.onValuesChange}/>
    },

    {
      key: 'previewConfig', label: '预览配置', children: <ProForm
        initialValues={{
          ...props.data
        }}
        style={{padding: '10px'}}
        submitter={false}
        layout='vertical'
        onValuesChange={props.onValuesChange}
      >
        <ProFormGroup>
          <ProFormSwitch
            label={l('pages.datastudio.label.execConfig.changelog')}
            name='useChangeLog'
            tooltip={{
              title: l('pages.datastudio.label.execConfig.changelog.tip'),
              icon: <InfoCircleOutlined/>
            }}
            {...SWITCH_OPTIONS()}
          />
          <ProFormSwitch
            label={l('pages.datastudio.label.execConfig.autostop')}
            name='useAutoCancel'
            tooltip={{
              title: l('pages.datastudio.label.execConfig.autostop.tip'),
              icon: <InfoCircleOutlined/>
            }}
            {...SWITCH_OPTIONS()}
          />
          <ProFormDigit
            width={'xs'}
            label={l('pages.datastudio.label.execConfig.maxrow')}
            name='maxRowNum'
            tooltip={l('pages.datastudio.label.execConfig.maxrow.tip')}
            min={1}
            max={9999}
          />
        </ProFormGroup>
      </ProForm>
    }
  ];

  return (<Tabs items={items} centered/>)
}




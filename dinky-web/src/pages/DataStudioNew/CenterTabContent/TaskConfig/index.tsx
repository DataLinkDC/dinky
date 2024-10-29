import {Tabs, TabsProps, Tag} from "antd";
import {ProForm, ProFormDigit, ProFormGroup, ProFormSelect, ProFormSwitch} from "@ant-design/pro-components";
import {l} from "@/utils/intl";
import React from "react";
import {InfoCircleOutlined} from "@ant-design/icons";
import {DIALECT, SWITCH_OPTIONS} from "@/services/constants";
import {TaskState, TempData} from "@/pages/DataStudioNew/type";
import {BasicConfig} from "@/pages/DataStudioNew/CenterTabContent/TaskConfig/BasicConfig";
import {assert} from "@/pages/DataStudio/function";
import {isSql} from "@/pages/DataStudioNew/utils";
import {DataSources} from "@/types/RegCenter/data";
import {TagAlignLeft} from "@/components/StyledComponents";

export default (props: {
  tempData: TempData,
  data: TaskState,
  onValuesChange?: (changedValues: any, values: TaskState) => void
}) => {

  const {data,tempData} = props;
  const items: TabsProps['items'] = [];
  if (assert(data.dialect, [DIALECT.FLINK_SQL, DIALECT.FLINKJAR], true, 'includes')) {
    items.push({
      key: 'basicConfig',
      label: '基础配置',
      children: <BasicConfig tempData={props.tempData} data={props.data} onValuesChange={props.onValuesChange}/>
    })
  }
  if (isSql(data.dialect) || assert(data.dialect, [DIALECT.FLINK_SQL, DIALECT.FLINKJAR], true, 'includes')) {
    const renderOtherConfig = () => {
      if (isSql(data.dialect)) {
        const dataSourceData: Record<string, React.ReactNode> = {};
        const databaseDataList = tempData.dataSourceDataList;
        databaseDataList
          .filter((x) => x.type.toLowerCase() === data?.dialect.toLowerCase())
          .forEach((item: DataSources.DataSource) => {
            dataSourceData[item.id] = (
              <TagAlignLeft>
                <Tag key={item.id} color={item.enabled ? 'processing' : 'error'}>
                  {item.type}
                </Tag>
                {item.name}
              </TagAlignLeft>
            );
          });
        return <ProFormSelect
          width={'sm'}
          name={'databaseId'}
          label={l('pages.datastudio.label.execConfig.selectDatabase')}
          convertValue={(value) => String(value)}
          valueEnum={dataSourceData}
          placeholder='Please select a dataSource'
          rules={[{required: true, message: 'Please select your dataSource!'}]}
        />
      } else {
        return <>
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
        </>
      }
    }
    items.push({
      key: 'previewConfig', label: '预览配置', children: <ProForm
        initialValues={{
          ...props.data
        }}
        style={{padding: '10px'}}
        submitter={false}
        layout='vertical'
        onValuesChange={props.onValuesChange}
      >
        <ProFormGroup style={{display: "flex", justifyContent: 'center'}}>
          {renderOtherConfig()}
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
    })
  }


  return (<Tabs items={items} centered/>)
}




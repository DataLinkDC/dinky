import {
  ProForm,
  ProFormDigit,
  ProFormInstance,
  ProFormSelect,
  ProFormSwitch,
  ProFormText
} from "@ant-design/pro-components";
import {l} from "@/utils/intl";
import {InfoCircleOutlined} from "@ant-design/icons";
import {SWITCH_OPTIONS} from "@/services/constants";
import {SAVE_POINT_TYPE} from "@/pages/DataStudio/constants";
import {ProFormDependency} from "@ant-design/pro-form";
import {buildAlertGroupOptions} from "@/pages/DataStudio/RightContainer/JobConfig/function";
import {ProFormFlinkConfig} from "@/pages/DataStudioNew/CenterTabContent/TaskConfig/ProFormFlinkConfig";
import {ProFormFlinkUdfConfig} from "@/pages/DataStudioNew/CenterTabContent/TaskConfig/ProFormFlinkUdfConfig";
import React, {useEffect, useRef, useState} from "react";
import {TempData} from "@/pages/DataStudioNew/type";
import {FlinkSQLState} from "@/pages/DataStudioNew/CenterTabContent/FlinkSQL";

export const BasicConfig = (props: { tempData: TempData,data:FlinkSQLState,onValuesChange?: (changedValues: any, values: FlinkSQLState) => void }) => {
  const {alertGroup, flinkConfigOptions, flinkUdfOptions} = props.tempData;
  const formRef = useRef<ProFormInstance>();
  const [containerWidth, setContainerWidth] = useState<number>(0);

  const divRef = useRef<HTMLDivElement>(null);
  useEffect(() => {
    if (!divRef.current) {
      return () => {
      }
    }
    // 监控布局宽度高度变化，重新计算树的高度
    const element = divRef.current!!;
    const observer = new ResizeObserver((entries) => {
      if (entries?.length === 1) {
        // 这里节点理应为一个，减去的高度是为搜索栏的高度
        setContainerWidth(entries[0].contentRect.width);
      }
    });
    observer.observe(element);
    return () => {
      observer.unobserve(element)
    };
  }, [])
  return <div ref={divRef}>
    <ProForm initialValues={{...props.data}}
             submitter={false}
             onValuesChange={props.onValuesChange}
             formRef={formRef} layout={'vertical'} rowProps={{
      gutter: [16, 0],
    }}>
      <ProForm.Group>
        <ProFormDigit
          width={'xs'}
          label={l('pages.datastudio.label.jobConfig.parallelism')}
          name='parallelism'
          tooltip={l('pages.datastudio.label.jobConfig.parallelism.tip')}
          max={100}
          min={1}
        />
        <ProFormSwitch
          label={l('pages.datastudio.label.jobConfig.fragment')}
          name='fragment'
          valuePropName='checked'
          tooltip={{
            title: l('pages.datastudio.label.jobConfig.fragment.tip'),
            icon: <InfoCircleOutlined/>
          }}
          {...SWITCH_OPTIONS()}
        />
        <ProFormSwitch
          label={l('pages.datastudio.label.jobConfig.batchmode')}
          name='batchModel'
          valuePropName='checked'
          tooltip={{
            title: l('pages.datastudio.label.jobConfig.batchmode.tip'),
            icon: <InfoCircleOutlined/>
          }}
          {...SWITCH_OPTIONS()}
        />
      </ProForm.Group>
      <ProFormSelect
        label={l('pages.datastudio.label.jobConfig.savePointStrategy')}
        name='savePointStrategy'
        tooltip={l('pages.datastudio.label.jobConfig.savePointStrategy.tip')}
        options={SAVE_POINT_TYPE}
        allowClear={false}
      />
      <ProFormDependency name={['savePointStrategy']}>
        {({savePointStrategy}) => {
          if (savePointStrategy === 3) {
            return <ProFormText
              label={l('pages.datastudio.label.jobConfig.savePointpath')}
              name='savePointPath'
              tooltip={l('pages.datastudio.label.jobConfig.savePointpath.tip1')}
              placeholder={l('pages.datastudio.label.jobConfig.savePointpath.tip2')}
            />
          } else {
            return null
          }
        }}
      </ProFormDependency>

      <ProFormSelect
        label={l('pages.datastudio.label.jobConfig.alertGroup')}
        name='alertGroupId'
        placeholder={l('pages.datastudio.label.jobConfig.alertGroup.tip')}
        options={buildAlertGroupOptions(alertGroup)}
        allowClear={false}
      />
      <ProFormFlinkConfig containerWidth={containerWidth} flinkConfigOptions={flinkConfigOptions}
                          getCode={() => formRef.current?.getFieldValue(["configJson", 'customConfig'])?.map((item: {
                            key: string,
                            value: string
                          }) => item.key + " : " + item.value)?.join("\n")}/>
      <ProFormFlinkUdfConfig containerWidth={containerWidth} flinkUdfOptions={flinkUdfOptions}
                             proFormInstance={() => formRef.current!!} defaultValue={[]}/>


    </ProForm>
  </div>
}

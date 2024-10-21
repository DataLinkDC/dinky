import {EnvType} from "@/pages/DataStudio/model";
import {Select} from "antd";
import React from "react";
import {l} from "@/utils/intl";
import {buildEnvOptions} from "@/pages/DataStudio/RightContainer/JobConfig/function";
import {ProFormSelect} from "@ant-design/pro-components";


export const SelectFlinkEnv = (params: { flinkEnv: EnvType[] ,value?:number,onChange?:(value:number)=>void}) => {
  const {flinkEnv,value,onChange} = params;
  const options = [{label: l('button.disable'), value: -1},...flinkEnv.map((env) => ({label: env.name, value: env.id}))];

  return (
    <ProFormSelect
      style={{height: "100%"}}
      name='envId'
      tooltip={l('pages.datastudio.label.jobConfig.flinksql.env.tip1')}
      options={options}
      rules={[
        { required: true }
      ]}
      showSearch
      allowClear={false}
    />
  )
}



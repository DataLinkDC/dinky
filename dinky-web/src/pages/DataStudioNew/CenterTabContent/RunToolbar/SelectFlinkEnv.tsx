import {EnvType} from "@/pages/DataStudio/model";
import React, {memo} from "react";
import {l} from "@/utils/intl";
import {ProFormSelect} from "@ant-design/pro-components";

import "../index.less"

export const SelectFlinkEnv = memo((params: {
    flinkEnv: EnvType[],
  }) => {
    const {flinkEnv} = params;
    const options = [{label: l('button.disable'), value: -1}, ...flinkEnv.map((env) => ({
      label: env.name,
      value: env.id
    }))];
    return (
      <ProFormSelect
        name='envId'
        tooltip={l('pages.datastudio.label.jobConfig.flinksql.env.tip1')}
        options={options}
        rules={[
          {required: true}
        ]}
        showSearch
        allowClear={false}
      />
    )
  }
)


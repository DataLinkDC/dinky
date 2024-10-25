import {ProFormCascader} from "@ant-design/pro-form/lib";
import {CascaderProps, Tag} from "antd";
import {DefaultOptionType} from "antd/es/select";
import {FlinkCluster} from "@/pages/DataStudioNew/type";


type Option = {
  value: number;
  label: string;
  enabled?: boolean;
  children?: Option[];
}
export const SelectFlinkRunMode = (props: { data: FlinkCluster[] }) => {
  const {data} = props
  const optionDict = {
    "local": [],
    "standalone": [],
    "yarn-session": [],
    "yarn-per-job": [],
    "yarn-application": [],
    "kubernetes-session": [],
    "kubernetes-application": [],
    "kubernetes-application-operator": []
  } as Record<string, Option[]>
  data.forEach((item) => {
    if (item.type === "yarn-application") {
      optionDict['yarn-per-job'].push({
        value: item.id,
        label: item.name,
        enabled: item.enabled
      })
    }
    optionDict[item.type].push({
      value: item.id,
      label: item.name,
      enabled: item.enabled
    })
  })
  //optionDict转换options
  const options = [{
    value: 'local',
    label: "local"
  }, ...Object.keys(optionDict).filter(key => optionDict[key].length > 0).map((key) => {
    return {
      value: key,
      label: key,
      children: optionDict[key]
    }
  })]
  console.log(data)
  const displayRender: CascaderProps<DefaultOptionType>['displayRender'] = (labels, selectedOptions = []) =>

    labels.map((label, i) => {
      const option = selectedOptions[i];
      return option && (i === labels.length - 1) && (
        <span key={label}>
          {label} {labels.length > 1 &&
          <Tag color={option.enabled ? 'processing' : 'error'}>{selectedOptions[0].label}</Tag>}
        </span>
      );
    });
  return (
    <ProFormCascader
      name={['flinkMode']}
      rules={[
        {required: true}
      ]}
      fieldProps={{
        options: options,
        displayRender: displayRender,
        allowClear: false
      }}
    />
  )
}

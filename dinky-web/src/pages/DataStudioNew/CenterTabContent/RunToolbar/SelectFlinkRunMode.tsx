import {ProFormCascader} from "@ant-design/pro-form/lib";
import {CascaderProps, Tag} from "antd";
import {DefaultOptionType} from "antd/es/select";
import {FlinkCluster} from "@/pages/DataStudioNew/type";
import {l} from "@/utils/intl";


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
    "standalone":  [],
    "yarn-session": [],
    "yarn-per-job": [],
    "yarn-application": [],
    "kubernetes-session": [],
    "kubernetes-application": [],
    "kubernetes-application-operator": []
  } as Record<string, Option[]>
  data.forEach((item) => {
    optionDict[item.type].push({
      value: item.id,
      label: item.name,
      enabled: item.enabled
    })
  })
  //optionDict转换options
  const options = [{
    value: -1,
    label: "local"
  },...Object.keys(optionDict).filter(key => optionDict[key].length > 0).map((key) => {
    return {
      value: key,
      label: key,
      children: optionDict[key]
    }
  })]

  const displayRender: CascaderProps<DefaultOptionType>['displayRender'] = (labels, selectedOptions = []) =>
    labels.map((label, i) => {
      const option = selectedOptions[i];
      return (i === labels.length - 1) && (
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
        { required: true }
      ]}
      fieldProps={{
        options: options,
        displayRender: displayRender,
        onChange: (value) => {
          console.log(value);
        },
        allowClear:false
      }}
    />
  )
}

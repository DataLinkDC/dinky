import {CascaderProps, Modal} from "antd";
import React, {useEffect, useRef, useState} from "react";
import {DefaultOptionType} from "antd/es/select";
import {Option} from "@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter/data";
import useHookRequest from "@/hooks/useHookRequest";
import {getMetricsLayoutByCascader} from "@/pages/Dashboard/service";
import {ProFormCascader} from "@ant-design/pro-form/lib";
import {ProForm, ProFormInstance, ProFormSegmented, ProFormText} from "@ant-design/pro-components";
import {EchartsOptions, getRandomData, LayoutChartData, Options} from "@/pages/Dashboard/data";
import {AreaChartOutlined, BarChartOutlined, FieldNumberOutlined, LineChartOutlined} from "@ant-design/icons";
import ReactECharts from "echarts-for-react";

interface EditProps {
  open: boolean;
  chartTheme: string,
  onCancel: () => void,
  onOk: (data: { title: string, layouts: LayoutChartData[] }) => Promise<void>
  title: string,
  defaultValue?: {
    title: string,
    layouts: LayoutChartData[]
  }
}

export default (props: EditProps) => {
  const {open = true, chartTheme = 'dark', defaultValue} = props;

  const filter = (inputValue: string, path: DefaultOptionType[]) =>
    path.some(
      (option) => (option.label as string).toLowerCase().indexOf(inputValue.toLowerCase()) > -1,
    );
  const {data, refresh, loading} = useHookRequest<any, any>(getMetricsLayoutByCascader, {defaultParams: []});

  const [selectOptions, setSelectOptions] = useState<LayoutChartData[]>(defaultValue?.layouts ?? [])

  const [title, setTitle] = useState(defaultValue?.title || "");
  const form = useRef<ProFormInstance>();

  useEffect(() => {
    setTitle(defaultValue?.title || "")
    setSelectOptions(defaultValue?.layouts || [])
    form?.current?.setFieldsValue({
      title: defaultValue?.title || "",
      layouts: (defaultValue?.layouts)?.map(x => getAllPath(data,x.id)) || []
    })
  }, [defaultValue])


  const options = [
    {label: '', value: 'Line', icon: <LineChartOutlined/>},
    {label: '', value: 'Area', icon: <AreaChartOutlined/>},
    {label: '', value: 'Bar', icon: <BarChartOutlined/>},
  ]
  if (selectOptions.length < 2) {
    options.push({label: '', value: 'Statistic', icon: <FieldNumberOutlined/>})
  }


  const onChange: CascaderProps<Option>['onChange'] = (value, selectedOptions) => {
    // @ts-ignore
    setSelectOptions(selectedOptions.filter(x => x.length === 3).map(x => {
      // @ts-ignore
      const d = x[x.length - 1];
      return {
        type: "Line",
        name: d.label,
        id: Number.parseInt(d.value),
        data: getRandomData(5)
      }
    }));
  };

  return (<>
    <Modal open={open} loading={loading} title={props.title}
           onOk={async () => await props.onOk({
             title: title, layouts: selectOptions.map(x => {
               return {type: x.type, name: x.name, id: x.id}
             })
           })} onCancel={props.onCancel}
           onClose={props.onCancel}>
      <ProForm
        formRef={form}
        submitter={false}
        layout={"horizontal"}
      >
        <ProFormText label={"title"} name={"title"}
                     fieldProps={{
                       defaultValue: title,
                       onChange: v => {
                         setTitle(v.currentTarget.value)
                       }
                     }}/>
        <ProFormCascader
          fieldProps={
            {
              // @ts-ignore
              multiple: true,
              options: data,
            }
          }
          name={"layouts"}
          onChange={onChange}
          placeholder="Please select"
          showSearch={{filter}}
        />
        {selectOptions.length > 0 && (
          <>
            {selectOptions.map(x => {
              return <ProFormSegmented key={x.id} label={x.name}
                                       fieldProps={{
                                         options: options,
                                         defaultValue: x.type,
                                         onChange: (v) => {
                                           const index= selectOptions.findIndex(y => y.id === x.id)
                                           selectOptions[index] = {
                                             ...selectOptions[index] as LayoutChartData,
                                             type: v as string
                                           }
                                           setSelectOptions([...selectOptions])
                                         }
                                       }}/>


            })}
            <p>以下数据为随机生成，不是真实数据</p>
            <ReactECharts
              option={EchartsOptions(selectOptions, title)}
              notMerge={true}
              lazyUpdate={true}
              theme={chartTheme}
              style={{height: '30vh', width: '100%', zIndex: 99}}
            />
          </>
        )}
      </ProForm>
    </Modal>
  </>)
}


const getAllPath = (data: Options[], id: number | string) => {
  for (const d1 of data) {
    for (const d2 of d1.children || []) {
      for (const d3 of d2.children || []) {
        if (d3.value == id) {
          return [d1.value, d2.value, d3.value]
        }
      }
    }
  }
  return []
}


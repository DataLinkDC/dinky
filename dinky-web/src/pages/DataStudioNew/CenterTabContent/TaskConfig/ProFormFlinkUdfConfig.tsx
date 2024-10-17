import {l} from "@/utils/intl";
import {ProFormGroup, ProFormInstance, ProFormList, ProFormText} from "@ant-design/pro-components";
import {Space} from "antd";
import FlinkUdfOptionsSelect from "@/components/Flink/UdfSelect";
import React, {useState} from "react";
import {DefaultOptionType} from "antd/es/select";
import {TaskUdfRefer} from "@/types/Studio/data";
import {calculatorWidth} from "@/pages/DataStudioNew/CenterTabContent/TaskConfig/function";

export const ProFormFlinkUdfConfig = (props: {
  containerWidth: number,
  flinkUdfOptions: DefaultOptionType[];
  proFormInstance: () => ProFormInstance,
  defaultValue: { className: string, name: string }[]
}) => {
  const {flinkUdfOptions, containerWidth,proFormInstance} = props;

  const [currentSelectUdfIndexMap, setCurrentSelectUdfIndexMap] = useState<
    Map<number, TaskUdfRefer>
  >(
    new Map(
      []
    )
  );

  const existsClassNameList = [...currentSelectUdfIndexMap.values().map(item => item.className)];

  return (<ProFormList
    label={l('pages.datastudio.label.udf')}
    tooltip={l('pages.datastudio.label.udf.tip')}
    name={['configJson', 'udfRefer']}
    copyIconProps={false}
    onAfterRemove={(_, index) => {
      // 删除一项之后拿到 index 从 currentSelectUdfIndexMap 中删除对应的值 || get the value from currentSelectUdfIndexMap and delete it
      setCurrentSelectUdfIndexMap((prevState) => {
        const newState = new Map(prevState);
        newState.delete(index);
        return newState;
      });
    }}
    creatorButtonProps={{
      style: {width: '100%'},
      creatorButtonText: l('pages.datastudio.label.udf.injectUdf')
    }}
  >
    {(_, index) => {
      return (
        <ProFormGroup>
          <Space key={'udf' + index} align='baseline'>
            <FlinkUdfOptionsSelect
              colProps={{flex: 5}}
              name={'className'}
              width={calculatorWidth(containerWidth) + 80}
              mode={'single'}
              key={index + 'udf-config'}
              allowClear
              showSearch
              placeholder={l('pages.datastudio.label.udf.className')}
              options={flinkUdfOptions.map(item => {
                return {...item, children: item.children?.filter(child => !existsClassNameList.includes(child.value))}
              })}
              onChange={(value: string) => {
                setCurrentSelectUdfIndexMap((prevState) => {
                  const newState = new Map(prevState);
                  newState.set(index, {name: '', className: value});
                  return newState;
                });
                const simpleClassName = value?.split('.')?.pop() ?? '';
                const lowerName = simpleClassName.charAt(0).toLowerCase() + simpleClassName.slice(1);
                proFormInstance().setFieldsValue({
                  configJson: {
                    udfRefer: {
                      [index]: {
                        className: value,
                        name: lowerName
                      }
                    }
                  }
                });
              }}
            />
            <ProFormText
              name={'name'}
              width={calculatorWidth(containerWidth) - 60}
              placeholder={l('pages.datastudio.label.udf.name')}
            />
          </Space>
        </ProFormGroup>
      );
    }}
  </ProFormList>)
}
// const handleClassChange = async (value: string, index: number) => {
//   // 检测 这个值是否已经存在 currentSelectUdfIndexMap 的 map 中 || check if the value already exists in the map of currentSelectUdfIndexMap
//   const values = currentSelectUdfIndexMap.values();
//   for (const taskUdfRefer of values) {
//     if (taskUdfRefer?.className === value) {
//       await ErrorMessageAsync(
//         l('pages.datastudio.label.udf.duplicate.tip', '', { className: value }),
//         3
//       );
//       // clear the value of the form
//       form.setFieldsValue({
//         configJson: {
//           udfRefer: {
//             [index]: {
//               className: '',
//               name: ''
//             }
//           }
//         }
//       });
//       return;
//     }
//   }
//   const simpleClassName = value?.split('.')?.pop() ?? '';
//   const lowerName = simpleClassName.charAt(0).toLowerCase() + simpleClassName.slice(1);
//   processSelectUdfMapState(index, value, lowerName);
//   form.setFieldsValue({
//     configJson: {
//       udfRefer: {
//         [index]: {
//           className: value,
//           name: lowerName
//         }
//       }
//     }
//   });
// };

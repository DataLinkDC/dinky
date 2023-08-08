import {BaseConfigProperties} from "@/types/SettingCenter/data";
import React, {useEffect, useState} from "react";
import {ProCard} from "@ant-design/pro-components";
import {l} from "@/utils/intl";
import GeneralConfig from "@/pages/SettingCenter/GlobalSetting/SettingOverView/GeneralConfig";
import {RadioChangeEvent, Tag} from "antd";

interface ResourcesConfigProps {
  data: BaseConfigProperties[];
  onSave: (data: BaseConfigProperties) => void;
}

type CacheEnum = {
  name: string;
  configs: BaseConfigProperties[];
}

export const ResourcesConfig = ({data, onSave}: ResourcesConfigProps) => {

  const [loading, setLoading] = React.useState(false);
  const [model, setModel] = React.useState("hdfs");
  const [baseData, setBaseData] = React.useState(data);
  const [enumCache] = useState({
    "base": [] as BaseConfigProperties[],
    "hdfs": [] as BaseConfigProperties[],
    "oss": [] as BaseConfigProperties[]
  });
  useEffect(() => {
    if (data.length < 1) {
      return
    }
    const needDeleteIndexes: number[] = [];
    let m;
    data.forEach((datum, index) => {
      if (datum.key === 'resource.settings.model'){
        enumCache.base.push(datum);
        const modelCase = datum.value.toLowerCase();
        m = modelCase
        setModel(modelCase);
        return
      }
      const v = datum.key.split(".").at(2);
      if (v === "hdfs" || v === "oss"){
        // @ts-ignore
        enumCache[v].push(datum);
        needDeleteIndexes.push(index)
      } else {
        enumCache.base.push(datum);
      }
    });

    const baseConfigProperties = data.filter((d, index) => !needDeleteIndexes.includes(index));
    // @ts-ignore
    enumCache[m].forEach(x => {
      baseConfigProperties.push(x)
    });
    setBaseData(baseConfigProperties);
  }, [data])
  useEffect(() => {
    const d: BaseConfigProperties[] = [];
    enumCache.base.forEach(x => {
      d.push(x)
    });
    console.log(enumCache)
    // @ts-ignore
    enumCache[model].forEach(x => {
      d.push(x)
    })
    setBaseData(d)
  }, [model])
  const onSaveHandler = async (data: BaseConfigProperties) => {
    setLoading(true);
    await onSave(data);
    setLoading(false);
  };
  const selectChange = async (value: RadioChangeEvent) => {
    setModel(value.target.value)
    await onSaveHandler({example: [], frontType: "", key: "resource.settings.model", note: "", value:value.target.value.toLocaleUpperCase() })
  }
  return <>
      {/*tooltip={l('sys.setting.resource.tooltip')}*/}
      <GeneralConfig
        loading={loading}
        onSave={onSaveHandler}
        tag={<><Tag color={'default'}>{l('sys.setting.tag.integration')}</Tag></>}
        data={baseData}
        selectChanges={{"resource.settings.model": selectChange}}
      />
  </>;
};

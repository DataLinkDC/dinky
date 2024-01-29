import {useEffect, useState} from "react";
import CodeShow from "@/components/CustomEditor/CodeShow";
import {queryClassLoaderJars} from "@/pages/SettingCenter/ClassLoaderJars/service";
import {Alert, Space} from "antd";
import {l} from "@/utils/intl";


export default () => {

  const [data, setData] = useState<string>('')


  useEffect(() => {
    queryClassLoaderJars().then((res) => {
      if (res && res.length > 0) setData(res.join('\n'))
    })
  }, []);

  return (
    <Space size={'large'} direction={'vertical'}>
      <Alert message={l('sys.classLoaderJars.tips')} type="info" showIcon/>
      <CodeShow showFloatButton enableMiniMap height={'88vh'} code={data} language={'java'}/>
    </Space>
  )
}

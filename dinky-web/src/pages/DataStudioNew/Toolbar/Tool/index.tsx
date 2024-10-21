import {Tabs} from "antd";
import TextComparison from "@/pages/DataStudioNew/Toolbar/Tool/TextComparison";
import {JsonToSql} from "@/pages/DataStudioNew/Toolbar/Tool/JsonToSql";

export default () => {
  const items = [
    {
      key: 'jsonToSql',
      label: 'JSON转Flink-SQL',
      children: <JsonToSql/>,
    }
    ,
    {
      key: 'textComparison',
      label: '文本对比',
      children: <TextComparison/>,
    },
  ];
  return (
    <div style={{padding: 10}}>
      <Tabs items={items} size={"small"}/>
    </div>
  )
}

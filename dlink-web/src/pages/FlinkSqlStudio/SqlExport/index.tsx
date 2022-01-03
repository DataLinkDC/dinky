import {Typography} from 'antd';
import {useEffect, useState} from "react";
import {getData} from "@/components/Common/crud";

const {Text, Paragraph} = Typography;

const SqlExport = (props: any) => {

  const {id} = props;
  const [statement, setStatement] = useState<string>();

  const refreshStatement = async () => {
    const msg = await getData('api/task/exportSql', {id: id});
    setStatement(msg.datas);
  };

  useEffect(() => {
    refreshStatement();
  }, []);

  return (<>
    <Paragraph copyable={{text: statement}}>
    </Paragraph>
    <Paragraph>
      <pre style={{height: '300px'}}>{statement}</pre>
    </Paragraph></>)
};

export default SqlExport;

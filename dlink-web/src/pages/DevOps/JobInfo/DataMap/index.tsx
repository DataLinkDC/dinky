import {Tabs, Empty} from 'antd';
import {getLineage} from "@/pages/DevOps/service";
import {useEffect, useState} from "react";
import Lineage, {getInit} from "@/components/Lineage";
const {TabPane} = Tabs;

const DataMap = (props: any) => {

  const {job} = props;
  const [data, setData] = useState(getInit());

  const getData = () => {
    const res = getLineage(job.instance?.id);
    res.then((result)=>{
      result.datas.tables.forEach(table => {
        table.isExpand = true;
        table.isFold = false;
      });
      setData(result.datas);
    });
  };

  useEffect(() => {
    getData();
  }, []);

  return (<>
    <Tabs defaultActiveKey="Lineage" size="small" tabPosition="top" style={{
      border: "1px solid #f0f0f0"
    }}>
      <TabPane tab={<span>血缘分析</span>} key="Lineage">
        <Lineage datas={data}/>
      </TabPane>
    </Tabs>
  </>)
};

export default DataMap;

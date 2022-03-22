import { Tabs,Tooltip,Button } from "antd";
import {SearchOutlined} from "@ant-design/icons";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {connect} from "umi";
import styles from "./index.less";
import {getLineage} from "@/pages/FlinkSqlStudio/service";
import {useState} from "react";
import Lineage, {getInit} from "@/components/Lineage";

const { TabPane } = Tabs;

const StudioCA = (props: any) => {
  const {current} = props;
  const [data, setData] = useState(getInit());

  const handleLineage=()=>{
    const res = getLineage({
      statement:current.value,
      type: 1,
    });
    res.then((result)=>{
      setData(result.datas);
    })
  };

  return (<>
    <Tabs defaultActiveKey="Lineage" size="small" tabPosition="top" style={{border: "1px solid #f0f0f0"}}
          tabBarExtraContent={<Tooltip title="重新计算血缘">
            <Button
              type="text"
              icon={<SearchOutlined />}
              onClick={handleLineage}
            >
              计算血缘
            </Button>
          </Tooltip>}
    >
      <TabPane tab={<span>血缘分析</span>} key="Lineage">
        <Lineage datas={data}/>
      </TabPane>
    </Tabs>
  </>)
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  current: Studio.current,
}))(StudioCA);

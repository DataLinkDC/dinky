import {Tabs, Tooltip, Button, Modal, message} from "antd";
import {SearchOutlined, SnippetsOutlined} from "@ant-design/icons";
import {StateType} from "@/pages/DataStudio/model";
import {connect} from "umi";
import styles from "./index.less";
import {getLineage, getStreamGraph} from "@/pages/DataStudio/service";
import {useState} from "react";
import Lineage, {getInit} from "@/components/Lineage";
import CodeShow from "@/components/Common/CodeShow";
import {DIALECT} from "@/components/Studio/conf";

const { TabPane } = Tabs;

const StudioCA = (props: any) => {
  const {current} = props;
  const [data, setData] = useState(getInit());

  const handleLineage=()=>{
    const res = getLineage({
      statement:current.value,
      statementSet:current.task.statementSet,
      dialect:current.task.dialect,
      databaseId:current.task.databaseId,
      type: 1,
    });
    res.then((result)=>{
      if(result.datas){
        setData(result.datas);
      }else {
        message.error(`获取作业血缘失败，原因：\n${result.msg}`);
      }
    })
  };

  const handleExportStreamGraphPlan=()=>{
    const res = getStreamGraph({
      ...current.task,
      configJson: JSON.stringify(current.task.config),
      statement: current.value,
    });
    res.then((result)=>{
      Modal.info({
        title: current.task.alias + '的 StreamGraphPlan',
        width: 1000,
        content: (
          <CodeShow code={JSON.stringify((result.datas?result.datas:result.msg), null, "\t")} language='json'
                    height='500px' theme="vs-dark"/>
        ),
        onOk() {
        },
      });
    })
  };

  return (<>
    <Tabs defaultActiveKey="Lineage" size="small" tabPosition="top" style={{border: "1px solid #f0f0f0"}}
          tabBarExtraContent={
            <>
              <Tooltip title="重新计算血缘">
                <Button
                  type="text"
                  icon={<SearchOutlined />}
                  onClick={handleLineage}
                >
                  计算血缘
                </Button>
              </Tooltip>
              {(!current.task.dialect || current.task.dialect == DIALECT.FLINKSQL) ?
                <Tooltip title="导出 StreamGraphPlan">
                  <Button
                    type="text"
                    icon={<SnippetsOutlined/>}
                    onClick={handleExportStreamGraphPlan}
                  >
                    StreamGraphPlan
                  </Button>
                </Tooltip> : undefined
              }
            </>}
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

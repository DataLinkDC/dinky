import {Tabs, Empty} from 'antd';
import CodeShow from "@/components/Common/CodeShow";
import {LineageTable} from 'react-lineage-dag';
const {TabPane} = Tabs;

const DataMap = (props: any) => {

  const {job} = props;

  const data = {
    tables: [
      {
        id: '1',
        name: 'table-1',
        columns: [
          {
            name: 'id',
            title: 'id'
          },
          {
            name: 'age',
            title: 'age'
          }
        ]
      },
      {
        id: '2',
        name: 'table-2',
        columns: [
          {
            name: 'id',
            title: 'id'
          },
          {
            name: 'age',
            title: 'age'
          }
        ]
      },
      {
        id: '3',
        name: 'table-3',
        columns: [
          {
            name: 'id',
            title: 'id'
          },
          {
            name: 'age',
            title: 'age'
          }
        ]
      }
    ],
    relations: [
      {
        srcTableId: '1',
        tgtTableId: '2',
        // srcTableColName: 'id',
        // tgtTableColName: 'age'
      },
      {
        srcTableId: '1',
        tgtTableId: '3',
        // srcTableColName: 'id',
        // tgtTableColName: 'age'
      }
    ]
  };

  return (<>
    <Tabs defaultActiveKey="OneCA" size="small" tabPosition="top" style={{
      border: "1px solid #f0f0f0"
    }}>
      <TabPane tab={<span>血缘分析</span>} key="OneCA">
        <LineageTable {...data} onEachFrame={() => { }}/>
      </TabPane>
    </Tabs>
  </>)
};

export default DataMap;

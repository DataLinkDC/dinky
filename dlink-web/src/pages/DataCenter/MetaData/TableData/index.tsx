import  {useEffect, useState} from "react";
import {Spin, Table} from "antd";
import {showTableData} from "@/components/Studio/StudioEvent/DDL";

const TableData = (props: any) => {

  const {dbId,table,schema} = props;
  const [tableData, setableData] = useState({columns:[],rowData:[]});
  const [loading, setLoading] = useState(false);

  const fetchData = async () => {
    setLoading(true);
    let temp = {rowData:[],columns:[]}
    await showTableData(dbId,schema,table,40).then(result => {
      let data = result.datas;
      for (const columnsKey in result.datas.columns) {
        temp.columns.push({
          title: data.columns[columnsKey],
          dataIndex: data.columns[columnsKey],
          key: data.columns[columnsKey],
          ellipsis: true
        })
      }

      for (const row  of result.datas.rowData) {
        row.key = row.id
        temp.rowData.push(row)
      }
    })
    setableData(temp);
    setLoading(false)
  };

  useEffect(() => {
    fetchData();
  }, [dbId,table,schema]);

  return (
    <div>
      <Spin   spinning={loading} delay={500}>
        <Table style={{height: '95vh'}}
               columns={tableData.columns}
               dataSource={tableData.rowData}
               pagination={{ pageSize: 50 }}
               scroll={{ y: "80vh" ,x: true}}

        />
      </Spin>

    </div>

  )
};

export default TableData

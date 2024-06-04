import React, {useEffect, useState} from "react";
import {JobMetricsItem} from "@/pages/DevOps/JobDetail/data";
import {ProForm, ProFormSelect, ProFormText} from "@ant-design/pro-components";
import {Pagination, Row} from "antd";


type ListPaginationProps<T,F> = {
  data: T[];
  defaultPageSize: number;
  layount: (data: T[]) => React.ReactNode;
  filter ?:FilterProp<T,F>
};


type FilterProp<T,F> = {
  content: (data:T[],setFilter:React.Dispatch<F>)=>React.ReactNode;
  filter: (data:T,filter:F)=>boolean;
}




const ListPagination = <T,F,>(props: ListPaginationProps<T,F>) => {

  const [data, setData] = useState<T[]>(props.data);
  const [currentData, setCurrentData] = useState<T[]>([]);
  const [currentPage, setCurrentPage] = useState<number>(1);
  const [currentPageSize, setCurrentPageSize] = useState<number>(props.defaultPageSize);
  const [filter, setFilter] = useState<F>({} as F);

  useEffect(() => {

    const newData = props.data.filter(item => {
      if (props.filter) {
        return props.filter.filter(item,filter)
      }
      return true
    })
    setCurrentData(newData.slice((currentPage - 1) * currentPageSize, currentPage * currentPageSize))
    setData(newData)
  }, [currentPage, currentPageSize, filter]);


  return (<>


    {props.filter && props.filter.content(props.data,setFilter)}
    <Row gutter={[8, 16]}>{props.layount(currentData)}</Row>
    <Pagination style={{float: "right"}} total={data.length}
                showTotal={(total) => `Total ${total} items`}
                defaultPageSize={props.defaultPageSize}
                defaultCurrent={currentPage}
                onChange={(page, pageSize) => {
                  setCurrentPage(page);
                  setCurrentPageSize(pageSize);
                }}
    />
  </>)
}

export default ListPagination;

import {connect} from "umi";
import {isSql} from "@/pages/DataStudio/HeaderContainer/service";
import {Button, Empty, Input, InputRef, Space, Table, Tag} from "antd";
import {FireOutlined, SearchOutlined} from "@ant-design/icons";
import {l} from "@/utils/intl";
import {getCurrentData, getCurrentTab, mapDispatchToProps} from "@/pages/DataStudio/function";
import {DataStudioParams, StateType, TabsPageType} from "@/pages/DataStudio/model";
import {useEffect, useRef, useState} from "react";
import {handleGetOption} from "@/services/BusinessCrud";
import {API_CONSTANTS} from "@/services/constants";
import {postAll} from "@/services/api";
import {ColumnsType, ColumnType} from "antd/es/table";
import {DataIndex} from "rc-table/es/interface";
import {FilterConfirmProps} from "antd/es/table/interface";
import {Highlight} from "@ant-design/pro-layout/es/components/Help/Search";
import {transformTableDataToCsv} from "@/utils/function";

type Data = {
  [c: string]: any,
  columns?: string[],
  rowData?: object[]
}
const Result = (props: any) => {

  const {saveTabs, tabs: {panes, activeKey}} = props;
  const [data, setData] = useState<Data>({})
  const [loading, setLoading] = useState<boolean>(true)
  const currentTabs = getCurrentTab(panes, activeKey);
  const current = getCurrentData(panes, activeKey);

  const [searchText, setSearchText] = useState('');
  const [searchedColumn, setSearchedColumn] = useState('');
  const searchInput = useRef<InputRef>(null);
  const handleReset = (clearFilters: () => void) => {
    clearFilters();
    setSearchText('');
  };
  const handleSearch = (
    selectedKeys: string[],
    confirm: (param?: FilterConfirmProps) => void,
    dataIndex: DataIndex,
  ) => {
    confirm();
    setSearchText(selectedKeys[0]);
    setSearchedColumn(dataIndex.toString());
  };
  const getColumnSearchProps = (dataIndex: string): ColumnType<Data> => ({
    filterDropdown: ({setSelectedKeys, selectedKeys, confirm, clearFilters}) => (
      <div style={{padding: 8}} onKeyDown={(e) => e.stopPropagation()}>
        <Input
          ref={searchInput}
          placeholder={`Search ${dataIndex}`}
          value={selectedKeys[0]}
          onChange={(e) => setSelectedKeys(e.target.value ? [e.target.value] : [])}
          onPressEnter={() => handleSearch(selectedKeys as string[], confirm, dataIndex)}
          style={{marginBottom: 8, display: 'block'}}
        />
        <Space>
          <Button
            type="primary"
            onClick={() => handleSearch(selectedKeys as string[], confirm, dataIndex)}
            icon={<SearchOutlined/>}
            size="small"
            style={{width: 90}}
          >
            {l("button.search")}
          </Button>
          <Button
            onClick={() => clearFilters && handleReset(clearFilters)}
            size="small"
            style={{width: 90}}
          >
            {l("button.reset")}
          </Button>
        </Space>
      </div>
    ),
    filterIcon: (filtered: boolean) => (
      <SearchOutlined style={{color: filtered ? '#1677ff' : undefined}}/>
    ),
    onFilter: (value, record) =>
      record[dataIndex]
        .toString()
        .toLowerCase()
        .includes((value as string).toLowerCase()),
    onFilterDropdownOpenChange: (visible) => {
      if (visible) {
        setTimeout(() => searchInput.current?.select(), 100);
      }
    },
    render: (text) =>
      searchedColumn === dataIndex ? (
        <Highlight label={text ? text.toString() : ''} words={[searchText]}/>
      ) : (
        text
      ),
  });


  const loadData = async (isRefresh?: boolean) => {
    if (!currentTabs) {
      return
    }
    if (currentTabs.type !== TabsPageType.project) {
      return
    }
    if ((currentTabs?.params as DataStudioParams).resultData && !isRefresh) {
      setData((currentTabs?.params as DataStudioParams).resultData)
    } else {
      if (isSql(current.dialect)) {
        // common sql
        const res = await handleGetOption("api/studio/getCommonSqlData", "Get Data", {taskId: (currentTabs?.params as DataStudioParams).taskId});
        if (res.datas) {
          (currentTabs?.params as DataStudioParams).resultData = res.datas
          saveTabs({...props.tabs})
          setData(res.datas)
        }
      } else {
        // flink sql
        if (current.jobInstanceId) {
          const res = await postAll(API_CONSTANTS.GET_JOB_BY_ID, {id: current.jobInstanceId});
          const jobData = res.datas;
          if ("unknown" !== jobData.status.toLowerCase()) {
            const jid = jobData.jid;
            const tableData = await handleGetOption("api/studio/getJobData", "Get Data", {jobId: jid});
            const datas = tableData.datas;
            datas.jid = jid;
            if (datas.success) {
              (currentTabs?.params as DataStudioParams).resultData = datas
              saveTabs({...props.tabs})
            }
          }
        }
      }
    }
    setLoading(false)
  }
  useEffect(() => {
    setData({})
    loadData()
  }, [currentTabs])


  const getColumns = (columns: string[]) => {
    return columns?.map((item) => {
      return {
        title: item, dataIndex: item,
        sorter: (a, b) => a[item] - b[item],
        ...getColumnSearchProps(item),
      };
    }) as ColumnsType<any>;
  };

  const showDetail = async () => {
    setLoading(true)
    await loadData(true);
    setLoading(false)

  };

  const renderFlinkSQLContent = () => {
    return (<>
      {current.jobInstanceId ? (<>
        <Space>
          <Button loading={loading} type="primary" onClick={showDetail} icon={<SearchOutlined/>}>
            {l('pages.datastudio.label.result.query.latest.data')}
          </Button>
          <Tag color="blue" key={data.jid}>
            <FireOutlined/> {data.jid}
          </Tag>
        </Space>
      </>) : undefined}
    </>)
  }
  const renderDownloadButton = () => {
    if (current && data.columns) {
      const _utf = "\uFEFF";
      const csvDataBlob = new Blob([_utf + transformTableDataToCsv(data.columns!, data.rowData!)], {
        type: "text/csv",
      });
      const url = URL.createObjectURL(csvDataBlob);
      return <Button type="link" href={url}>Export Csv</Button>
    }
    return undefined
  }


  return (
    <div style={{width: '100%'}}>
      <div style={{direction: "rtl"}}>
        {renderDownloadButton()}
        {current ? (isSql(current.dialect) ? <></> : renderFlinkSQLContent()) : undefined}
      </div>
      {data.columns ?
        <Table columns={getColumns(data.columns)} dataSource={data.rowData!.map((item: any, index: number) => {
          return {...item, key: index}
        })} loading={loading}/>
        : (<Empty image={Empty.PRESENTED_IMAGE_SIMPLE}/>)
      }
    </div>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  tabs: Studio.tabs,
}), mapDispatchToProps)(Result);

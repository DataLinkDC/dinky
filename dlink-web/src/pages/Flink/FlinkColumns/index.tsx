import React from "react";
import {CheckSquareOutlined, KeyOutlined} from '@ant-design/icons';
import DTable from "@/components/Common/DTable";
import {DIALECT} from "@/components/Studio/conf";

const FlinkColumns = (props: any) => {

  const {envId, catalog, database, table} = props;

  const cols = [{
    title: '序号',
    dataIndex: 'position',
    isString: false,
  },
    {
      title: '列名',
      dataIndex: 'name',
      copyable: true,
    },
    {
      title: '类型',
      dataIndex: 'type',
    },
    {
      title: '主键',
      dataIndex: 'key',
      render: (_, record) => (
        <>
          {record.key ? <KeyOutlined style={{color: '#FAA100'}}/> : undefined}
        </>
      ),
      filters: [
        {
          text: '主键',
          value: true,
        },
        {
          text: '其他',
          value: '',
        },
      ],
      openSearch: 'dict',
    }, {
      title: '可为空',
      dataIndex: 'nullable',
      render: (_, record) => (
        <>
          {record.nullable ? <CheckSquareOutlined style={{color: '#1296db'}}/> : undefined}
        </>
      ),
      filters: [
        {
          text: '可为空',
          value: true,
        },
        {
          text: '非空',
          value: '',
        },
      ],
      openSearch: 'dict',
    }, {
      title: '扩展',
      dataIndex: 'extras',
    }, {
      title: '水印',
      dataIndex: 'watermark',
    },];

  return (
    <DTable columns={cols}
            dataSource={{
              url: 'api/studio/getMSFlinkColumns', params: {
                envId,
                dialect: DIALECT.FLINKSQL,
                catalog,
                database,
                table
              }
            }}/>
  )
};

export default FlinkColumns;

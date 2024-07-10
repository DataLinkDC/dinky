import React from 'react';
import ColumnInfo from '@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter/SchemaDesc/ColumnInfo';
import { DataSources } from '@/types/RegCenter/data';

const GeneralJdbcDesc: React.FC<DataSources.SchemaDescProps> = (props) => {
  const { tableInfo, queryParams } = props;

  return <ColumnInfo columnInfo={tableInfo?.columns} />;
};

export default GeneralJdbcDesc;

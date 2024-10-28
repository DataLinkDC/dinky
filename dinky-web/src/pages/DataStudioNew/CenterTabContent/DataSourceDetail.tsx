import {CenterTab, LayoutState} from "@/pages/DataStudioNew/model";
import RightTagsRouter from "@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter";
import {QueryParams} from "@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter/data";
import {connect} from "@@/exports";
import {mapDispatchToProps} from "@/pages/DataStudioNew/DvaFunction";

 const  DataSourceDetail = (props:CenterTab) => {
  const {params} = props;
   const queryParams:QueryParams={id: params.selectDatabaseId, schemaName: params.schemaName, tableName: params.tableName}
  return <RightTagsRouter  queryParams={queryParams}  />;
}
export default connect(
  ({DataStudio}: { DataStudio: LayoutState }) => ({
  }), mapDispatchToProps)(DataSourceDetail);

import { StudioMetaStoreParam } from '@/pages/DataStudio/LeftContainer/Catalog/data';
import { postAll } from '@/services/api';
import { getDataByParamsReturnResult } from '@/services/BusinessCrud';

export async function getMSSchemaInfo(params: StudioMetaStoreParam) {
  return (await postAll('/api/studio/getMSSchemaInfo', params)).datas;
}
export async function getMSCatalogs(params: StudioMetaStoreParam) {
  return (await postAll('/api/studio/getMSCatalogs', params)).datas;
}
export async function getMSFlinkColumns(params: StudioMetaStoreParam) {
  return (await getDataByParamsReturnResult('/api/studio/getMSFlinkColumns', params)).datas;
}

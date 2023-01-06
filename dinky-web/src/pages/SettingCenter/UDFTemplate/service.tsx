import {handleRemoveById, postAll, handleAddOrUpdate} from "@/components/Common/crud";
import {UDFTemplateItem} from "@/pages/SettingCenter/UDFTemplate/data";

const url = '/api/udf/template/list';
const addUrl = '/api/udf/template/';

export async function getTemplate() {
  return await postAll(url);
}

export function deleteTemplate(id: number) {
  handleRemoveById(url, id)
}

export function addTemplate(params: UDFTemplateItem) {
  return handleAddOrUpdate(addUrl, params)
}

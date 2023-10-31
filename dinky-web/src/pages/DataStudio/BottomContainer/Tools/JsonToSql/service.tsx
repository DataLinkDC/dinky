import {postAll} from "@/services/api";


export async function jsonToSql(params: any) {
    return (await postAll('/api/tools/jsonToFlinkSql', params)).datas;
}
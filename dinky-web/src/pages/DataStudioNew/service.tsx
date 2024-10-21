import {handleOption} from "@/services/BusinessCrud";
import {API_CONSTANTS} from "@/services/endpoints";

export async function explainSql(title: string, params: any) {
  return handleOption(API_CONSTANTS.EXPLAIN_SQL, title, params);
}

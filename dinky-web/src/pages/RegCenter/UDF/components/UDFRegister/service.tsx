import {postAll} from "@/services/api";
import {API_CONSTANTS} from "@/services/endpoints";
import {Key} from "react";

export const add = (ids:Key[]) => {
    return postAll(API_CONSTANTS.UDF_ADD,{data:ids});
}
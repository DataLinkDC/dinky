import request from 'umi-request';
import type { TableListParams } from './data.d';
import {FlinkSqlTableListItem} from "./data.d";

export async function queryFlinkSql(params?: TableListParams) {
    return request('/api-dlink/flinkSql', {
        method: 'POST',
        data: {
            ...params,
        },
    });
}

export async function removeFlinkSql(params: number[]) {
    return request('/api-dlink/flinkSql', {
        method: 'DELETE',
        data: {
            ...params,
        },
    });
}

export async function addOrUpdateFlinkSql(params: FlinkSqlTableListItem) {
    return request('/api-dlink/flinkSql', {
        method: 'PUT',
        data: {
            ...params,
        },
    });
}


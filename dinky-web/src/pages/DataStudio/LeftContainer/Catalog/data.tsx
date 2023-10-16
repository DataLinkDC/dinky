import {DataNode} from "antd/es/tree";
import {DataSources} from "@/types/RegCenter/data";

export type StudioMetaStoreParam = {
    statement?: string,
    fragment?: boolean,
    dialect?: string,
    envId?: number,
    databaseId?: number,
    catalog?: string,
    database?: string,
    table?: string,
}

export type TableDataNode = {
    isTable: boolean,
} & DataNode &DataSources.Table
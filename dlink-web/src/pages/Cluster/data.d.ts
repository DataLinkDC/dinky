export type ClusterTableListItem = {
            id: number,
            name: string,
            alias: string,
            type: string,
            checkPoint: number,
            savePointPath: string,
            parallelism: number,
            fragment: boolean,
            clusterId: number,
            note: string,
            enabled: boolean,
            createTime: Date,
            updateTime: Date,
};

export type TableListPagination = {
    total: number;
    pageSize: number;
    current: number;
};

export type TableListData = {
    list: TableListItem[];
    pagination: Partial<TableListPagination>;
};

export type TableListParams = {
    status?: string;
    name?: string;
    desc?: string;
    key?: number;
    pageSize?: number;
    currentPage?: number;
    filter?: Record<string, any[]>;
    sorter?: Record<string, any>;
};

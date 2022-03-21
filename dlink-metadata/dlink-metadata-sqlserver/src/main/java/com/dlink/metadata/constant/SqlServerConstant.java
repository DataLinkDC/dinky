package com.dlink.metadata.constant;

/**
 * @operate sqlServer常量
 * @date 2022/1/26 14:11
 * @return
 */
public interface SqlServerConstant {

    /**
     * 添加注释模板SQL
     */
    String COMMENT_SQL = " EXECUTE sp_addextendedproperty N'MS_Description', N'%s', N'SCHEMA', N'%s', N'table', N'%S', N'column', N'%S' ";

    /**
     * 查询列信息模板SQL
     */
    String QUERY_COLUMNS_SQL = " SELECT  cast(a.name AS VARCHAR(500)) AS TABLE_NAME,cast(b.name AS VARCHAR(500)) AS COLUMN_NAME,  isnull(CAST ( c.VALUE AS NVARCHAR ( 500 ) ),'') AS COMMENTS, " +
            " CASE b.is_nullable WHEN 1 THEN 'YES' ELSE 'NO' END as NULLVALUE,cast(sys.types.name AS VARCHAR (500)) AS DATA_TYPE," +
            " ( SELECT CASE count(1) WHEN 1 then 'PRI' ELSE '' END FROM syscolumns,sysobjects,sysindexes,sysindexkeys,systypes  WHERE syscolumns.xusertype = systypes.xusertype " +
            " AND syscolumns.id = object_id (a.name) AND sysobjects.xtype = 'PK' AND sysobjects.parent_obj = syscolumns.id  " +
            " AND sysindexes.id = syscolumns.id  AND sysobjects.name = sysindexes.name AND sysindexkeys.id = syscolumns.id  AND sysindexkeys.indid = sysindexes.indid  AND syscolumns.colid = sysindexkeys.colid " +
            " AND syscolumns.name = b.name) as 'KEY',  b.is_identity isIdentity , '' as CHARACTER_SET_NAME, '' as COLLATION_NAME, 0 as ORDINAL_POSITION, 0 as NUMERIC_PRECISION, 0 as NUMERIC_SCALE, '' as AUTO_INCREMENT FROM ( select name,object_id from sys.tables UNION all select name,object_id from sys.views ) a  INNER JOIN sys.columns b " +
            " ON b.object_id = a.object_id  LEFT JOIN sys.types ON b.user_type_id = sys.types.user_type_id    LEFT JOIN sys.extended_properties c ON c.major_id = b.object_id AND c.minor_id = b.column_id " +
            "  WHERE a.name = '%s' and sys.types.name !='sysname' ";

    /**
     * 查询schema模板SQL
     */
    String QUERY_SCHEMA_SQL = " SELECT distinct table_schema   from INFORMATION_SCHEMA.tables ";

    /**
     * 根据schema查询table信息模板SQL
     */
    String QUERY_TABLE_BY_SCHEMA_SQL = " SELECT  table_name ,table_schema, '' as type, '' as CATALOG, '' as ENGINE , '' as OPTIONS ,0 as rows , null as CREATE_TIME, null as UPDATE_TIME,null AS COMMENTS  FROM INFORMATION_SCHEMA.tables WHERE TABLE_SCHEMA  = '%s' ";
}

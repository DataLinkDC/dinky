/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.dlink.metadata.query;

/**
 * PostgreSqlQuery
 *
 * @author wenmo
 * @since 2021/7/22 9:29
 **/
public class PostgreSqlQuery extends AbstractDBQuery {

    @Override
    public String schemaAllSql() {
        return "SELECT nspname AS \"schema_name\" FROM pg_namespace WHERE nspname NOT LIKE 'pg_%' AND nspname != 'information_schema' ORDER BY nspname";
    }

    @Override
    public String tablesSql(String schemaName) {
        return "SELECT n.nspname                                                        AS schema_name,\n"
                + "       c.relname                                                        AS tablename,\n"
                + "       pg_get_userbyid(c.relowner)                                      AS tableowner,\n"
                + "       obj_description(c.oid)                                           AS comments,\n"
                + "       c.relkind,\n"
                + "       c.relhasindex                                                    AS hasindexes,\n"
                + "       c.relhasrules                                                    AS hasrules,\n"
                + "       t.spcname                                                        AS tablespace,\n"
                + "       c.reloptions                                                     AS param,\n"
                + "       c.relhastriggers                                                 AS hastriggers,\n"
                + "       c.relpersistence                                                 AS unlogged,\n"
                + "       ft.ftoptions,\n"
                + "       fs.srvname,\n"
                + "       c.reltuples                                                      as rows,\n"
                + "       ((SELECT count(*) FROM pg_inherits WHERE inhparent = c.oid) > 0) AS inhtable,\n"
                + "       i2.nspname                                                       AS inhschemaname,\n"
                + "       i2.relname                                                       AS inhtablename\n"
                + "FROM pg_class c\n"
                + "         LEFT JOIN pg_namespace n ON n.oid = c.relnamespace\n"
                + "         LEFT JOIN pg_tablespace t ON t.oid = c.reltablespace\n"
                + "         LEFT JOIN (pg_inherits i INNER JOIN pg_class c2 ON i.inhparent = c2.oid LEFT JOIN pg_namespace n2\n"
                + ""
                + "                    ON n2.oid = c2.relnamespace) i2 ON i2.inhrelid = c.oid\n"
                + "         LEFT JOIN pg_index ind ON (ind.indrelid = c.oid) and (ind.indisclustered = 't')\n"
                + "         LEFT JOIN pg_class ci ON ci.oid = ind.indexrelid\n"
                + "         LEFT JOIN pg_foreign_table ft ON ft.ftrelid = c.oid\n"
                + "         LEFT JOIN pg_foreign_server fs ON ft.ftserver = fs.oid\n"
                + "WHERE ((c.relkind = 'r'::\"char\") OR (c.relkind = 'f'::\"char\") OR (c.relkind = 'p'::\"char\"))\n"
                + "  AND n.nspname = '" + schemaName + "'\n"
                + "ORDER BY n.nspname, tablename";
    }

    @Override
    public String columnsSql(String schemaName, String tableName) {
        return "SELECT col.column_name                              as name,\n"
                + "       col.character_maximum_length                 as length,\n"
                + "       col.is_nullable,\n"
                + "       col.numeric_precision                        as numeric_precision,\n"
                + "       col.numeric_scale                            as numeric_scale,\n"
                + "       col.datetime_precision,\n"
                + "       col.ordinal_position,\n"
                + "       b.atttypmod,\n"
                + "       b.attndims,\n"
                + "       et.typelem,\n"
                + "       et.typlen,\n"
                + "       et.typtype,\n"
                + "       nbt.nspname                                  AS elem_schema,\n"
                + "       bt.typname                                   AS elem_name,\n"
                + "       b.atttypid,\n"
                + "       col.udt_schema,\n"
                + "       col.udt_name                                 as type,\n"
                + "       (CASE\n"
                + "            WHEN (SELECT COUNT(*) FROM pg_constraint AS PC WHERE b.attnum = PC.conkey[1] AND PC.contype = 'p') > 0\n"
                + "                THEN 'PRI'\n"
                + "            ELSE '' END)                            AS key,\n"
                + "       col.domain_catalog,\n"
                + "       col.domain_schema,\n"
                + "       col.domain_name,\n"
                + "       col_description(c.oid, col.ordinal_position) AS comment,\n"
                + "       col.column_default                           AS column_default,\n"
                + "       b.attacl,\n"
                + "       colnsp.nspname                               AS collation_schema_name,\n"
                + "       coll.collname,\n"
                + "       c.relkind,\n"
                + "       b.attfdwoptions                              AS foreign_options\n"
                + "FROM information_schema.columns AS col\n"
                + "         LEFT JOIN pg_namespace ns ON ns.nspname = col.table_schema\n"
                + "         LEFT JOIN pg_class c ON col.table_name = c.relname AND c.relnamespace = ns.oid\n"
                + "         LEFT JOIN pg_attrdef a ON c.oid = a.adrelid AND col.ordinal_position = a.adnum\n"
                + "         LEFT JOIN pg_attribute b ON b.attrelid = c.oid AND b.attname = col.column_name\n"
                + "         LEFT JOIN pg_type et ON et.oid = b.atttypid\n"
                + "         LEFT JOIN pg_collation coll ON coll.oid = b.attcollation\n"
                + "         LEFT JOIN pg_namespace colnsp ON coll.collnamespace = colnsp.oid\n"
                + "         LEFT JOIN pg_type bt ON et.typelem = bt.oid\n"
                + "         LEFT JOIN pg_namespace nbt ON bt.typnamespace = nbt.oid\n"
                + "WHERE col.table_schema = '" + schemaName + "'\n"
                + "  AND col.table_name = '" + tableName + "'\n"
                + "ORDER BY col.table_schema, col.table_name, col.ordinal_position";
    }

    @Override
    public String schemaName() {
        return "schema_name";
    }

    @Override
    public String tableName() {
        return "tablename";
    }

    @Override
    public String tableComment() {
        return "comments";
    }

    @Override
    public String rows() {
        return "rows";
    }

    @Override
    public String columnName() {
        return "name";
    }

    @Override
    public String columnType() {
        return "type";
    }

    @Override
    public String columnComment() {
        return "comment";
    }

    @Override
    public String columnKey() {
        return "key";
    }

    @Override
    public String precision() {
        return "numeric_precision";
    }

    @Override
    public String scale() {
        return "numeric_scale";
    }

    @Override
    public String columnPosition() {
        return "ordinal_position";
    }

    @Override
    public String defaultValue() {
        return "column_default";
    }

    @Override
    public String isNullable() {
        return "is_nullable";
    }
}

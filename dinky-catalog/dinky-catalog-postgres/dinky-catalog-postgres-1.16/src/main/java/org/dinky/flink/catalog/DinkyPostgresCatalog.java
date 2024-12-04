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

package org.dinky.flink.catalog;

import static org.apache.flink.util.Preconditions.checkArgument;
import static org.apache.flink.util.Preconditions.checkNotNull;

import org.dinky.flink.catalog.factory.DinkyPostgresCatalogFactoryOptions;

import org.apache.flink.table.api.Schema;
import org.apache.flink.table.catalog.AbstractCatalog;
import org.apache.flink.table.catalog.CatalogBaseTable;
import org.apache.flink.table.catalog.CatalogDatabase;
import org.apache.flink.table.catalog.CatalogDatabaseImpl;
import org.apache.flink.table.catalog.CatalogFunction;
import org.apache.flink.table.catalog.CatalogFunctionImpl;
import org.apache.flink.table.catalog.CatalogPartition;
import org.apache.flink.table.catalog.CatalogPartitionSpec;
import org.apache.flink.table.catalog.CatalogTable;
import org.apache.flink.table.catalog.CatalogView;
import org.apache.flink.table.catalog.FunctionLanguage;
import org.apache.flink.table.catalog.ObjectPath;
import org.apache.flink.table.catalog.ResolvedCatalogBaseTable;
import org.apache.flink.table.catalog.ResolvedCatalogTable;
import org.apache.flink.table.catalog.ResolvedCatalogView;
import org.apache.flink.table.catalog.exceptions.CatalogException;
import org.apache.flink.table.catalog.exceptions.DatabaseAlreadyExistException;
import org.apache.flink.table.catalog.exceptions.DatabaseNotEmptyException;
import org.apache.flink.table.catalog.exceptions.DatabaseNotExistException;
import org.apache.flink.table.catalog.exceptions.FunctionAlreadyExistException;
import org.apache.flink.table.catalog.exceptions.FunctionNotExistException;
import org.apache.flink.table.catalog.exceptions.PartitionAlreadyExistsException;
import org.apache.flink.table.catalog.exceptions.PartitionNotExistException;
import org.apache.flink.table.catalog.exceptions.PartitionSpecInvalidException;
import org.apache.flink.table.catalog.exceptions.TableAlreadyExistException;
import org.apache.flink.table.catalog.exceptions.TableNotExistException;
import org.apache.flink.table.catalog.exceptions.TableNotPartitionedException;
import org.apache.flink.table.catalog.exceptions.TablePartitionedException;
import org.apache.flink.table.catalog.stats.CatalogColumnStatistics;
import org.apache.flink.table.catalog.stats.CatalogTableStatistics;
import org.apache.flink.table.expressions.Expression;
import org.apache.flink.table.types.DataType;
import org.apache.flink.util.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;

/**
 * Custom catalog checks connection done. The default db will be forcibly specified. No matter what is entered, it will be specified as default_database.
 * The configuration file information can be read to obtain the database connection instead of forcing it to be specified in the sql statement.
 */
public class DinkyPostgresCatalog extends AbstractCatalog {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String POSTGRES_DRIVER = "org.postgresql.Driver";

    public static final String DEFAULT_DATABASE = "default_database";

    static {
        try {
            Class.forName(POSTGRES_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new CatalogException("PG DRIVER NOT LOADED！", e);
        }
    }

    private static final String COMMENT = "comment";
    /**
     * Determine whether a SQL exception has occurred. If so, conn may be invalid. Pay attention to judgment
     */
    private boolean sqlExceptionHappened = false;

    /**
     * Object type, such as library, table, view, etc.
     */
    protected static class ObjectType {

        /**
         * DATABASE
         */
        public static final String DATABASE = "database";

        /**
         * TABLE
         */
        public static final String TABLE = "TABLE";

        /**
         * VIEW
         */
        public static final String VIEW = "VIEW";
    }

    /**
     * Object type, such as library, table, view, etc.
     */
    protected static class ColumnType {

        /**
         * PHYSICAL FIELD
         */
        public static final String PHYSICAL = "physical";

        /**
         * Calculated field
         */
        public static final String COMPUTED = "computed";

        /**
         * METADATA FIELDS
         */
        public static final String METADATA = "metadata";

        /**
         * watermark
         */
        public static final String WATERMARK = "watermark";
    }

    @Getter
    private final String user;

    @Getter
    private final String pwd;

    @Getter
    private final String url;

    /**
     * 默认database
     */
    private static final String defaultDatabase = "default_database";

    /**
     * Constructor method of DinkyPostgresCatalog class
     *
     * @param name database name
     * @param url  database connection URL
     * @param user database user name
     * @param pwd  database password
     */
    public DinkyPostgresCatalog(String name, String url, String user, String pwd) {
        super(name, defaultDatabase);
        this.url = url;
        this.user = user;
        this.pwd = pwd;
    }

    /**
     * Constructor method of DinkyPostgresCatalog class, initializes the object using default database connection information
     *
     * @param name database name
     */
    public DinkyPostgresCatalog(String name) {
        super(name, defaultDatabase);
        this.url = DinkyPostgresCatalogFactoryOptions.URL.defaultValue();
        this.user = DinkyPostgresCatalogFactoryOptions.USERNAME.defaultValue();
        this.pwd = DinkyPostgresCatalogFactoryOptions.PASSWORD.defaultValue();
    }

    /**
     * Open Catalog, check and create the default database
     *
     * @throws CatalogException This exception is thrown if an error occurs while opening the Catalog
     */
    @Override
    public void open() throws CatalogException {
        // Verify whether the connection is valid
        // Get the default db to see if it exists
        Integer defaultDbId = getDatabaseId(defaultDatabase);
        if (defaultDbId == null) {
            try {
                createDatabase(defaultDatabase, new CatalogDatabaseImpl(new HashMap<>(), ""), true);
            } catch (DatabaseAlreadyExistException a) {
                logger.info("Repeat the creation of the default library");
            }
        }
    }

    /**
     * Close the Catalog connection.
     * <p>
     * If a connection currently exists, try to close it. If a SQL exception occurs when closing the connection, the exception is logged and a CatalogException is thrown.
     *
     * @throws CatalogException This exception is thrown if an error occurs while closing the connection
     */
    @Override
    public void close() throws CatalogException {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                sqlExceptionHappened = true;
                throw new CatalogException("Fail to close connection.", e);
            }
        }
    }

    private Connection connection;

    /**
     * Get the database connection.
     * <p>
     * If there is currently no connection, try to obtain a new connection through DriverManager.
     * If a SQLException has occurred before, first check whether the current connection is valid, and if it is invalid, close and reacquire the connection.
     * If the connection was closed, reacquire the connection.
     *
     * @return Returns the database connection object
     * @throws CatalogException This exception is thrown if an error occurs while obtaining the connection
     */
    protected Connection getConnection() throws CatalogException {
        try {
            // todo: Wrap a method to obtain the connection to facilitate subsequent transformation and use other
            // connection generation.
            // Class.forName(MYSQL_DRIVER);
            if (connection == null) {
                connection = DriverManager.getConnection(url, user, pwd);
            }
            if (sqlExceptionHappened) {
                sqlExceptionHappened = false;
                if (!connection.isValid(10)) {
                    connection.close();
                }
                if (connection.isClosed()) {
                    connection = null;
                    return getConnection();
                }
                connection = null;
                return getConnection();
            }

            return connection;
        } catch (Exception e) {
            throw new CatalogException("Fail to get connection.", e);
        }
    }

    /**
     * List all database names in the current Catalog.
     *
     * @return a list containing all database names
     * @throws CatalogException This exception is thrown if an error occurs while listing the database
     */
    @Override
    public List<String> listDatabases() throws CatalogException {
        List<String> myDatabases = new ArrayList<>();
        String querySql = "SELECT database_name FROM metadata_database";
        Connection conn = getConnection();
        try (PreparedStatement ps = conn.prepareStatement(querySql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String dbName = rs.getString(1);
                myDatabases.add(dbName);
            }

            return myDatabases;
        } catch (Exception e) {
            throw new CatalogException(String.format("Failed listing database in catalog %s", getName()), e);
        }
    }

    /**
     * Get the CatalogDatabase object based on the database name.
     *
     * @param databaseName database name
     * @return CatalogDatabase object, containing the description and properties of the database
     * @throws DatabaseNotExistException If the database does not exist, this exception is thrown
     * @throws CatalogException          This exception is thrown if an error occurs while retrieving the database
     */
    @Override
    public CatalogDatabase getDatabase(String databaseName) throws DatabaseNotExistException, CatalogException {
        String querySql = "SELECT id, database_name,description  FROM metadata_database where database_name=?";
        Connection conn = getConnection();
        try (PreparedStatement ps = conn.prepareStatement(querySql)) {
            ps.setString(1, databaseName);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                String description = rs.getString("description");

                Map<String, String> map = new HashMap<>();

                String sql = "select \"key\", \"value\"  from metadata_database_property where database_id=? ";
                try (PreparedStatement pStat = conn.prepareStatement(sql)) {
                    pStat.setInt(1, id);
                    ResultSet prs = pStat.executeQuery();
                    while (prs.next()) {
                        map.put(prs.getString("key"), prs.getString("value"));
                    }
                } catch (SQLException e) {
                    sqlExceptionHappened = true;
                    throw new CatalogException(
                            String.format("Failed get database properties in catalog %s", getName()), e);
                }

                return new CatalogDatabaseImpl(map, description);
            } else {
                throw new DatabaseNotExistException(getName(), databaseName);
            }
        } catch (SQLException e) {
            sqlExceptionHappened = true;
            throw new CatalogException(String.format("Failed get database in catalog %s", getName()), e);
        }
    }

    /**
     * Determine whether the database with the specified name exists.
     *
     * @param databaseName database name
     * @return If the database exists, return true; otherwise return false
     * @throws CatalogException If an error occurs when determining whether the database exists, this exception is thrown
     */
    @Override
    public boolean databaseExists(String databaseName) throws CatalogException {
        return getDatabaseId(databaseName) != null;
    }

    /**
     * Get the ID based on the database name.
     *
     * @param databaseName database name
     * @return If the database exists, return its ID; otherwise return null
     * @throws CatalogException This exception is thrown if an error occurs while getting the database ID
     */
    private Integer getDatabaseId(String databaseName) throws CatalogException {
        String querySql = "select id from metadata_database where database_name=?";
        Connection conn = getConnection();
        try (PreparedStatement ps = conn.prepareStatement(querySql)) {
            ps.setString(1, databaseName);
            ResultSet rs = ps.executeQuery();
            boolean multiDB = false;
            Integer id = null;
            while (rs.next()) {
                if (!multiDB) {
                    id = rs.getInt(1);
                    multiDB = true;
                } else {
                    throw new CatalogException("There are multiple databases with the same name: " + databaseName);
                }
            }
            return id;
        } catch (SQLException e) {
            sqlExceptionHappened = true;
            throw new CatalogException(
                    String.format("Failed to obtain database information：%s.%s", getName(), databaseName), e);
        }
    }

    /**
     * Create a new database.
     *
     * @param databaseName   database name
     * @param db             database object, including database description and properties
     * @param ignoreIfExists If the database already exists, whether to ignore the creation operation
     * @throws DatabaseAlreadyExistException This exception is thrown if the database already exists and ignoreIfExists is false
     * @throws CatalogException              This exception is thrown if an error occurs while creating the database
     */
    @Override
    public void createDatabase(String databaseName, CatalogDatabase db, boolean ignoreIfExists)
            throws DatabaseAlreadyExistException, CatalogException {

        checkArgument(!StringUtils.isNullOrWhitespaceOnly(databaseName));
        checkNotNull(db);
        if (databaseExists(databaseName)) {
            if (!ignoreIfExists) {
                throw new DatabaseAlreadyExistException(getName(), databaseName);
            }
        } else {
            // Implement the code to create the library here
            Connection conn = getConnection();
            // Start transaction
            String insertSql = "insert into metadata_database(database_name, description) values(?, ?)";

            try (PreparedStatement stat = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                conn.setAutoCommit(false);
                stat.setString(1, databaseName);
                stat.setString(2, db.getComment());
                stat.executeUpdate();
                ResultSet idRs = stat.getGeneratedKeys();
                if (idRs.next()
                        && db.getProperties() != null
                        && db.getProperties().size() > 0) {
                    int id = idRs.getInt(1);
                    String propInsertSql =
                            "insert into metadata_database_property(database_id,key, value) values (?,?,?)";
                    PreparedStatement pstat = conn.prepareStatement(propInsertSql);
                    for (Map.Entry<String, String> entry : db.getProperties().entrySet()) {
                        pstat.setInt(1, id);
                        pstat.setString(2, entry.getKey());
                        pstat.setString(3, entry.getValue());
                        pstat.addBatch();
                    }
                    pstat.executeBatch();
                    pstat.close();
                }
                conn.commit();
            } catch (SQLException e) {
                sqlExceptionHappened = true;
                logger.error("Failed to create database information：", e);
            }
        }
    }

    /**
     * Delete the database with the specified name.
     *
     * @param name              database name
     * @param ignoreIfNotExists If the database does not exist, whether to ignore the delete operation
     * @param cascade           Whether to cascade delete all tables in the database
     * @throws DatabaseNotExistException This exception is thrown if the database does not exist and ignoreIfNotExists is false
     * @throws DatabaseNotEmptyException This exception is thrown if the database is not empty and cascade delete is not selected
     * @throws CatalogException          This exception is thrown if an error occurs while deleting the database
     */
    @Override
    public void dropDatabase(String name, boolean ignoreIfNotExists, boolean cascade)
            throws DatabaseNotExistException, DatabaseNotEmptyException, CatalogException {
        if (name.equals(defaultDatabase)) {
            throw new CatalogException("The default database cannot be deleted");
        }

        Integer id = getDatabaseId(name);
        if (id == null) {
            if (!ignoreIfNotExists) {
                throw new DatabaseNotExistException(getName(), name);
            }
            return;
        }
        Connection conn = getConnection();
        try {
            conn.setAutoCommit(false);
            // Query whether there is a table
            List<String> tables = listTables(name);
            if (!tables.isEmpty()) {
                if (!cascade) {
                    // There is a table and no cascading delete is performed.
                    throw new DatabaseNotEmptyException(getName(), name);
                }
                // Do cascade delete
                for (String table : tables) {
                    try {
                        dropTable(new ObjectPath(name, table), true);
                    } catch (TableNotExistException t) {
                        logger.warn("Table {} does not exist", name + "." + table);
                    }
                }
            }
            // todo: Now it is actually deleted, whether records will be retained for subsequent designs.
            String deletePropSql = "delete from metadata_database_property where database_id=?";
            PreparedStatement dStat = conn.prepareStatement(deletePropSql);
            dStat.setInt(1, id);
            dStat.executeUpdate();
            dStat.close();
            String deleteDbSql = "delete from metadata_database where id=?";
            dStat = conn.prepareStatement(deleteDbSql);
            dStat.setInt(1, id);
            dStat.executeUpdate();
            dStat.close();
            conn.commit();
        } catch (SQLException e) {
            sqlExceptionHappened = true;
            throw new CatalogException("Failed to delete database information：", e);
        }
    }

    /**
     * Modify the database information of the specified name.
     *
     * @param name              database name
     * @param newDb             new database information, including description and properties
     * @param ignoreIfNotExists If the database does not exist, whether to ignore modification operations
     * @throws DatabaseNotExistException This exception is thrown if the database does not exist and ignoreIfNotExists is false
     * @throws CatalogException          This exception is thrown if an error occurs while modifying database information
     */
    @Override
    public void alterDatabase(String name, CatalogDatabase newDb, boolean ignoreIfNotExists)
            throws DatabaseNotExistException, CatalogException {
        if (name.equals(defaultDatabase)) {
            throw new CatalogException("The default database cannot be modified");
        }
        // 1、Take out the db id, if it does not exist, throw an exception
        Integer id = getDatabaseId(name);
        if (id == null) {
            if (!ignoreIfNotExists) {
                throw new DatabaseNotExistException(getName(), name);
            }
            return;
        }
        Connection conn = getConnection();
        try {
            conn.setAutoCommit(false);
            // 1. The name cannot be changed and the type cannot be changed. Only notes can be changed
            String updateCommentSql = "update metadata_database set description=? where id=?";
            PreparedStatement uState = conn.prepareStatement(updateCommentSql);
            uState.setString(1, newDb.getComment());
            uState.setInt(2, id);
            uState.executeUpdate();
            uState.close();
            if (newDb.getProperties() != null && newDb.getProperties().size() > 0) {
                String upsertSql = "insert  into metadata_database_property (database_id, key, value) "
                        + "values (?,?,?) "
                        + "on CONFLICT (database_id, \"key\") do update set value = excluded.value, update_time = now()";
                PreparedStatement pstat = conn.prepareStatement(upsertSql);
                for (Map.Entry<String, String> entry : newDb.getProperties().entrySet()) {
                    pstat.setInt(1, id);
                    pstat.setString(2, entry.getKey());
                    pstat.setString(3, entry.getValue());
                    pstat.addBatch();
                }

                pstat.executeBatch();
            }
            conn.commit();
        } catch (SQLException e) {
            sqlExceptionHappened = true;
            throw new CatalogException("Failed to modify database information：", e);
        }
    }

    /**
     * List all table names in the specified database.
     *
     * @param databaseName database name
     * @return a list containing all table names
     * @throws DatabaseNotExistException If the database does not exist, this exception is thrown
     * @throws CatalogException          If an error occurs while listing table names, this exception is thrown
     */
    @Override
    public List<String> listTables(String databaseName) throws DatabaseNotExistException, CatalogException {
        return listTablesViews(databaseName, ObjectType.TABLE);
    }
    /**
     * List all view names in the specified database.
     *
     * @param databaseName database name
     * @return a list containing all view names
     * @throws DatabaseNotExistException If the database does not exist, this exception is thrown
     * @throws CatalogException This exception is thrown if an error occurs while listing the view names
     */
    @Override
    public List<String> listViews(String databaseName) throws DatabaseNotExistException, CatalogException {
        return listTablesViews(databaseName, ObjectType.VIEW);
    }

    /**
     * List the table or view names in the specified database.
     *
     * @param databaseName database name
     * @param tableType    The type of table or view (TABLE or VIEW)
     * @return a list containing all table or view names
     * @throws DatabaseNotExistException If the database does not exist, this exception is thrown
     * @throws CatalogException          Thrown if an error occurs while listing table or view names
     */
    protected List<String> listTablesViews(String databaseName, String tableType)
            throws DatabaseNotExistException, CatalogException {
        Integer databaseId = getDatabaseId(databaseName);
        if (null == databaseId) {
            throw new DatabaseNotExistException(getName(), databaseName);
        }

        // get all schemas
        // To give table or view
        String querySql = "SELECT table_name FROM metadata_table where table_type=? and database_id = ?";
        Connection conn = getConnection();
        try (PreparedStatement ps = conn.prepareStatement(querySql)) {
            ps.setString(1, tableType);
            ps.setInt(2, databaseId);
            ResultSet rs = ps.executeQuery();

            List<String> tables = new ArrayList<>();

            while (rs.next()) {
                String table = rs.getString(1);
                tables.add(table);
            }
            return tables;
        } catch (Exception e) {
            throw new CatalogException(String.format("Failed listing %s in catalog %s", tableType, getName()), e);
        }
    }

    /**
     * Get table details based on the table path.
     *
     * @param tablePath table path
     * @return CatalogBaseTable object, containing table details
     * @throws TableNotExistException If the table does not exist, this exception is thrown
     * @throws CatalogException       This exception is thrown if an error occurs while getting table information
     */
    @Override
    public CatalogBaseTable getTable(ObjectPath tablePath) throws TableNotExistException, CatalogException {
        // Still do it in steps
        // 1. Take out the table first. This may be a view or a table.
        // 2. Take out the column
        // 3. Get the attributes
        Integer id = getTableId(tablePath);

        if (id == null) {
            throw new TableNotExistException(getName(), tablePath);
        }

        Connection conn = getConnection();
        try {
            String queryTable = "SELECT table_name   ,description, table_type  FROM metadata_table  where  id=?";
            PreparedStatement ps = conn.prepareStatement(queryTable);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            String description;
            String tableType;
            if (rs.next()) {
                description = rs.getString("description");
                tableType = rs.getString("table_type");
                ps.close();
            } else {
                ps.close();
                throw new TableNotExistException(getName(), tablePath);
            }
            if (tableType.equals(ObjectType.TABLE)) {
                // This is table
                String propSql = "SELECT \"key\", \"value\" from metadata_table_property " + "WHERE table_id=?";
                PreparedStatement pState = conn.prepareStatement(propSql);
                pState.setInt(1, id);
                ResultSet prs = pState.executeQuery();
                Map<String, String> props = new HashMap<>();
                while (prs.next()) {
                    String key = prs.getString("key");
                    String value = prs.getString("value");
                    props.put(key, value);
                }
                pState.close();
                props.put(COMMENT, description);
                return CatalogTable.fromProperties(props);
            } else if (tableType.equals(ObjectType.VIEW)) {
                // 1. Get table information from the library. (Already done before)
                // 2. Take out the field.
                String colSql =
                        "SELECT column_name, column_type, data_type, description  FROM metadata_column WHERE  table_id=?";
                PreparedStatement cStat = conn.prepareStatement(colSql);
                cStat.setInt(1, id);
                ResultSet crs = cStat.executeQuery();

                Schema.Builder builder = Schema.newBuilder();
                while (crs.next()) {
                    String colName = crs.getString("column_name");
                    String dataType = crs.getString("data_type");

                    builder.column(colName, dataType);
                    String cDesc = crs.getString("description");
                    if (null != cDesc && !cDesc.isEmpty()) {
                        builder.withComment(cDesc);
                    }
                }
                cStat.close();
                // 3、Take out the query
                String qSql = "SELECT \"key\", \"value\" FROM metadata_table_property  WHERE table_id=? ";
                PreparedStatement qStat = conn.prepareStatement(qSql);
                qStat.setInt(1, id);
                ResultSet qrs = qStat.executeQuery();
                String originalQuery = "";
                String expandedQuery = "";
                Map<String, String> options = new HashMap<>();
                while (qrs.next()) {
                    String key = qrs.getString("key");
                    String value = qrs.getString("value");
                    if ("OriginalQuery".equals(key)) {
                        originalQuery = value;
                    } else if ("ExpandedQuery".equals(key)) {
                        expandedQuery = value;
                    } else {
                        options.put(key, value);
                    }
                }
                // Synthetic view
                return CatalogView.of(builder.build(), description, originalQuery, expandedQuery, options);
            } else {
                throw new CatalogException("Unsupported data type。" + tableType);
            }
        } catch (SQLException e) {
            sqlExceptionHappened = true;
            throw new CatalogException("Failed to obtain table information。", e);
        }
    }

    /**
     * Determine whether the table at the specified path exists.
     *
     * @param tablePath table path
     * @return Returns true if the table exists; otherwise returns false
     * @throws CatalogException If an error occurs when determining whether the table exists, this exception is thrown
     */
    @Override
    public boolean tableExists(ObjectPath tablePath) throws CatalogException {
        Integer id = getTableId(tablePath);
        return id != null;
    }

    /**
     * Get the table ID based on the table path.
     *
     * @param tablePath table path object, including database name and table name
     * @return If the table exists, return its ID; otherwise return null
     * @throws CatalogException This exception is thrown if an error occurs while getting the table ID
     */
    private Integer getTableId(ObjectPath tablePath) {
        Integer dbId = getDatabaseId(tablePath.getDatabaseName());
        if (dbId == null) {
            return null;
        }
        // 获取id
        String getIdSql = "select id from metadata_table   where table_name=? and database_id=?";
        Connection conn = getConnection();
        try (PreparedStatement gStat = conn.prepareStatement(getIdSql)) {
            gStat.setString(1, tablePath.getObjectName());
            gStat.setInt(2, dbId);
            ResultSet rs = gStat.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            sqlExceptionHappened = true;
            logger.error("get table fail", e);
            throw new CatalogException("get table fail.", e);
        }
        return null;
    }

    /**
     * Delete the table at the specified path.
     *
     * @param tablePath         table path object, including database name and table name
     * @param ignoreIfNotExists If the table does not exist, whether to ignore the delete operation
     * @throws TableNotExistException This exception is thrown if the table does not exist and ignoreIfNotExists is false
     * @throws CatalogException       This exception is thrown if an error occurs while deleting the table
     */
    @Override
    public void dropTable(ObjectPath tablePath, boolean ignoreIfNotExists)
            throws TableNotExistException, CatalogException {
        Integer id = getTableId(tablePath);

        if (id == null) {
            throw new TableNotExistException(getName(), tablePath);
        }
        Connection conn = getConnection();
        try {
            // todo: Now it is actually deleted, whether records will be retained for subsequent designs.
            conn.setAutoCommit(false);
            String deletePropSql = "delete from metadata_table_property  where table_id=?";
            PreparedStatement dStat = conn.prepareStatement(deletePropSql);
            dStat.setInt(1, id);
            dStat.executeUpdate();
            dStat.close();
            String deleteColSql = "delete from metadata_column  where table_id=?";
            dStat = conn.prepareStatement(deleteColSql);
            dStat.setInt(1, id);
            dStat.executeUpdate();
            dStat.close();
            String deleteDbSql = "delete from metadata_table   where id=?";
            dStat = conn.prepareStatement(deleteDbSql);
            dStat.setInt(1, id);
            dStat.executeUpdate();
            dStat.close();
            conn.commit();
        } catch (SQLException e) {
            sqlExceptionHappened = true;
            logger.error("drop table fail", e);
            throw new CatalogException("drop table fail.", e);
        }
    }

    /**
     * Rename the table at the specified path.
     *
     * @param tablePath         table path object, including database name and original table name
     * @param newTableName      new table name
     * @param ignoreIfNotExists If the table does not exist, whether to ignore the rename operation
     * @throws TableNotExistException     This exception is thrown if the table does not exist and ignoreIfNotExists is false
     * @throws TableAlreadyExistException If the new table name already exists, this exception is thrown
     * @throws CatalogException           This exception is thrown if an error occurs while renaming the table
     */
    @Override
    public void renameTable(ObjectPath tablePath, String newTableName, boolean ignoreIfNotExists)
            throws TableNotExistException, TableAlreadyExistException, CatalogException {
        Integer id = getTableId(tablePath);

        if (id == null) {
            throw new TableNotExistException(getName(), tablePath);
        }
        ObjectPath newPath = new ObjectPath(tablePath.getDatabaseName(), newTableName);
        if (tableExists(newPath)) {
            throw new TableAlreadyExistException(getName(), newPath);
        }
        String updateSql = "UPDATE metadata_table SET table_name=? WHERE id=?";
        Connection conn = getConnection();
        try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
            ps.setString(1, newTableName);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            sqlExceptionHappened = true;
            throw new CatalogException("Failed to modify table name", ex);
        }
    }

    /**
     * Create a new table.
     *
     * @param tablePath      table path object, including database name and table name
     * @param table          table object, including the structure and attribute information of the table
     * @param ignoreIfExists If the table already exists, whether to ignore the creation operation
     * @throws TableAlreadyExistException This exception is thrown if the table already exists and ignoreIfExists is false
     * @throws DatabaseNotExistException  If the database does not exist, this exception is thrown
     * @throws CatalogException           This exception is thrown if an error occurs while creating the table
     */
    @Override
    public void createTable(ObjectPath tablePath, CatalogBaseTable table, boolean ignoreIfExists)
            throws TableAlreadyExistException, DatabaseNotExistException, CatalogException {
        Integer dbId = getDatabaseId(tablePath.getDatabaseName());
        if (null == dbId) {
            throw new DatabaseNotExistException(getName(), tablePath.getDatabaseName());
        }
        if (tableExists(tablePath)) {
            if (!ignoreIfExists) {
                throw new TableAlreadyExistException(getName(), tablePath);
            }
            return;
        }
        // Insert table
        // Insert into table table. Here, it may be a table or a view
        // If it is a table, we think it is a resolved table, so we can use properties to serialize and save it.
        // If it is a view, we think it can only have physical fields
        if (!(table instanceof ResolvedCatalogBaseTable)) {
            throw new UnsupportedOperationException(
                    "The input of non-ResolvedCatalogBaseTable type tables is temporarily not supported.");
        }
        Connection conn = getConnection();
        try {
            conn.setAutoCommit(false);
            // First insert table information
            CatalogBaseTable.TableKind kind = table.getTableKind();

            String insertSql = "insert into metadata_table(\n"
                    + " table_name,"
                    + " table_type,"
                    + " database_id,"
                    + " description)"
                    + " values(?,?,?,?)";
            PreparedStatement iStat = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            iStat.setString(1, tablePath.getObjectName());
            iStat.setString(2, kind.toString());
            iStat.setInt(3, dbId);
            iStat.setString(4, table.getComment());
            iStat.executeUpdate();
            ResultSet idRs = iStat.getGeneratedKeys();
            if (!idRs.next()) {
                iStat.close();
                throw new CatalogException("Failed to insert metadata table information");
            }
            int id = idRs.getInt(1);
            iStat.close();
            // Insert attributes and columns
            if (table instanceof ResolvedCatalogTable) {
                // table You can get the properties directly。
                Map<String, String> props = ((ResolvedCatalogTable) table).toProperties();
                String propInsertSql = "insert into metadata_table_property(table_id, key, value) values (?,?,?)";
                PreparedStatement pStat = conn.prepareStatement(propInsertSql);
                for (Map.Entry<String, String> entry : props.entrySet()) {
                    pStat.setInt(1, id);
                    pStat.setString(2, entry.getKey());
                    pStat.setString(3, entry.getValue());
                    pStat.addBatch();
                }
                pStat.executeBatch();
                pStat.close();
            } else {
                // view, let's first assume that it only has physical fields
                // view also needs to save: query, expanded query
                // Insert attributes and columns for the view
                ResolvedCatalogView view = (ResolvedCatalogView) table;
                List<Schema.UnresolvedColumn> cols = view.getUnresolvedSchema().getColumns();
                if (!cols.isEmpty()) {
                    String colInsertSql = "insert into metadata_column("
                            + " column_name, column_type, data_type"
                            + " , expr"
                            + " , description"
                            + " , table_id"
                            + " , primary) "
                            + " values(?,?,?,?,?,?,?)";
                    PreparedStatement colIStat = conn.prepareStatement(colInsertSql);
                    for (Schema.UnresolvedColumn col : cols) {
                        if (col instanceof Schema.UnresolvedPhysicalColumn) {
                            Schema.UnresolvedPhysicalColumn pCol = (Schema.UnresolvedPhysicalColumn) col;
                            if (!(pCol.getDataType() instanceof DataType)) {
                                throw new UnsupportedOperationException(String.format(
                                        "Type recognition failed, the column is not a valid type：%s.%s.%s : %s",
                                        tablePath.getDatabaseName(),
                                        tablePath.getObjectName(),
                                        pCol.getName(),
                                        pCol.getDataType()));
                            }
                            DataType dataType = (DataType) pCol.getDataType();

                            colIStat.setString(1, pCol.getName());
                            colIStat.setString(2, ColumnType.PHYSICAL);
                            colIStat.setString(3, dataType.getLogicalType().asSerializableString());
                            colIStat.setObject(4, null);
                            colIStat.setString(5, pCol.getComment().orElse(""));
                            colIStat.setInt(6, id);
                            colIStat.setObject(7, null); // view没有主键
                            colIStat.addBatch();
                        } else {
                            throw new UnsupportedOperationException(
                                    "For the time being, it is believed that non-physical fields will not appear in the view.");
                        }
                    }
                    colIStat.executeBatch();
                    colIStat.close();

                    // Write query and other information to the database
                    Map<String, String> option = view.getOptions();
                    if (option == null) {
                        option = new HashMap<>();
                    }
                    option.put("OriginalQuery", view.getOriginalQuery());
                    option.put("ExpandedQuery", view.getExpandedQuery());
                    String propInsertSql =
                            "insert into metadata_table_property(table_id," + "key, value) values (?,?,?)";
                    PreparedStatement pStat = conn.prepareStatement(propInsertSql);
                    for (Map.Entry<String, String> entry : option.entrySet()) {
                        pStat.setInt(1, id);
                        pStat.setString(2, entry.getKey());
                        pStat.setString(3, entry.getValue());
                        pStat.addBatch();
                    }
                    pStat.executeBatch();
                    pStat.close();
                }
            }
            conn.commit();
        } catch (SQLException ex) {
            sqlExceptionHappened = true;
            logger.error("Insertion into database failed", ex);
            throw new CatalogException("Insertion into database failed", ex);
        }
    }

    /**
     * Modify the table information of the specified path.
     *
     * @param tablePath         table path object, including database name and table name
     * @param newTable          New table object, including the structure and attribute information of the table
     * @param ignoreIfNotExists If the table does not exist, whether to ignore modification operations
     * @throws TableNotExistException This exception is thrown if the table does not exist and ignoreIfNotExists is false
     * @throws CatalogException       If an error occurs while modifying table information, this exception is thrown
     */
    @Override
    public void alterTable(ObjectPath tablePath, CatalogBaseTable newTable, boolean ignoreIfNotExists)
            throws TableNotExistException, CatalogException {
        Integer id = getTableId(tablePath);

        if (id == null) {
            throw new TableNotExistException(getName(), tablePath);
        }

        Map<String, String> opts = newTable.getOptions();
        if (opts != null && !opts.isEmpty()) {

            String updateSql = "INSERT INTO metadata_table_property(table_id,"
                    + "key, value) values (?,?,?) "
                    + "on CONFLICT (table_id, \"key\") do update set value = excluded.value, update_time = now()";
            Connection conn = getConnection();
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                for (Map.Entry<String, String> entry : opts.entrySet()) {
                    ps.setInt(1, id);
                    ps.setString(2, entry.getKey());
                    ps.setString(3, entry.getValue());
                    ps.setString(4, entry.getValue());
                    ps.addBatch();
                }
                ps.executeBatch();
            } catch (SQLException ex) {
                sqlExceptionHappened = true;
                throw new CatalogException("Failed to modify table name", ex);
            }
        }
    }

    /** ********************** partition ************************ */
    /**
     * List all partition specifications for the specified table.
     *
     * @param tablePath table path object, including database name and table name
     * @return a list containing all partition specifications
     * @throws CatalogException              This exception is thrown if an error occurs while listing partitions
     * @throws UnsupportedOperationException If this method has not been implemented, this exception is thrown
     */
    @Override
    public List<CatalogPartitionSpec> listPartitions(ObjectPath tablePath) throws CatalogException {
        // todo: Supplementary completion of this method。
        throw new UnsupportedOperationException("This method is not yet complete");
    }

    /**
     * List all partition specifications for the specified table based on the partition specification.
     *
     * @param tablePath     table path object, including database name and table name
     * @param partitionSpec partition specification object, used to filter specific partitions
     * @return A list containing all partition specifications that match partitionSpec
     * @throws CatalogException              This exception is thrown if an error occurs while listing partition specifications
     * @throws UnsupportedOperationException If this method has not been implemented, this exception is thrown
     */
    @Override
    public List<CatalogPartitionSpec> listPartitions(ObjectPath tablePath, CatalogPartitionSpec partitionSpec)
            throws CatalogException {
        // todo: Supplementary completion of this method。
        throw new UnsupportedOperationException("This method is not yet complete");
    }

    /**
     * List the partition specification list of the specified table according to the filter conditions.
     *
     * @param tablePath table path object, including database name and table name
     * @param filters   A list of expressions used to filter partitions
     * @return a list containing partition specifications
     * @throws TableNotExistException        If the table does not exist, this exception is thrown
     * @throws TableNotPartitionedException  If the table is not a partitioned table, this exception is thrown
     * @throws CatalogException              This exception is thrown if an error occurs while listing partition specifications
     * @throws UnsupportedOperationException If this method has not been implemented, this exception is thrown
     */
    @Override
    public List<CatalogPartitionSpec> listPartitionsByFilter(ObjectPath tablePath, List<Expression> filters)
            throws TableNotExistException, TableNotPartitionedException, CatalogException {
        // todo: Supplementary completion of this method。
        throw new UnsupportedOperationException("This method is not yet complete");
    }

    /**
     * Obtain the partition information of the specified table according to the partition specification.
     *
     * @param tablePath     table path object, including database name and table name
     * @param partitionSpec partition specification object, including partition information
     * @return CatalogPartition object, containing partition details
     * @throws CatalogException              This exception is thrown if an error occurs while obtaining partition information
     * @throws UnsupportedOperationException If this method has not been implemented, this exception is thrown
     */
    @Override
    public CatalogPartition getPartition(ObjectPath tablePath, CatalogPartitionSpec partitionSpec)
            throws CatalogException {
        // todo: Supplementary completion of this method。
        throw new UnsupportedOperationException("This method is not yet complete");
    }

    /**
     * Determine whether the specified partition exists in the specified table.
     *
     * @param tablePath     table path object, including database name and table name
     * @param partitionSpec partition specification object, including partition information
     * @return If the partition exists, return true; otherwise return false
     * @throws CatalogException              If an error occurs when determining whether the partition exists, this exception is thrown
     * @throws UnsupportedOperationException If this method has not been implemented, this exception is thrown
     */
    @Override
    public boolean partitionExists(ObjectPath tablePath, CatalogPartitionSpec partitionSpec) throws CatalogException {
        // todo: Supplementary completion of this method。
        throw new UnsupportedOperationException("This method is not yet complete");
    }

    /**
     * Create a new partition in the specified table.
     *
     * @param tablePath      table path object, including database name and table name
     * @param partitionSpec  partition specification object, defining the keys and values of the partition
     * @param partition      New partition object, containing specific information about the partition
     * @param ignoreIfExists If the partition already exists, whether to ignore the creation operation
     * @throws TableNotExistException          If the table does not exist, this exception is thrown
     * @throws TableNotPartitionedException    If the table is not a partitioned table, this exception is thrown
     * @throws PartitionSpecInvalidException   This exception is thrown if the partition specification is invalid
     * @throws PartitionAlreadyExistsException If the partition already exists and ignoreIfExists is false, this exception is thrown
     * @throws CatalogException                This exception is thrown if an error occurs while creating the partition
     * @throws UnsupportedOperationException   If this method has not been implemented, this exception is thrown
     */
    @Override
    public void createPartition(
            ObjectPath tablePath,
            CatalogPartitionSpec partitionSpec,
            CatalogPartition partition,
            boolean ignoreIfExists)
            throws TableNotExistException, TableNotPartitionedException, PartitionSpecInvalidException,
                    PartitionAlreadyExistsException, CatalogException {
        // todo: Supplementary completion of this method。
        throw new UnsupportedOperationException("This method is not yet complete");
    }

    /**
     * Delete the partition of the specified partition table.
     *
     * @param tablePath         table path object, including database name and table name
     * @param partitionSpec     partition specification object, including partition information
     * @param ignoreIfNotExists If the partition does not exist, whether to ignore the delete operation
     * @throws PartitionNotExistException    If the specified partition does not exist and ignoreIfNotExists is false, this exception is thrown
     * @throws CatalogException              This exception is thrown if an error occurs while deleting a partition
     * @throws UnsupportedOperationException If this method has not been implemented, this exception is thrown
     */
    @Override
    public void dropPartition(ObjectPath tablePath, CatalogPartitionSpec partitionSpec, boolean ignoreIfNotExists)
            throws PartitionNotExistException, CatalogException {
        // todo: Supplementary completion of this method。
        throw new UnsupportedOperationException("This method is not yet complete");
    }

    /**
     * Modify the partition information of the specified partition table.
     *
     * @param tablePath         table path object, including database name and table name
     * @param partitionSpec     partition specification object, including partition information
     * @param newPartition      new partition information
     * @param ignoreIfNotExists If the partition does not exist, whether to ignore the modification operation
     * @throws PartitionNotExistException    If the specified partition does not exist and ignoreIfNotExists is false, this exception is thrown
     * @throws CatalogException              This exception is thrown if an error occurs while modifying partition information
     * @throws UnsupportedOperationException If this method has not been implemented, this exception is thrown
     */
    @Override
    public void alterPartition(
            ObjectPath tablePath,
            CatalogPartitionSpec partitionSpec,
            CatalogPartition newPartition,
            boolean ignoreIfNotExists)
            throws PartitionNotExistException, CatalogException {
        // todo: Supplementary completion of this method。
        throw new UnsupportedOperationException("This method is not yet complete");
    }

    /** *********************Functions********************* */

    /**
     * Get a list of all user-defined function (UDF) names in the specified database.
     *
     * @param dbName database name
     * @return a list containing all UDF names
     * @throws DatabaseNotExistException If the database does not exist, this exception is thrown
     * @throws CatalogException          This exception is thrown if an error occurs while getting the UDF list
     */
    @Override
    public List<String> listFunctions(String dbName) throws DatabaseNotExistException, CatalogException {
        Integer dbId = getDatabaseId(dbName);
        if (null == dbId) {
            throw new DatabaseNotExistException(getName(), dbName);
        }
        String querySql = "SELECT function_name from metadata_function  WHERE database_id=?";

        Connection conn = getConnection();
        try (PreparedStatement gStat = conn.prepareStatement(querySql)) {
            gStat.setInt(1, dbId);
            ResultSet rs = gStat.executeQuery();
            List<String> functions = new ArrayList<>();
            while (rs.next()) {
                String n = rs.getString("function_name");
                functions.add(n);
            }
            return functions;
        } catch (SQLException e) {
            sqlExceptionHappened = true;
            throw new CatalogException("Failed to get UDF list");
        }
    }

    /**
     * Get the CatalogFunction object based on the function path.
     *
     * @param functionPath function path object, including database name and function name
     * @return CatalogFunction object, containing the class name and programming language of the function
     * @throws FunctionNotExistException If the function does not exist, this exception is thrown
     * @throws CatalogException          This exception is thrown if an error occurs while obtaining function information
     */
    @Override
    public CatalogFunction getFunction(ObjectPath functionPath) throws FunctionNotExistException, CatalogException {
        Integer id = getFunctionId(functionPath);
        if (null == id) {
            throw new FunctionNotExistException(getName(), functionPath);
        }

        String querySql = "SELECT class_name,function_language from metadata_function   WHERE id=?";
        Connection conn = getConnection();
        try (PreparedStatement gStat = conn.prepareStatement(querySql)) {
            gStat.setInt(1, id);
            ResultSet rs = gStat.executeQuery();
            if (rs.next()) {
                String className = rs.getString("class_name");
                String language = rs.getString("function_language");
                return new CatalogFunctionImpl(className, FunctionLanguage.valueOf(language));
            } else {
                throw new FunctionNotExistException(getName(), functionPath);
            }
        } catch (SQLException e) {
            sqlExceptionHappened = true;
            throw new CatalogException(
                    "Failed to get UDF：" + functionPath.getDatabaseName() + "." + functionPath.getObjectName());
        }
    }

    /**
     * Determine whether the function of the specified path exists.
     *
     * @param functionPath function path object, including database name and function name
     * @return If the function exists, return true; otherwise return false
     * @throws CatalogException If an error occurs when determining whether the function exists, this exception is thrown
     */
    @Override
    public boolean functionExists(ObjectPath functionPath) throws CatalogException {
        Integer id = getFunctionId(functionPath);
        return id != null;
    }

    /**
     * Get the ID of the function based on the function path.
     *
     * @param functionPath function path object, including database name and function name
     * @return If the function exists, return its ID; otherwise return null
     * @throws CatalogException This exception is thrown if an error occurs while getting the function ID
     */
    private Integer getFunctionId(ObjectPath functionPath) {
        Integer dbId = getDatabaseId(functionPath.getDatabaseName());
        if (dbId == null) {
            return null;
        }
        // Get id
        String getIdSql = "select id from metadata_function  where function_name=? and database_id=?";
        Connection conn = getConnection();
        try (PreparedStatement gStat = conn.prepareStatement(getIdSql)) {
            gStat.setString(1, functionPath.getObjectName());
            gStat.setInt(2, dbId);
            ResultSet rs = gStat.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                return id;
            }
        } catch (SQLException e) {
            sqlExceptionHappened = true;
            logger.error("get function fail", e);
            throw new CatalogException("get function fail.", e);
        }
        return null;
    }

    /**
     * Create a new user-defined function (UDF).
     *
     * @param functionPath   function path object, including database name and function name
     * @param function       CatalogFunction object to be created, including the class name and programming language of the function
     * @param ignoreIfExists If the function already exists, whether to ignore the creation operation
     * @throws FunctionAlreadyExistException This exception is thrown if the function already exists and ignoreIfExists is false
     * @throws DatabaseNotExistException     If the database does not exist, this exception is thrown
     * @throws CatalogException              This exception is thrown if an error occurs while creating the function
     */
    @Override
    public void createFunction(ObjectPath functionPath, CatalogFunction function, boolean ignoreIfExists)
            throws FunctionAlreadyExistException, DatabaseNotExistException, CatalogException {
        Integer dbId = getDatabaseId(functionPath.getDatabaseName());
        if (null == dbId) {
            throw new DatabaseNotExistException(getName(), functionPath.getDatabaseName());
        }
        if (functionExists(functionPath)) {
            if (!ignoreIfExists) {
                throw new FunctionAlreadyExistException(getName(), functionPath);
            }
        }

        Connection conn = getConnection();
        String insertSql = "insert into metadata_function "
                + "(function_name,class_name,database_id,function_language) "
                + " values (?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setString(1, functionPath.getObjectName());
            ps.setString(2, function.getClassName());
            ps.setInt(3, dbId);
            ps.setString(4, function.getFunctionLanguage().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            sqlExceptionHappened = true;
            throw new CatalogException("Create function failed", e);
        }
    }

    /**
     * Modify the function information of the specified path.
     *
     * @param functionPath      function path object, including database name and function name
     * @param newFunction       New CatalogFunction object, including new class name and programming language
     * @param ignoreIfNotExists If the function does not exist, whether to ignore the modification operation
     * @throws FunctionNotExistException This exception is thrown if the function does not exist and ignoreIfNotExists is false
     * @throws CatalogException          If an error occurs when modifying function information, this exception is thrown
     */
    @Override
    public void alterFunction(ObjectPath functionPath, CatalogFunction newFunction, boolean ignoreIfNotExists)
            throws FunctionNotExistException, CatalogException {
        Integer id = getFunctionId(functionPath);
        if (null == id) {
            if (!ignoreIfNotExists) {
                throw new FunctionNotExistException(getName(), functionPath);
            }
            return;
        }

        Connection conn = getConnection();
        String insertSql = "update metadata_function  set class_name =?, function_language=? where id=?";
        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setString(1, newFunction.getClassName());
            ps.setString(2, newFunction.getFunctionLanguage().toString());
            ps.setInt(3, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            sqlExceptionHappened = true;
            throw new CatalogException("Modify function failed", e);
        }
    }

    /**
     * Delete the user-defined function (UDF) of the specified path.
     *
     * @param functionPath      function path object, including database name and function name
     * @param ignoreIfNotExists If the function does not exist, whether to ignore the delete operation
     * @throws FunctionNotExistException This exception is thrown if the function does not exist and ignoreIfNotExists is false
     * @throws CatalogException          This exception is thrown if an error occurs while deleting the function
     */
    @Override
    public void dropFunction(ObjectPath functionPath, boolean ignoreIfNotExists)
            throws FunctionNotExistException, CatalogException {
        Integer id = getFunctionId(functionPath);
        if (null == id) {
            if (!ignoreIfNotExists) {
                throw new FunctionNotExistException(getName(), functionPath);
            }
            return;
        }

        Connection conn = getConnection();
        String insertSql = "delete from metadata_function where id=?";
        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            sqlExceptionHappened = true;
            throw new CatalogException("Delete function failed", e);
        }
    }

    /**
     * Get statistical information of the table with the specified path.
     *
     * @param tablePath table path object, including database name and table name
     * @return CatalogTableStatistics object, containing table statistics
     * @throws TableNotExistException If the table does not exist, this exception is thrown
     * @throws CatalogException       This exception is thrown if an error occurs while getting table statistics
     */
    @Override
    public CatalogTableStatistics getTableStatistics(ObjectPath tablePath)
            throws TableNotExistException, CatalogException {
        // todo: Supplementary completion of this method。
        checkNotNull(tablePath);

        if (!tableExists(tablePath)) {
            throw new TableNotExistException(getName(), tablePath);
        }
        /*
         * if (!isPartitionedTable(tablePath)) { CatalogTableStatistics result = tableStats.get(tablePath); return
         * result != null ? result.copy() : CatalogTableStatistics.UNKNOWN; } else { return
         * CatalogTableStatistics.UNKNOWN; }
         */
        return CatalogTableStatistics.UNKNOWN;
    }

    /**
     * Get the column statistics of the table with the specified path.
     *
     * @param tablePath table path object, including database name and table name
     * @return CatalogColumnStatistics object, containing column statistics of the table
     * @throws TableNotExistException If the table does not exist, this exception is thrown
     * @throws CatalogException       This exception is thrown if an error occurs while obtaining table column statistics
     */
    @Override
    public CatalogColumnStatistics getTableColumnStatistics(ObjectPath tablePath)
            throws TableNotExistException, CatalogException {
        // todo: Supplementary completion of this method。
        checkNotNull(tablePath);

        if (!tableExists(tablePath)) {
            throw new TableNotExistException(getName(), tablePath);
        }

        // CatalogColumnStatistics result = tableColumnStats.get(tablePath);
        // return result != null ? result.copy() : CatalogColumnStatistics.UNKNOWN;
        return CatalogColumnStatistics.UNKNOWN;
    }

    /**
     * Get partition statistics of the specified partition table.
     *
     * @param tablePath     table path object, including database name and table name
     * @param partitionSpec partition specification object, including partition information
     * @return CatalogTableStatistics object, containing partition statistics
     * @throws PartitionNotExistException    If the specified partition does not exist, this exception is thrown
     * @throws CatalogException              This exception is thrown if an error occurs while getting partition statistics
     * @throws UnsupportedOperationException If this method has not been implemented, this exception is thrown
     */
    @Override
    public CatalogTableStatistics getPartitionStatistics(ObjectPath tablePath, CatalogPartitionSpec partitionSpec)
            throws PartitionNotExistException, CatalogException {
        // todo: Supplementary completion of this method。
        throw new UnsupportedOperationException("This method is not yet complete");
    }

    /**
     * Get the partition column statistics of the specified partition table.
     *
     * @param tablePath     table path object, including database name and table name
     * @param partitionSpec partition specification object, including partition information
     * @return CatalogColumnStatistics object, containing partition column statistics
     * @throws PartitionNotExistException    If the specified partition does not exist, this exception is thrown
     * @throws CatalogException              This exception is thrown if an error occurs while getting partition column statistics
     * @throws UnsupportedOperationException If this method has not been implemented, this exception is thrown
     */
    @Override
    public CatalogColumnStatistics getPartitionColumnStatistics(
            ObjectPath tablePath, CatalogPartitionSpec partitionSpec)
            throws PartitionNotExistException, CatalogException {
        // todo: Supplementary completion of this method。
        throw new UnsupportedOperationException("This method is not yet complete");
    }

    /**
     * Modify the statistical information of the table with the specified path.
     *
     * @param tablePath         table path object, including database name and table name
     * @param tableStatistics   new table statistics
     * @param ignoreIfNotExists If the table does not exist, whether to ignore modification operations
     * @throws TableNotExistException        This exception is thrown if the table does not exist and ignoreIfNotExists is false
     * @throws CatalogException              This exception is thrown if an error occurs while modifying table statistics
     * @throws UnsupportedOperationException If this method has not been implemented, this exception is thrown
     */
    @Override
    public void alterTableStatistics(
            ObjectPath tablePath, CatalogTableStatistics tableStatistics, boolean ignoreIfNotExists)
            throws TableNotExistException, CatalogException {
        // todo: Supplementary completion of this method。
        throw new UnsupportedOperationException("This method is not yet complete");
    }

    /**
     * Modify the column statistics of the table with the specified path.
     *
     * @param tablePath         table path object, including database name and table name
     * @param columnStatistics  new column statistics
     * @param ignoreIfNotExists If the table does not exist, whether to ignore modification operations
     * @throws TableNotExistException        This exception is thrown if the table does not exist and ignoreIfNotExists is false
     * @throws CatalogException              This exception is thrown if an error occurs while modifying column statistics
     * @throws TablePartitionedException     If the table is a partitioned table, this exception is thrown
     * @throws UnsupportedOperationException If this method has not been implemented, this exception is thrown
     */
    @Override
    public void alterTableColumnStatistics(
            ObjectPath tablePath, CatalogColumnStatistics columnStatistics, boolean ignoreIfNotExists)
            throws TableNotExistException, CatalogException, TablePartitionedException {
        // todo: Supplementary completion of this method。
        throw new UnsupportedOperationException("This method is not yet complete");
    }

    /**
     * Modify the partition statistics of the specified partition table.
     *
     * @param tablePath           table path object, including database name and table name
     * @param partitionSpec       partition specification object, including partition information
     * @param partitionStatistics new partition statistics
     * @param ignoreIfNotExists   If the partition does not exist, whether to ignore the modification operation
     * @throws PartitionNotExistException    If the specified partition does not exist and ignoreIfNotExists is false, this exception is thrown
     * @throws CatalogException              This exception is thrown if an error occurs while modifying partition statistics
     * @throws UnsupportedOperationException If this method has not been implemented, this exception is thrown
     */
    @Override
    public void alterPartitionStatistics(
            ObjectPath tablePath,
            CatalogPartitionSpec partitionSpec,
            CatalogTableStatistics partitionStatistics,
            boolean ignoreIfNotExists)
            throws PartitionNotExistException, CatalogException {
        // todo: Supplementary completion of this method。
        throw new UnsupportedOperationException("This method is not yet complete");
    }

    /**
     * Modify the partition column statistics of the specified partition table.
     *
     * @param tablePath         table path object, including database name and table name
     * @param partitionSpec     partition specification object, including partition information
     * @param columnStatistics  new partition column statistics
     * @param ignoreIfNotExists If the partition does not exist, whether to ignore the modification operation
     * @throws PartitionNotExistException    If the specified partition does not exist and ignoreIfNotExists is false, this exception is thrown
     * @throws CatalogException              This exception is thrown if an error occurs while modifying partition column statistics
     * @throws UnsupportedOperationException If this method has not been implemented, this exception is thrown
     */
    @Override
    public void alterPartitionColumnStatistics(
            ObjectPath tablePath,
            CatalogPartitionSpec partitionSpec,
            CatalogColumnStatistics columnStatistics,
            boolean ignoreIfNotExists)
            throws PartitionNotExistException, CatalogException {
        // todo: Supplementary completion of this method。
        throw new UnsupportedOperationException("This method is not yet complete");
    }
}

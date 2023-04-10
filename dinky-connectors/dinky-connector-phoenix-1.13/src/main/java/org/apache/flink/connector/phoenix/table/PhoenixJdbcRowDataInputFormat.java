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

package org.apache.flink.connector.phoenix.table;

import org.apache.flink.api.common.io.DefaultInputSplitAssigner;
import org.apache.flink.api.common.io.RichInputFormat;
import org.apache.flink.api.common.io.statistics.BaseStatistics;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.ResultTypeQueryable;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.phoenix.JdbcConnectionOptions;
import org.apache.flink.connector.phoenix.internal.connection.JdbcConnectionProvider;
import org.apache.flink.connector.phoenix.internal.connection.PhoneixJdbcConnectionProvider;
import org.apache.flink.connector.phoenix.internal.converter.JdbcRowConverter;
import org.apache.flink.connector.phoenix.split.JdbcParameterValuesProvider;
import org.apache.flink.core.io.GenericInputSplit;
import org.apache.flink.core.io.InputSplit;
import org.apache.flink.core.io.InputSplitAssigner;
import org.apache.flink.table.data.RowData;
import org.apache.flink.util.Preconditions;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PhoenixJdbcRowDataInputFormat
 *
 * @since 2022/3/17 10:53
 **/
public class PhoenixJdbcRowDataInputFormat extends RichInputFormat<RowData, InputSplit> implements ResultTypeQueryable<RowData> {
    private static final long serialVersionUID = 2L;
    private static final Logger LOG = LoggerFactory.getLogger(PhoenixJdbcRowDataInputFormat.class);
    private JdbcConnectionProvider connectionProvider;
    private int fetchSize;
    private Boolean autoCommit;
    private Object[][] parameterValues;
    private String queryTemplate;
    private int resultSetType;
    private int resultSetConcurrency;
    private JdbcRowConverter rowConverter;
    private TypeInformation<RowData> rowDataTypeInfo;
    private transient PreparedStatement statement;
    private transient ResultSet resultSet;
    private transient boolean hasNext;
    private boolean namespaceMappingEnabled;
    private boolean mapSystemTablesEnabled;

    private PhoenixJdbcRowDataInputFormat(
            JdbcConnectionProvider connectionProvider,
            int fetchSize, Boolean autoCommit, Object[][] parameterValues,
            String queryTemplate, int resultSetType, int resultSetConcurrency,
            JdbcRowConverter rowConverter, TypeInformation<RowData> rowDataTypeInfo,
            boolean namespaceMappingEnabled,boolean mapSystemTablesEnabled) {
        this.connectionProvider = connectionProvider;
        this.fetchSize = fetchSize;
        this.autoCommit = autoCommit;
        this.parameterValues = parameterValues;
        this.queryTemplate = queryTemplate;
        this.resultSetType = resultSetType;
        this.resultSetConcurrency = resultSetConcurrency;
        this.rowConverter = rowConverter;
        this.rowDataTypeInfo = rowDataTypeInfo;
        this.namespaceMappingEnabled = namespaceMappingEnabled;
        this.mapSystemTablesEnabled = mapSystemTablesEnabled;
    }

    public void configure(Configuration parameters) {
    }

    public void openInputFormat() {
        try {
            Connection dbConn = this.connectionProvider.getOrEstablishConnection();
            if (this.autoCommit != null) {
                dbConn.setAutoCommit(this.autoCommit);
            }

            this.statement = dbConn.prepareStatement(this.queryTemplate, this.resultSetType, this.resultSetConcurrency);
            if (this.fetchSize == -2147483648 || this.fetchSize > 0) {
                this.statement.setFetchSize(this.fetchSize);
            }

        } catch (SQLException var2) {
            throw new IllegalArgumentException("open() failed." + var2.getMessage(), var2);
        } catch (ClassNotFoundException var3) {
            throw new IllegalArgumentException("JDBC-Class not found. - " + var3.getMessage(), var3);
        }
    }

    public void closeInputFormat() {
        try {
            if (this.statement != null) {
                this.statement.close();
            }
        } catch (SQLException var5) {
            LOG.info("Inputformat Statement couldn't be closed - " + var5.getMessage());
        } finally {
            this.statement = null;
        }

        this.connectionProvider.closeConnection();
        this.parameterValues = (Object[][])null;
    }

    public void open(InputSplit inputSplit) throws IOException {
        try {
            if (inputSplit != null && this.parameterValues != null) {
                for (int i = 0; i < this.parameterValues[inputSplit.getSplitNumber()].length; ++i) {
                    Object param = this.parameterValues[inputSplit.getSplitNumber()][i];
                    if (param instanceof String) {
                        this.statement.setString(i + 1, (String)param);
                    } else if (param instanceof Long) {
                        this.statement.setLong(i + 1, (Long)param);
                    } else if (param instanceof Integer) {
                        this.statement.setInt(i + 1, (Integer)param);
                    } else if (param instanceof Double) {
                        this.statement.setDouble(i + 1, (Double)param);
                    } else if (param instanceof Boolean) {
                        this.statement.setBoolean(i + 1, (Boolean)param);
                    } else if (param instanceof Float) {
                        this.statement.setFloat(i + 1, (Float)param);
                    } else if (param instanceof BigDecimal) {
                        this.statement.setBigDecimal(i + 1, (BigDecimal)param);
                    } else if (param instanceof Byte) {
                        this.statement.setByte(i + 1, (Byte)param);
                    } else if (param instanceof Short) {
                        this.statement.setShort(i + 1, (Short)param);
                    } else if (param instanceof Date) {
                        this.statement.setDate(i + 1, (Date)param);
                    } else if (param instanceof Time) {
                        this.statement.setTime(i + 1, (Time)param);
                    } else if (param instanceof Timestamp) {
                        this.statement.setTimestamp(i + 1, (Timestamp)param);
                    } else {
                        if (!(param instanceof Array)) {
                            throw new IllegalArgumentException("open() failed. Parameter " + i + " of type " + param.getClass() + " is not handled (yet).");
                        }

                        this.statement.setArray(i + 1, (Array)param);
                    }
                }

                if (LOG.isDebugEnabled()) {
                    LOG.debug(String.format("Executing '%s' with parameters %s", this.queryTemplate, Arrays.deepToString(this.parameterValues[inputSplit.getSplitNumber()])));
                }
            }

            this.resultSet = this.statement.executeQuery();
            this.hasNext = this.resultSet.next();
        } catch (SQLException var4) {
            throw new IllegalArgumentException("open() failed." + var4.getMessage(), var4);
        }
    }

    public void close() throws IOException {
        if (this.resultSet != null) {
            try {
                this.resultSet.close();
            } catch (SQLException var2) {
                LOG.info("Inputformat ResultSet couldn't be closed - " + var2.getMessage());
            }

        }
    }

    public TypeInformation<RowData> getProducedType() {
        return this.rowDataTypeInfo;
    }

    public boolean reachedEnd() throws IOException {
        return !this.hasNext;
    }

    public RowData nextRecord(RowData reuse) throws IOException {
        try {
            if (!this.hasNext) {
                return null;
            } else {
                RowData row = this.rowConverter.toInternal(this.resultSet);
                this.hasNext = this.resultSet.next();
                return row;
            }
        } catch (SQLException var3) {
            throw new IOException("Couldn't read data - " + var3.getMessage(), var3);
        } catch (NullPointerException var4) {
            throw new IOException("Couldn't access resultSet", var4);
        }
    }

    public BaseStatistics getStatistics(BaseStatistics cachedStatistics) throws IOException {
        return cachedStatistics;
    }

    public InputSplit[] createInputSplits(int minNumSplits) throws IOException {
        if (this.parameterValues == null) {
            return new GenericInputSplit[]{new GenericInputSplit(0, 1)};
        } else {
            GenericInputSplit[] ret = new GenericInputSplit[this.parameterValues.length];

            for (int i = 0; i < ret.length; ++i) {
                ret[i] = new GenericInputSplit(i, ret.length);
            }

            return ret;
        }
    }

    public InputSplitAssigner getInputSplitAssigner(InputSplit[] inputSplits) {
        return new DefaultInputSplitAssigner(inputSplits);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private JdbcConnectionOptions.JdbcConnectionOptionsBuilder connOptionsBuilder = new JdbcConnectionOptions.JdbcConnectionOptionsBuilder();
        private int fetchSize;
        private Boolean autoCommit;
        private Object[][] parameterValues;
        private String queryTemplate;
        private JdbcRowConverter rowConverter;
        private TypeInformation<RowData> rowDataTypeInfo;
        private int resultSetType = 1003;
        private int resultSetConcurrency = 1007;
        private boolean namespaceMappingEnabled;
        private boolean mapSystemTablesEnabled;

        public Builder() {
        }

        public Builder setDrivername(String drivername) {
            this.connOptionsBuilder.withDriverName(drivername);
            return this;
        }

        public Builder setDBUrl(String dbURL) {
            this.connOptionsBuilder.withUrl(dbURL);
            return this;
        }

        public Builder setUsername(String username) {
            this.connOptionsBuilder.withUsername(username);
            return this;
        }

        public Builder setPassword(String password) {
            this.connOptionsBuilder.withPassword(password);
            return this;
        }

        public Builder setQuery(String query) {
            this.queryTemplate = query;
            return this;
        }

        public Builder setParametersProvider(JdbcParameterValuesProvider parameterValuesProvider) {
            this.parameterValues = parameterValuesProvider.getParameterValues();
            return this;
        }

        public Builder setRowDataTypeInfo(TypeInformation<RowData> rowDataTypeInfo) {
            this.rowDataTypeInfo = rowDataTypeInfo;
            return this;
        }

        public Builder setRowConverter(JdbcRowConverter rowConverter) {
            this.rowConverter = rowConverter;
            return this;
        }

        public Builder setFetchSize(int fetchSize) {
            Preconditions.checkArgument(fetchSize == -2147483648 || fetchSize > 0, "Illegal value %s for fetchSize, has to be positive or Integer.MIN_VALUE.", new Object[]{fetchSize});
            this.fetchSize = fetchSize;
            return this;
        }

        public Builder setAutoCommit(boolean autoCommit) {
            this.autoCommit = autoCommit;
            return this;
        }

        public Builder setResultSetType(int resultSetType) {
            this.resultSetType = resultSetType;
            return this;
        }

        public Builder setResultSetConcurrency(int resultSetConcurrency) {
            this.resultSetConcurrency = resultSetConcurrency;
            return this;
        }

        public Builder setNamespaceMappingEnabled(Boolean namespaceMappingEnabled) {
            this.namespaceMappingEnabled = namespaceMappingEnabled;
            return this;
        }

        public Builder setMapSystemTablesToNamespace(Boolean mapSystemTablesEnabled) {
            this.mapSystemTablesEnabled = mapSystemTablesEnabled;
            return this;
        }

        public PhoenixJdbcRowDataInputFormat build() {
            if (this.queryTemplate == null) {
                throw new NullPointerException("No query supplied");
            } else if (this.rowConverter == null) {
                throw new NullPointerException("No row converter supplied");
            } else {
                if (this.parameterValues == null) {
                    PhoenixJdbcRowDataInputFormat.LOG.debug("No input splitting configured (data will be read with parallelism 1).");
                }

                return new PhoenixJdbcRowDataInputFormat(new PhoneixJdbcConnectionProvider(this.connOptionsBuilder.build(),this.namespaceMappingEnabled,this.mapSystemTablesEnabled),
                        this.fetchSize, this.autoCommit, this.parameterValues, this.queryTemplate, this.resultSetType, this.resultSetConcurrency, this.rowConverter, this.rowDataTypeInfo,
                        this.namespaceMappingEnabled,this.mapSystemTablesEnabled);
            }
        }

    }
}

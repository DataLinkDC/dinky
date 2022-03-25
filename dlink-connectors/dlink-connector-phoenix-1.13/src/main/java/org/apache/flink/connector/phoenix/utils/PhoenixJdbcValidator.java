package org.apache.flink.connector.phoenix.utils;

import org.apache.flink.annotation.Internal;
import org.apache.flink.connector.phoenix.dialect.JdbcDialect;
import org.apache.flink.connector.phoenix.dialect.JdbcDialects;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.descriptors.ConnectorDescriptorValidator;
import org.apache.flink.table.descriptors.DescriptorProperties;
import org.apache.flink.table.utils.TableSchemaUtils;
import org.apache.flink.util.Preconditions;

import java.util.Optional;

import static org.apache.flink.table.descriptors.Schema.SCHEMA;

/** The validator for JDBC. */
@Internal
public class PhoenixJdbcValidator extends ConnectorDescriptorValidator {

    public static final String CONNECTOR_TYPE_VALUE_JDBC = "phoenix";
    public static final String PHOENIX_SCHEMA_NAMESPACE_MAPPING_ENABLE = "phoenix.schema.isnamespacemappingenabled";
    public static final String PHOENIX_SCHEMA_MAP_SYSTEMTABLE_ENABLE = "phoenix.schema.mapsystemtablestonamespace";


    public static final String CONNECTOR_URL = "connector.url";
    public static final String CONNECTOR_TABLE = "connector.table";
    public static final String CONNECTOR_DRIVER = "connector.driver";
    public static final String CONNECTOR_USERNAME = "connector.username";
    public static final String CONNECTOR_PASSWORD = "connector.password";
    public static final String CONNECTOR_CONNECTION_MAX_RETRY_TIMEOUT =
            "connector.connection.max-retry-timeout";

    public static final String CONNECTOR_READ_QUERY = "connector.read.query";
    public static final String CONNECTOR_READ_PARTITION_COLUMN = "connector.read.partition.column";
    public static final String CONNECTOR_READ_PARTITION_LOWER_BOUND =
            "connector.read.partition.lower-bound";
    public static final String CONNECTOR_READ_PARTITION_UPPER_BOUND =
            "connector.read.partition.upper-bound";
    public static final String CONNECTOR_READ_PARTITION_NUM = "connector.read.partition.num";
    public static final String CONNECTOR_READ_FETCH_SIZE = "connector.read.fetch-size";

    public static final String CONNECTOR_LOOKUP_CACHE_MAX_ROWS = "connector.lookup.cache.max-rows";
    public static final String CONNECTOR_LOOKUP_CACHE_TTL = "connector.lookup.cache.ttl";
    public static final String CONNECTOR_LOOKUP_MAX_RETRIES = "connector.lookup.max-retries";

    public static final String CONNECTOR_WRITE_FLUSH_MAX_ROWS = "connector.write.flush.max-rows";
    public static final String CONNECTOR_WRITE_FLUSH_INTERVAL = "connector.write.flush.interval";
    public static final String CONNECTOR_WRITE_MAX_RETRIES = "connector.write.max-retries";

    @Override
    public void validate(DescriptorProperties properties) {
        super.validate(properties);
        validateCommonProperties(properties);
        validateReadProperties(properties);
        validateLookupProperties(properties);
        validateSinkProperties(properties);
    }

    private void validateCommonProperties(DescriptorProperties properties) {
        properties.validateString(CONNECTOR_URL, false, 1);
        properties.validateString(CONNECTOR_TABLE, false, 1);
        properties.validateString(CONNECTOR_DRIVER, true);
        properties.validateString(CONNECTOR_USERNAME, true);
        properties.validateString(CONNECTOR_PASSWORD, true);
        properties.validateDuration(CONNECTOR_CONNECTION_MAX_RETRY_TIMEOUT, true, 1000);

        properties.validateString(PHOENIX_SCHEMA_NAMESPACE_MAPPING_ENABLE, true);
        properties.validateString(PHOENIX_SCHEMA_MAP_SYSTEMTABLE_ENABLE, true);

        final String url = properties.getString(CONNECTOR_URL);
        final Optional<JdbcDialect> dialect = JdbcDialects.get(url);
        Preconditions.checkState(dialect.isPresent(), "Cannot handle such jdbc url: " + url);

        TableSchema schema = TableSchemaUtils.getPhysicalSchema(properties.getTableSchema(SCHEMA));
        dialect.get().validate(schema);

        Optional<String> password = properties.getOptionalString(CONNECTOR_PASSWORD);
        if (password.isPresent()) {
            Preconditions.checkArgument(
                    properties.getOptionalString(CONNECTOR_USERNAME).isPresent(),
                    "Database username must be provided when database password is provided");
        }
    }

    private void validateReadProperties(DescriptorProperties properties) {
        properties.validateString(CONNECTOR_READ_QUERY, true);
        properties.validateString(CONNECTOR_READ_PARTITION_COLUMN, true);
        properties.validateLong(CONNECTOR_READ_PARTITION_LOWER_BOUND, true);
        properties.validateLong(CONNECTOR_READ_PARTITION_UPPER_BOUND, true);
        properties.validateInt(CONNECTOR_READ_PARTITION_NUM, true);
        properties.validateInt(CONNECTOR_READ_FETCH_SIZE, true);

        Optional<Long> lowerBound =
                properties.getOptionalLong(CONNECTOR_READ_PARTITION_LOWER_BOUND);
        Optional<Long> upperBound =
                properties.getOptionalLong(CONNECTOR_READ_PARTITION_UPPER_BOUND);
        if (lowerBound.isPresent() && upperBound.isPresent()) {
            Preconditions.checkArgument(
                    lowerBound.get() <= upperBound.get(),
                    CONNECTOR_READ_PARTITION_LOWER_BOUND
                            + " must not be larger than "
                            + CONNECTOR_READ_PARTITION_UPPER_BOUND);
        }

        checkAllOrNone(
                properties,
                new String[] {
                    CONNECTOR_READ_PARTITION_COLUMN,
                    CONNECTOR_READ_PARTITION_LOWER_BOUND,
                    CONNECTOR_READ_PARTITION_UPPER_BOUND,
                    CONNECTOR_READ_PARTITION_NUM
                });
    }

    private void validateLookupProperties(DescriptorProperties properties) {
        properties.validateLong(CONNECTOR_LOOKUP_CACHE_MAX_ROWS, true);
        properties.validateDuration(CONNECTOR_LOOKUP_CACHE_TTL, true, 1);
        properties.validateInt(CONNECTOR_LOOKUP_MAX_RETRIES, true, 0);

        checkAllOrNone(
                properties,
                new String[] {CONNECTOR_LOOKUP_CACHE_MAX_ROWS, CONNECTOR_LOOKUP_CACHE_TTL});
    }

    private void validateSinkProperties(DescriptorProperties properties) {
        properties.validateInt(CONNECTOR_WRITE_FLUSH_MAX_ROWS, true);
        properties.validateDuration(CONNECTOR_WRITE_FLUSH_INTERVAL, true, 1);
        properties.validateInt(CONNECTOR_WRITE_MAX_RETRIES, true);
    }

    private void checkAllOrNone(DescriptorProperties properties, String[] propertyNames) {
        int presentCount = 0;
        for (String name : propertyNames) {
            if (properties.getOptionalString(name).isPresent()) {
                presentCount++;
            }
        }
        Preconditions.checkArgument(
                presentCount == 0 || presentCount == propertyNames.length,
                "Either all or none of the following properties should be provided:\n"
                        + String.join("\n", propertyNames));
    }
}
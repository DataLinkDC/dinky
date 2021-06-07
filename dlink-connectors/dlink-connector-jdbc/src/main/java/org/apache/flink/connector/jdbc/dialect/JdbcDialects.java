package org.apache.flink.connector.jdbc.dialect;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * JdbcDialects
 *
 * @author wenmo
 * @since 2021/6/7 19:29
 */
public final class JdbcDialects {

    private static final List<JdbcDialect> DIALECTS =
            Arrays.asList(new DerbyDialect(), new MySQLDialect(), new PostgresDialect()
            , new OracleDialect(), new ClickHouseDialect());

    /** Fetch the JdbcDialect class corresponding to a given database url. */
    public static Optional<JdbcDialect> get(String url) {
        for (JdbcDialect dialect : DIALECTS) {
            if (dialect.canHandle(url)) {
                return Optional.of(dialect);
            }
        }
        return Optional.empty();
    }
}


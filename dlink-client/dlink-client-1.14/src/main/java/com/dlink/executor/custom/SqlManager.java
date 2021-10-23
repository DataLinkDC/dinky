package com.dlink.executor.custom;

import org.apache.flink.annotation.Internal;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.ExpressionParserException;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.internal.DlinkTableEnvironmentImpl;
import org.apache.flink.table.catalog.exceptions.CatalogException;
import org.apache.flink.types.Row;
import org.apache.flink.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static org.apache.flink.util.Preconditions.checkArgument;
import static org.apache.flink.util.Preconditions.checkNotNull;

/**
 * Flink Sql Fragment Manager
 * @author  wenmo
 * @since  2021/10/22 10:02
 **/
@Internal
public final class SqlManager {

    private Map<String, String> sqlFragments;
    static final String SHOW_FRAGMENTS = "SHOW FRAGMENTS";

    public SqlManager() {
        sqlFragments = new HashMap<>();
    }

    /**
     * Get names of sql fragments loaded.
     *
     * @return a list of names of sql fragments loaded
     */
    public List<String> listSqlFragments() {
        return new ArrayList<>(sqlFragments.keySet());
    }

    /**
     * Registers a fragment of sql under the given name. The sql fragment name must be unique.
     *
     * @param sqlFragmentName name under which to register the given sql fragment
     * @param sqlFragment     a fragment of sql to register
     * @throws CatalogException if the registration of the sql fragment under the given name failed.
     *                          But at the moment, with CatalogException, not SqlException
     */
    public void registerSqlFragment(String sqlFragmentName, String sqlFragment) {
        checkArgument(
                !StringUtils.isNullOrWhitespaceOnly(sqlFragmentName),
                "sql fragment name cannot be null or empty.");
        checkNotNull(sqlFragment, "sql fragment cannot be null");

        if (sqlFragments.containsKey(sqlFragmentName)) {
            throw new CatalogException(
                    format("The fragment of sql %s already exists.", sqlFragmentName));
        }

        sqlFragments.put(sqlFragmentName, sqlFragment);
    }

    /**
     * Unregisters a fragment of sql under the given name. The sql fragment name must be existed.
     *
     * @param sqlFragmentName   name under which to unregister the given sql fragment.
     * @param ignoreIfNotExists If false exception will be thrown if the fragment of sql to be
     *                          altered does not exist.
     * @throws CatalogException if the unregistration of the sql fragment under the given name
     *                          failed. But at the moment, with CatalogException, not SqlException
     */
    public void unregisterSqlFragment(String sqlFragmentName, boolean ignoreIfNotExists) {
        checkArgument(
                !StringUtils.isNullOrWhitespaceOnly(sqlFragmentName),
                "sql fragmentName name cannot be null or empty.");

        if (sqlFragments.containsKey(sqlFragmentName)) {
            sqlFragments.remove(sqlFragmentName);
        } else if (!ignoreIfNotExists) {
            throw new CatalogException(
                    format("The fragment of sql %s does not exist.", sqlFragmentName));
        }
    }

    /**
     * Get a fragment of sql under the given name. The sql fragment name must be existed.
     *
     * @param sqlFragmentName name under which to unregister the given sql fragment.
     * @throws CatalogException if the unregistration of the sql fragment under the given name
     *                          failed. But at the moment, with CatalogException, not SqlException
     */
    public String getSqlFragment(String sqlFragmentName) {
        checkArgument(
                !StringUtils.isNullOrWhitespaceOnly(sqlFragmentName),
                "sql fragmentName name cannot be null or empty.");

        if (sqlFragments.containsKey(sqlFragmentName)) {
            return sqlFragments.get(sqlFragmentName);
        } else {
            throw new CatalogException(
                    format("The fragment of sql %s does not exist.", sqlFragmentName));
        }
    }

    /**
     * Get a fragment of sql under the given name. The sql fragment name must be existed.
     *
     * @throws CatalogException if the unregistration of the sql fragment under the given name
     *                          failed. But at the moment, with CatalogException, not SqlException
     */
    public Map<String, String> getSqlFragment() {
        return sqlFragments;
    }

    public TableResult getSqlFragments() {
        List<Row> rows = new ArrayList<>();
        for (String key : sqlFragments.keySet()) {
            rows.add(Row.of(key));
        }
        return CustomTableResultImpl.buildTableResult(new ArrayList<>(Arrays.asList(new TableSchemaField("sql fragment name", DataTypes.STRING()))), rows);
    }

    public Iterator getSqlFragmentsIterator() {
        return sqlFragments.entrySet().iterator();
    }

    public Table getSqlFragmentsTable(DlinkTableEnvironmentImpl environment) {
        List<String> keys = new ArrayList<>();
        for (String key : sqlFragments.keySet()) {
            keys.add(key);
        }
        return environment.fromValues(keys);
    }

    public boolean checkShowFragments(String sql){
        return SHOW_FRAGMENTS.equals(sql.trim().toUpperCase());
    }
    /**
     * Parse some variables under the given sql.
     *
     * @param statement A sql will be parsed.
     * @throws ExpressionParserException if the name of the variable under the given sql failed.
     */
    public String parseVariable(String statement) {
        if (statement == null || "".equals(statement)) {
            return statement;
        }
        String[] strs = statement.split(";");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strs.length; i++) {
            String str = strs[i].trim();
            if (str.length() == 0) {
                continue;
            }
            if (str.contains(":=")) {
                String[] strs2 = str.split(":=");
                if (strs2.length >= 2) {
                    if (strs2[0].length() == 0) {
                        throw new ExpressionParserException("Illegal variable name.");
                    }
                    String valueString = str.substring(str.indexOf(":=") + 2);
                    this.registerSqlFragment(strs2[0], replaceVariable(valueString));
                } else {
                    throw new ExpressionParserException("Illegal variable definition.");
                }
            } else {
                sb.append(replaceVariable(str));
            }
        }
        return sb.toString();
    }

    /**
     * Replace some variables under the given sql.
     *
     * @param statement A sql will be replaced.
     */
    private String replaceVariable(String statement) {
        String pattern = "\\$\\{(.+?)\\}";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(statement);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String key = m.group(1);
            String value = this.getSqlFragment(key);
            m.appendReplacement(sb, value == null ? "" : value);
        }
        m.appendTail(sb);
        return sb.toString();
    }
}

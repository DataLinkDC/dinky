package com.dlink.result;

import java.util.Date;

/**
 * 解释结果
 *
 * @author  wenmo
 * @since  2021/5/25 11:41
 **/
public class SqlExplainResult {
    private Integer index;
    private String type;
    private String sql;
    private String parse;
    private String explain;
    private String error;
    private boolean parseTrue;
    private boolean explainTrue;
    private Date explainTime;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getParse() {
        return parse;
    }

    public void setParse(String parse) {
        this.parse = parse;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean isParseTrue() {
        return parseTrue;
    }

    public void setParseTrue(boolean parseTrue) {
        this.parseTrue = parseTrue;
    }

    public boolean isExplainTrue() {
        return explainTrue;
    }

    public void setExplainTrue(boolean explainTrue) {
        this.explainTrue = explainTrue;
    }

    public Date getExplainTime() {
        return explainTime;
    }

    public void setExplainTime(Date explainTime) {
        this.explainTime = explainTime;
    }

    @Override
    public String toString() {
        return "SqlExplainRecord{" +
                "index=" + index +
                ", type='" + type + '\'' +
                ", sql='" + sql + '\'' +
                ", parse='" + parse + '\'' +
                ", explain='" + explain + '\'' +
                ", error='" + error + '\'' +
                ", parseTrue=" + parseTrue +
                ", explainTrue=" + explainTrue +
                ", explainTime=" + explainTime +
                '}';
    }
}

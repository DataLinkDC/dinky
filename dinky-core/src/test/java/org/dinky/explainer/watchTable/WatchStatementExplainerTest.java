package org.dinky.explainer.watchTable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class WatchStatementExplainerTest {

    @Test
    void getTableNames() {
        String sql = "watch VersionT";
        WatchStatementExplainer watchStatementExplainer = new WatchStatementExplainer(sql);
        assertArrayEquals(new String[]{"VersionT"}, watchStatementExplainer.getTableNames());

        sql = "watch VersionT, Buyers, r, rr, vvv";
        WatchStatementExplainer wse = new WatchStatementExplainer(sql);

        assertArrayEquals(new String[]{"VersionT", "Buyers", "r", "rr", "vvv"},
                wse.getTableNames());

    }
}

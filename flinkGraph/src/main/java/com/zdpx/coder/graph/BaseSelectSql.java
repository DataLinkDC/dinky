package com.zdpx.coder.graph;


/**
 *
 */
public abstract class BaseSelectSql {

    public final void completeSql() {
        beforeSelection();
        completeSelection();
        fromInSelection();

    }

    protected abstract void fromInSelection();

    protected abstract String completeSelection();

    protected abstract String beforeSelection();
}

package com.dlink.metadata.query;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * AbstractDBQuery
 *
 * @author wenmo
 * @since 2021/7/20 13:50
 **/
public abstract class AbstractDBQuery implements IDBQuery {

    @Override
    public boolean isKeyIdentity(ResultSet results) throws SQLException {
        return false;
    }

    @Override
    public String[] columnCustom() {
        return null;
    }
}

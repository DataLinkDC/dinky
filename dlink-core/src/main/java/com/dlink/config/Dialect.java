package com.dlink.config;

import com.dlink.assertion.Asserts;

/**
 * Dialect
 *
 * @author wenmo
 * @since 2021/12/13
 **/
public enum  Dialect {

    FLINKSQL("FlinkSql"),SQL("Sql"),JAVA("Java");

    private String value;

    public static final Dialect DEFAULT = Dialect.FLINKSQL;

    Dialect(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean equalsVal(String valueText){
        return Asserts.isEqualsIgnoreCase(value,valueText);
    }

    public static Dialect get(String value){
        for (Dialect type : Dialect.values()) {
            if(Asserts.isEqualsIgnoreCase(type.getValue(),value)){
                return type;
            }
        }
        return Dialect.FLINKSQL;
    }
}


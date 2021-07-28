package com.dlink.explainer.trans;

/**
 * Field
 *
 * @author wenmo
 * @since 2021/6/22
 **/
public class Field {
    private String fragment;
    private String alias;

    public Field(String fragment) {
        this.fragment = fragment;
        this.alias = fragment;
    }

    public Field(String fragment, String alias) {
        this.fragment = fragment;
        this.alias = alias;
    }

    public String getFragment() {
        return fragment;
    }

    public void setFragment(String fragment) {
        this.fragment = fragment;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}

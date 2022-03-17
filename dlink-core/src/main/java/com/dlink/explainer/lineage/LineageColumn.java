package com.dlink.explainer.lineage;

/**
 * LineageColumn
 *
 * @author wenmo
 * @since 2022/3/15 22:55
 */
public class LineageColumn {
    private String name;
    private String title;

    public LineageColumn() {
    }

    public LineageColumn(String name, String title) {
        this.name = name;
        this.title = title;
    }

    public static LineageColumn build(String name, String title){
        return new LineageColumn(name,title);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

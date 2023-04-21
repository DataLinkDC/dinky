package com.zdpx.coder.json;

/**
 * 描述信息, 与算子业务逻辑无关, 图上的注释信息
 *
 * @author Licho Sun
 */
public class Description {
    /**
     * 对齐方式
     */
    private String align;
    /**
     * 背景颜色
     */
    private String color;
    /**
     * 调度
     */
    private int height;
    /**
     * 宽度
     */
    private int width;
    /**
     * 缩放比例
     */
    private boolean resized;
    /**
     * x坐标
     */
    private int x;
    /**
     * y坐标
     */
    private int y;
    /**
     * 说明内容
     */
    private String context;

    //region setter/getter
    public String getAlign() {
        return align;
    }

    public void setAlign(String align) {
        this.align = align;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isResized() {
        return resized;
    }

    public void setResized(boolean resized) {
        this.resized = resized;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    //endregion
}

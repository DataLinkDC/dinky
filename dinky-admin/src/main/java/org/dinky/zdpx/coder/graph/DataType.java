package org.dinky.zdpx.coder.graph;


import org.dinky.zdpx.coder.operator.TableInfo;

/**
 * 数据类型枚举类, 目前只使用{@link #TABLE} 类型, 表示表结构抽象类型, 对应于{@link TableInfo TableInfo}
 *
 * @author Licho Sun
 */
public enum DataType {
    /**
     * 表示表结构抽象类型, 对应于{@link TableInfo TableInfo}
     */
    TABLE,
    /**
     * 可以直接传脚本,比如groovy,目前未使用
     */
    CODE;
}

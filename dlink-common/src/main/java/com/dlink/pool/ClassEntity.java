package com.dlink.pool;

import com.dlink.assertion.Asserts;
import lombok.Getter;
import lombok.Setter;

/**
 * ClassEntity
 *
 * @author wenmo
 * @since 2022/1/12 23:52
 */
@Getter
@Setter
public class ClassEntity {
    private String name;
    private String code;
    private byte[] classByte;

    public ClassEntity(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public ClassEntity(String name, String code, byte[] classByte) {
        this.name = name;
        this.code = code;
        this.classByte = classByte;
    }

    public static ClassEntity build(String name, String code) {
        return new ClassEntity(name, code);
    }

    public boolean equals(ClassEntity entity) {
        if (Asserts.isEquals(name, entity.getName()) && Asserts.isEquals(code, entity.getCode())) {
            return true;
        } else {
            return false;
        }
    }
}

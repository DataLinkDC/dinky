package com.dlink.pool;

import java.util.List;
import java.util.Vector;

/**
 * ClassPool
 *
 * @author wenmo
 * @since 2022/1/12 23:52
 */
public class ClassPool {

    private static volatile List<ClassEntity> classList = new Vector<>();

    public static boolean exist(String name) {
        for (ClassEntity executorEntity : classList) {
            if (executorEntity.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public static boolean exist(ClassEntity entity) {
        for (ClassEntity executorEntity : classList) {
            if (executorEntity.equals(entity)) {
                return true;
            }
        }
        return false;
    }

    public static Integer push(ClassEntity executorEntity) {
        if (exist(executorEntity.getName())) {
            remove(executorEntity.getName());
        }
        classList.add(executorEntity);
        return classList.size();
    }

    public static Integer remove(String name) {
        int count = classList.size();
        for (int i = 0; i < classList.size(); i++) {
            if (name.equals(classList.get(i).getName())) {
                classList.remove(i);
                break;
            }
        }
        return count - classList.size();
    }

    public static ClassEntity get(String name) {
        for (ClassEntity executorEntity : classList) {
            if (executorEntity.getName().equals(name)) {
                return executorEntity;
            }
        }
        return null;
    }
}

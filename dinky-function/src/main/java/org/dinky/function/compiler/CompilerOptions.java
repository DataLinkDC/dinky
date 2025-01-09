package org.dinky.function.compiler;

import sun.misc.SharedSecrets;
import sun.misc.URLClassPath;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author: lwjhn
 * Date: 2024/6/20 15:29
 * Description:
 */
public class CompilerOptions {
    private static final Iterable<String> options ;

    private static final String classpath;

    static {
        try {
            Field field = URLClassPath.class.getDeclaredField("lmap");
            field.setAccessible(true);

            URLClassPath urlClassPath = SharedSecrets.getJavaNetAccess().getURLClassPath((URLClassLoader) CompilerOptions.class.getClassLoader());

            options = Collections.unmodifiableList(Arrays.asList("-encoding", "UTF-8", "-classpath",
                    classpath = String.join(File.pathSeparator, ((Map<String, Object>) field.get(urlClassPath)).keySet()
                            .stream().map(o->o.replaceAll("^file://", "")).collect(Collectors.toSet()))));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Iterable<String> getOptions() {
        return options;
    }

    public static String getClasspath() {
        return classpath;
    }
}

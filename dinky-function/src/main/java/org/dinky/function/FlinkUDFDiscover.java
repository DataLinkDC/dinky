package org.dinky.function;

import org.apache.commons.collections.CollectionUtils;
import org.apache.flink.table.catalog.FunctionLanguage;
import org.apache.flink.table.functions.UserDefinedFunction;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.dinky.function.data.model.UDF;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

public class FlinkUDFDiscover {

    private static final List<UDF> JAVA_STATIC_UDF_LIST = getCustomStaticUDFs();

    public static List<UDF> getCustomStaticUDFs() {
        if (CollectionUtils.isNotEmpty(JAVA_STATIC_UDF_LIST)) {
            return JAVA_STATIC_UDF_LIST;
        }

        Reflections reflections =
                new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forJavaClassPath()));
        Set<Class<?>> operations =
                reflections.get(Scanners.SubTypes.of(UserDefinedFunction.class).asClass());
        return operations.stream()
                .filter(operation ->
                        !operation.isInterface() && !operation.getName().startsWith("org.apache"))
                .map(operation -> UDF.builder()
                        .className(operation.getName())
                        .functionLanguage(FunctionLanguage.JAVA)
                        .build())
                .collect(Collectors.toList());
    }
}

package com.dlink.ud.compiler;

import com.dlink.ud.constant.PathConstant;
import com.dlink.ud.data.model.UDF;
import com.dlink.ud.util.ZipUtils;

import org.apache.flink.table.catalog.FunctionLanguage;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;

/**
 * @author ZackYoung
 * @since 0.6.8
 */
public class JVMPackage implements FunctionPackage {
    @Override
    public String[] pack(List<UDF> udfList, Integer missionId) {
        if (CollUtil.isEmpty(udfList)) {
            return new String[0];
        }
        List<String> classNameList = udfList.stream()
            .filter(udf -> udf.getFunctionLanguage() == FunctionLanguage.JAVA || udf.getFunctionLanguage() == FunctionLanguage.SCALA)
            .map(UDF::getClassName)
            .collect(Collectors.toList());
        String[] clazzs = new String[classNameList.size()];
        InputStream[] fileInputStreams = new InputStream[classNameList.size()];
        if (CollUtil.isEmpty(classNameList)) {
            return new String[0];
        }

        for (int i = 0; i < classNameList.size(); i++) {
            String className = classNameList.get(i);
            String classFile = StrUtil.replace(className, ".", "/") + ".class";
            String absoluteFilePath = PathConstant.getUdfCompilerJavaPath(missionId,classFile);

            clazzs[i] = classFile;
            fileInputStreams[i] = FileUtil.getInputStream(absoluteFilePath);
        }

        String jarPath = PathConstant.getUdfPackagePath(missionId,PathConstant.UDF_JAR_NAME);
        // 编译好的文件打包jar
        File file = FileUtil.file(jarPath);
        FileUtil.del(file);
        try (ZipUtils zipWriter = new ZipUtils(file, Charset.defaultCharset())) {
            zipWriter.add(clazzs, fileInputStreams);
        }
        return new String[] {jarPath};
    }
}

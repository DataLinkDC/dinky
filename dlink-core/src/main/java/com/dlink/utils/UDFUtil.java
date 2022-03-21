package com.dlink.utils;

import com.dlink.pool.ClassEntity;
import com.dlink.pool.ClassPool;
import groovy.lang.GroovyClassLoader;
import org.codehaus.groovy.control.CompilerConfiguration;

/**
 * UDFUtil
 *
 * @author wenmo
 * @since 2021/12/27 23:25
 */
public class UDFUtil {

    public static void buildClass(String code) {
        CustomStringJavaCompiler compiler = new CustomStringJavaCompiler(code);
        boolean res = compiler.compiler();
        if (res) {
            String className = compiler.getFullClassName();
            byte[] compiledBytes = compiler.getJavaFileObjectMap(className).getCompiledBytes();
            ClassPool.push(new ClassEntity(className, code, compiledBytes));
            System.out.println("编译成功");
            System.out.println("compilerTakeTime：" + compiler.getCompilerTakeTime());
            initClassLoader(className);
        } else {
            System.out.println("编译失败");
            System.out.println(compiler.getCompilerMessage());
        }
    }

    public static void initClassLoader(String name) {
        ClassEntity classEntity = ClassPool.get(name);
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        CompilerConfiguration config = new CompilerConfiguration();
        config.setSourceEncoding("UTF-8");
        GroovyClassLoader groovyClassLoader = new GroovyClassLoader(contextClassLoader, config);
        groovyClassLoader.setShouldRecompile(true);
        groovyClassLoader.defineClass(classEntity.getName(), classEntity.getClassByte());
        Thread.currentThread().setContextClassLoader(groovyClassLoader);
//        Class<?> clazz = groovyClassLoader.parseClass(codeSource,"com.dlink.ud.udf.SubstringFunction");
    }
}

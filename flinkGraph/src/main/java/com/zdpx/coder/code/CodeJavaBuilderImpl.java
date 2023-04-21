package com.zdpx.coder.code;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import com.zdpx.coder.CodeContext;
import com.zdpx.coder.Specifications;
import com.zdpx.coder.graph.Environment;
import lombok.extern.slf4j.Slf4j;

import javax.lang.model.element.Modifier;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class CodeJavaBuilderImpl implements CodeJavaBuilder {
    public static final String SRC_MAIN_JAVA_GENERATE = "flinkGraph/src/main/java/generate";

    private static final Path directory =Paths.get(SRC_MAIN_JAVA_GENERATE);

    private final CodeContext codeContext;

    public CodeJavaBuilderImpl(CodeContext codeContext) {
        this.codeContext = codeContext;
    }

    @Override
    public void registerUdfFunction(String udfFunctionName, String functionClass) {
        codeContext.getMain().addStatement("$L.createTemporarySystemFunction($S, $S)", Specifications.TABLE_ENV, udfFunctionName, functionClass);
    }

    @Override
    public void firstBuild() {
        final Environment environment = codeContext.getScene().getEnvironment();

        codeContext.getMain()
            .addStatement("$T $L = $T.getExecutionEnvironment()", Specifications.SEE, Specifications.ENV, Specifications.SEE)
            .addStatement("$N.setRuntimeMode($T.$L)", Specifications.ENV, Specifications.RUNTIME_EXECUTION_MODE, environment.getMode())
            .addStatement("$N.setParallelism($L)", Specifications.ENV, environment.getParallelism())
            .addStatement("$T $N = $T.create($N)", Specifications.STE, Specifications.TABLE_ENV, Specifications.STE, Specifications.ENV)
            .addCode(System.lineSeparator());
    }

    @Override
    public void generate(String sql) {
        CodeBlock cb = CodeBlock.builder().addStatement(Specifications.EXECUTE_SQL, Specifications.TABLE_ENV, sql).build();
        this.codeContext.getMain().addCode(cb).addCode(System.lineSeparator());
    }

    @Override
    public void generateJavaFunction(CodeBlock codeBlock) {
        codeContext.getMain().addCode(codeBlock).addCode(System.lineSeparator());
    }

    @Override
    public CodeContext getCodeContext() {
        return codeContext;
    }

    @Override
    public String lastBuild() {
        codeContext.getMain().addStatement("$N.execute()", Specifications.ENV);

        TypeSpec classBody = TypeSpec.classBuilder(codeContext.getScene().getEnvironment().getName())
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(codeContext.getMain().build())
                .build();

        JavaFile javaFile = JavaFile.builder(Specifications.COM_ZDPX_CJPG, classBody).build();

        try {
            String source = javaFile.toString();
            return reformat(source);
        } catch (Exception e) {
            log.error(String.format("write file %s error!Error: %s", directory, e.getMessage()));
        }
        return null;
    }


    public static String reformat(String source) throws IOException {
        return source;
    }

}

package org.dinky.zdpx.coder;

import com.google.common.base.Strings;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import lombok.extern.slf4j.Slf4j;

import javax.lang.model.element.Modifier;

/**
 * generate java code file that define the scene structure and execution logic.
 */
@Slf4j
public class CodeGenerator {

    public static void main(String[] args) {
        CodeGenerator cg = new CodeGenerator();
        String result = cg.generate();
        log.debug(result);
    }

    public String generate() {
        return generate("Test");
    }

    public String generate(String sceneName) {
        if (!validSceneName(sceneName)) {
            String err = "scene name illegal.";
            log.error(err);
            throw new IllegalArgumentException(err);
        }

        MethodSpec main =
            MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(String[].class, "args")
                .build();

        TypeSpec classBody =
            TypeSpec.classBuilder(sceneName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(main)
                .build();

        JavaFile javaFile = JavaFile.builder(Specifications.COM_ZDPX_CJPG, classBody).build();

        return javaFile.toString();
    }

    public boolean validSceneName(String sceneName) {
        log.debug("Scene name {}", sceneName);
        return !Strings.isNullOrEmpty(sceneName);
    }
}

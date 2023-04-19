package org.dinky.zdpx.coder;


import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.dinky.zdpx.coder.graph.Scene;

import javax.lang.model.element.Modifier;
import java.util.Set;

import static org.dinky.zdpx.coder.Specifications.EXCEPTION;


/**
 * 代码生成信息操作上下文
 *
 * @author Licho Sun
 */
public final class CodeContext {
    // 目标可运行类的构造器
    public final TypeSpec.Builder job;
    // main函数声明生成定义
    private final MethodSpec.Builder main = MethodSpec.methodBuilder("main")
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .returns(void.class)
        .addParameter(String[].class, "args")
        .addException(EXCEPTION);
    // 需要压制的警告信息
    public final Set<String> suppressedWarnings;

    private final Scene scene;

    public CodeContext(String className) {
        this(TypeSpec.classBuilder(className), null, null);
    }

    public CodeContext(String className, Set<String> suppressedWarnings) {
        this(TypeSpec.classBuilder(className), suppressedWarnings, null);
    }

    private CodeContext(Builder builder) {
        this(builder.job, builder.suppressedWarnings, builder.scene);
    }

    public CodeContext(TypeSpec.Builder job, Set<String> suppressedWarnings, Scene scene) {
        this.job = job;
        this.suppressedWarnings = suppressedWarnings;
        this.scene = scene;
    }

    //region builder
    public static Builder newBuilder(String className) {
        return new Builder(className);
    }

    public static Builder newBuilder(String className, Set<String> suppressedWarnings) {
        return new Builder(className, suppressedWarnings);
    }

    //region getter/setter
    public TypeSpec.Builder getJob() {
        return job;
    }

    public MethodSpec.Builder getMain() {
        return main;
    }

    public Set<String> getSuppressedWarnings() {
        return suppressedWarnings;
    }

    public Scene getScene() {
        return scene;
    }

    //endregion
    public static final class Builder {
        private final TypeSpec.Builder job;
        private final Set<String> suppressedWarnings;
        private Scene scene;

        private Builder(String className) {
            this.job = TypeSpec.classBuilder(className);
            this.suppressedWarnings = null;
        }

        private Builder(String className, Set<String> suppressedWarnings) {
            this.job = TypeSpec.classBuilder(className);
            this.suppressedWarnings = suppressedWarnings;
        }

        public Builder scene(Scene val) {
            scene = val;
            return this;
        }

        public CodeContext build() {
            return new CodeContext(this);
        }
    }
}

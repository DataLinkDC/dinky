package org.dinky.zdpx.coder.code;

import com.squareup.javapoet.CodeBlock;
import org.dinky.zdpx.coder.CodeContext;

/**
 *
 */
public interface CodeJavaBuilder extends CodeBuilder {

    void generateJavaFunction(CodeBlock codeBlock);

    CodeContext getCodeContext();

}

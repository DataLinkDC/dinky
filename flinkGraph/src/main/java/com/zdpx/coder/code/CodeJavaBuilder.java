package com.zdpx.coder.code;

import com.squareup.javapoet.CodeBlock;
import com.zdpx.coder.CodeContext;

/**
 *
 */
public interface CodeJavaBuilder extends CodeBuilder {

    void generateJavaFunction(CodeBlock codeBlock);

    CodeContext getCodeContext();

}

package org.dinky.data.vo;

import lombok.Getter;
import lombok.Setter;
import org.dinky.trans.dml.ExecuteJarOperation;

@Getter
@Setter
public class FlinkJarSqlConvertVO {
    private String initSqlStatement;
    private ExecuteJarOperation.JarSubmitParam jarSubmitParam;
}

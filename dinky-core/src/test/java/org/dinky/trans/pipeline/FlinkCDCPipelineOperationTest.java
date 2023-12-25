package org.dinky.trans.pipeline;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FlinkCDCPipelineOperationTest {

    @Test
    void getPipelineConfigure() {
        FlinkCDCPipelineOperation operation = new FlinkCDCPipelineOperation();
        String configure = "EXECUTE PIPELINE withYaml (a.b=1\n a.b.c=2)";
        String result = operation.getPipelineConfigure(configure);
        assertEquals("a.b=1\n a.b.c=2", result);
    }
}
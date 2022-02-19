package com.dlink.trans.ddl;

import com.dlink.cdc.FlinkCDCMergeBuilder;
import com.dlink.executor.Executor;
import com.dlink.model.FlinkCDCConfig;
import com.dlink.trans.AbstractOperation;
import com.dlink.trans.Operation;
import org.apache.flink.table.api.TableResult;

/**
 * CreateCDCSourceOperation
 *
 * @author wenmo
 * @since 2022/1/29 23:25
 */
public class CreateCDCSourceOperation extends AbstractOperation implements Operation {

    private String KEY_WORD = "EXECUTE CDCSOURCE";

    public CreateCDCSourceOperation() {
    }

    public CreateCDCSourceOperation(String statement) {
        super(statement);
    }

    @Override
    public String getHandle() {
        return KEY_WORD;
    }

    @Override
    public Operation create(String statement) {
        return new CreateCDCSourceOperation(statement);
    }

    @Override
    public TableResult build(Executor executor) {
        CDCSource cdcSource = CDCSource.build(statement);
        FlinkCDCConfig config = new FlinkCDCConfig(cdcSource.getHostname(), cdcSource.getPort(), cdcSource.getUsername()
                , cdcSource.getPassword(), cdcSource.getCheckpoint(), cdcSource.getParallelism(), cdcSource.getDatabase(), cdcSource.getTable()
                , cdcSource.getStartupMode(), cdcSource.getTopic(), cdcSource.getBrokers());
        try {
            FlinkCDCMergeBuilder.buildMySqlCDC(executor.getStreamExecutionEnvironment(), config);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

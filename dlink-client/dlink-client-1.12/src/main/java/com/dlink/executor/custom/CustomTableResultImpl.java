package com.dlink.executor.custom;

import org.apache.flink.annotation.Internal;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.table.api.*;
import org.apache.flink.table.utils.PrintUtils;
import org.apache.flink.types.Row;
import org.apache.flink.util.CloseableIterator;
import org.apache.flink.util.Preconditions;

import javax.annotation.Nullable;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

/**
 * 定制TableResultImpl
 * @author  wenmo
 * @since  2021/6/7 22:06
 **/
@Internal
class CustomTableResultImpl implements TableResult {
    public static final TableResult TABLE_RESULT_OK;
    private final JobClient jobClient;
    private final TableSchema tableSchema;
    private final ResultKind resultKind;
    private final CloseableRowIteratorWrapper data;
    private final PrintStyle printStyle;

    private CustomTableResultImpl(@Nullable JobClient jobClient, TableSchema tableSchema, ResultKind resultKind, CloseableIterator<Row> data, PrintStyle printStyle) {
        this.jobClient = jobClient;
        this.tableSchema = (TableSchema) Preconditions.checkNotNull(tableSchema, "tableSchema should not be null");
        this.resultKind = (ResultKind)Preconditions.checkNotNull(resultKind, "resultKind should not be null");
        Preconditions.checkNotNull(data, "data should not be null");
        this.data = new CloseableRowIteratorWrapper(data);
        this.printStyle = (PrintStyle)Preconditions.checkNotNull(printStyle, "printStyle should not be null");
    }

    public static TableResult buildTableResult(List<TableSchemaField> fields,List<Row> rows){
        Builder builder = builder().resultKind(ResultKind.SUCCESS);
        if(fields.size()>0) {
            TableSchema.Builder tableSchemaBuild = TableSchema.builder();
            for (int i = 0; i < fields.size(); i++) {
                tableSchemaBuild.field(fields.get(i).getName(),fields.get(i).getType());
            }
            builder.tableSchema(tableSchemaBuild.build()).data(rows);
        }
        return builder.build();
    }

    public Optional<JobClient> getJobClient() {
        return Optional.ofNullable(this.jobClient);
    }

    public void await() throws InterruptedException, ExecutionException {
        try {
            this.awaitInternal(-1L, TimeUnit.MILLISECONDS);
        } catch (TimeoutException var2) {
            ;
        }

    }

    public void await(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        this.awaitInternal(timeout, unit);
    }

    private void awaitInternal(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (this.jobClient != null) {
            ExecutorService executor = Executors.newFixedThreadPool(1, (r) -> {
                return new Thread(r, "TableResult-await-thread");
            });

            try {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    while(!this.data.isFirstRowReady()) {
                        try {
                            Thread.sleep(100L);
                        } catch (InterruptedException var2) {
                            throw new TableException("Thread is interrupted");
                        }
                    }

                }, executor);
                if (timeout >= 0L) {
                    future.get(timeout, unit);
                } else {
                    future.get();
                }
            } finally {
                executor.shutdown();
            }

        }
    }

    public TableSchema getTableSchema() {
        return this.tableSchema;
    }

    public ResultKind getResultKind() {
        return this.resultKind;
    }

    public CloseableIterator<Row> collect() {
        return this.data;
    }

    public void print() {
        Iterator<Row> it = this.collect();
        if (this.printStyle instanceof TableauStyle) {
            int maxColumnWidth = ((TableauStyle)this.printStyle).getMaxColumnWidth();
            String nullColumn = ((TableauStyle)this.printStyle).getNullColumn();
            boolean deriveColumnWidthByType = ((TableauStyle)this.printStyle).isDeriveColumnWidthByType();
            boolean printRowKind = ((TableauStyle)this.printStyle).isPrintRowKind();
            PrintUtils.printAsTableauForm(this.getTableSchema(), it, new PrintWriter(System.out), maxColumnWidth, nullColumn, deriveColumnWidthByType, printRowKind);
        } else {
            if (!(this.printStyle instanceof RawContentStyle)) {
                throw new TableException("Unsupported print style: " + this.printStyle);
            }

            while(it.hasNext()) {
                System.out.println(String.join(",", PrintUtils.rowToString((Row)it.next())));
            }
        }

    }

    public static Builder builder() {
        return new Builder();
    }

    static {
        TABLE_RESULT_OK = builder().resultKind(ResultKind.SUCCESS).tableSchema(TableSchema.builder().field("result", DataTypes.STRING()).build()).data(Collections.singletonList(Row.of(new Object[]{"OK"}))).build();
    }

    private static final class CloseableRowIteratorWrapper implements CloseableIterator<Row> {
        private final CloseableIterator<Row> iterator;
        private boolean isFirstRowReady;

        private CloseableRowIteratorWrapper(CloseableIterator<Row> iterator) {
            this.isFirstRowReady = false;
            this.iterator = iterator;
        }

        public void close() throws Exception {
            this.iterator.close();
        }

        public boolean hasNext() {
            boolean hasNext = this.iterator.hasNext();
            this.isFirstRowReady = this.isFirstRowReady || hasNext;
            return hasNext;
        }

        public Row next() {
            Row next = (Row)this.iterator.next();
            this.isFirstRowReady = true;
            return next;
        }

        public boolean isFirstRowReady() {
            return this.isFirstRowReady || this.hasNext();
        }
    }

    private static final class RawContentStyle implements PrintStyle {
        private RawContentStyle() {
        }
    }

    private static final class TableauStyle implements PrintStyle {
        private final boolean deriveColumnWidthByType;
        private final int maxColumnWidth;
        private final String nullColumn;
        private final boolean printRowKind;

        private TableauStyle(int maxColumnWidth, String nullColumn, boolean deriveColumnWidthByType, boolean printRowKind) {
            this.deriveColumnWidthByType = deriveColumnWidthByType;
            this.maxColumnWidth = maxColumnWidth;
            this.nullColumn = nullColumn;
            this.printRowKind = printRowKind;
        }

        public boolean isDeriveColumnWidthByType() {
            return this.deriveColumnWidthByType;
        }

        int getMaxColumnWidth() {
            return this.maxColumnWidth;
        }

        String getNullColumn() {
            return this.nullColumn;
        }

        public boolean isPrintRowKind() {
            return this.printRowKind;
        }
    }

    public interface PrintStyle {
        static PrintStyle tableau(int maxColumnWidth, String nullColumn, boolean deriveColumnWidthByType, boolean printRowKind) {
            Preconditions.checkArgument(maxColumnWidth > 0, "maxColumnWidth should be greater than 0");
            Preconditions.checkNotNull(nullColumn, "nullColumn should not be null");
            return new TableauStyle(maxColumnWidth, nullColumn, deriveColumnWidthByType, printRowKind);
        }

        static PrintStyle rawContent() {
            return new RawContentStyle();
        }
    }

    public static class Builder {
        private JobClient jobClient;
        private TableSchema tableSchema;
        private ResultKind resultKind;
        private CloseableIterator<Row> data;
        private PrintStyle printStyle;

        private Builder() {
            this.jobClient = null;
            this.tableSchema = null;
            this.resultKind = null;
            this.data = null;
            this.printStyle = PrintStyle.tableau(2147483647, "(NULL)", false, false);
        }

        public Builder jobClient(JobClient jobClient) {
            this.jobClient = jobClient;
            return this;
        }

        public Builder tableSchema(TableSchema tableSchema) {
            Preconditions.checkNotNull(tableSchema, "tableSchema should not be null");
            this.tableSchema = tableSchema;
            return this;
        }

        public Builder resultKind(ResultKind resultKind) {
            Preconditions.checkNotNull(resultKind, "resultKind should not be null");
            this.resultKind = resultKind;
            return this;
        }

        public Builder data(CloseableIterator<Row> rowIterator) {
            Preconditions.checkNotNull(rowIterator, "rowIterator should not be null");
            this.data = rowIterator;
            return this;
        }

        public Builder data(List<Row> rowList) {
            Preconditions.checkNotNull(rowList, "listRows should not be null");
            this.data = CloseableIterator.adapterForIterator(rowList.iterator());
            return this;
        }

        public Builder setPrintStyle(PrintStyle printStyle) {
            Preconditions.checkNotNull(printStyle, "printStyle should not be null");
            this.printStyle = printStyle;
            return this;
        }

        public TableResult build() {
            return new CustomTableResultImpl(this.jobClient, this.tableSchema, this.resultKind, this.data, this.printStyle);
        }
    }
}

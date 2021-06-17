package com.dlink.executor.custom;

import org.apache.flink.annotation.Internal;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.table.api.*;
import org.apache.flink.table.catalog.Column;
import org.apache.flink.table.catalog.ResolvedSchema;
import org.apache.flink.table.types.DataType;
import org.apache.flink.table.utils.PrintUtils;
import org.apache.flink.types.Row;
import org.apache.flink.util.CloseableIterator;
import org.apache.flink.util.Preconditions;

import javax.annotation.Nullable;
import java.io.PrintWriter;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.*;

/**
 * 定制TableResultImpl
 * @author  wenmo
 * @since  2021/6/7 22:06
 **/
@Internal
public class CustomTableResultImpl implements TableResult {
    public static final TableResult TABLE_RESULT_OK;
    private final JobClient jobClient;
    private final ResolvedSchema resolvedSchema;
    private final ResultKind resultKind;
    private final CustomTableResultImpl.CloseableRowIteratorWrapper data;
    private final CustomTableResultImpl.PrintStyle printStyle;
    private final ZoneId sessionTimeZone;

    private CustomTableResultImpl(@Nullable JobClient jobClient, ResolvedSchema resolvedSchema, ResultKind resultKind, CloseableIterator<Row> data, CustomTableResultImpl.PrintStyle printStyle, ZoneId sessionTimeZone) {
        this.jobClient = jobClient;
        this.resolvedSchema = (ResolvedSchema)Preconditions.checkNotNull(resolvedSchema, "resolvedSchema should not be null");
        this.resultKind = (ResultKind)Preconditions.checkNotNull(resultKind, "resultKind should not be null");
        Preconditions.checkNotNull(data, "data should not be null");
        this.data = new CustomTableResultImpl.CloseableRowIteratorWrapper(data);
        this.printStyle = (CustomTableResultImpl.PrintStyle)Preconditions.checkNotNull(printStyle, "printStyle should not be null");
        this.sessionTimeZone = (ZoneId)Preconditions.checkNotNull(sessionTimeZone, "sessionTimeZone should not be null");
    }

    public static TableResult buildTableResult(List<TableSchemaField> fields,List<Row> rows){
        Builder builder = builder().resultKind(ResultKind.SUCCESS);
        if(fields.size()>0) {
            List<String> columnNames = new ArrayList<>();
            List<DataType> columnTypes = new ArrayList<>();
            for (int i = 0; i < fields.size(); i++) {
                columnNames.add(fields.get(i).getName());
                columnTypes.add(fields.get(i).getType());
            }
            builder.schema(ResolvedSchema.physical(columnNames,columnTypes)).data(rows);
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

    public ResolvedSchema getResolvedSchema() {
        return this.resolvedSchema;
    }

    public ResultKind getResultKind() {
        return this.resultKind;
    }

    public CloseableIterator<Row> collect() {
        return this.data;
    }

    public void print() {
        Iterator<Row> it = this.collect();
        if (this.printStyle instanceof CustomTableResultImpl.TableauStyle) {
            int maxColumnWidth = ((CustomTableResultImpl.TableauStyle)this.printStyle).getMaxColumnWidth();
            String nullColumn = ((CustomTableResultImpl.TableauStyle)this.printStyle).getNullColumn();
            boolean deriveColumnWidthByType = ((CustomTableResultImpl.TableauStyle)this.printStyle).isDeriveColumnWidthByType();
            boolean printRowKind = ((CustomTableResultImpl.TableauStyle)this.printStyle).isPrintRowKind();
            PrintUtils.printAsTableauForm(this.getResolvedSchema(), it, new PrintWriter(System.out), maxColumnWidth, nullColumn, deriveColumnWidthByType, printRowKind, this.sessionTimeZone);
        } else {
            if (!(this.printStyle instanceof CustomTableResultImpl.RawContentStyle)) {
                throw new TableException("Unsupported print style: " + this.printStyle);
            }

            while(it.hasNext()) {
                System.out.println(String.join(",", PrintUtils.rowToString((Row)it.next(), this.getResolvedSchema(), this.sessionTimeZone)));
            }
        }

    }

    public static CustomTableResultImpl.Builder builder() {
        return new CustomTableResultImpl.Builder();
    }

    static {
        TABLE_RESULT_OK = builder().resultKind(ResultKind.SUCCESS).schema(ResolvedSchema.of(new Column[]{Column.physical("result", DataTypes.STRING())})).data(Collections.singletonList(Row.of(new Object[]{"OK"}))).build();
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

    private static final class RawContentStyle implements CustomTableResultImpl.PrintStyle {
        private RawContentStyle() {
        }
    }

    private static final class TableauStyle implements CustomTableResultImpl.PrintStyle {
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
        static CustomTableResultImpl.PrintStyle tableau(int maxColumnWidth, String nullColumn, boolean deriveColumnWidthByType, boolean printRowKind) {
            Preconditions.checkArgument(maxColumnWidth > 0, "maxColumnWidth should be greater than 0");
            Preconditions.checkNotNull(nullColumn, "nullColumn should not be null");
            return new CustomTableResultImpl.TableauStyle(maxColumnWidth, nullColumn, deriveColumnWidthByType, printRowKind);
        }

        static CustomTableResultImpl.PrintStyle rawContent() {
            return new CustomTableResultImpl.RawContentStyle();
        }
    }

    public static class Builder {
        private JobClient jobClient;
        private ResolvedSchema resolvedSchema;
        private ResultKind resultKind;
        private CloseableIterator<Row> data;
        private CustomTableResultImpl.PrintStyle printStyle;
        private ZoneId sessionTimeZone;

        private Builder() {
            this.jobClient = null;
            this.resolvedSchema = null;
            this.resultKind = null;
            this.data = null;
            this.printStyle = CustomTableResultImpl.PrintStyle.tableau(2147483647, "(NULL)", false, false);
            this.sessionTimeZone = ZoneId.of("UTC");
        }

        public CustomTableResultImpl.Builder jobClient(JobClient jobClient) {
            this.jobClient = jobClient;
            return this;
        }

        public CustomTableResultImpl.Builder schema(ResolvedSchema resolvedSchema) {
            Preconditions.checkNotNull(resolvedSchema, "resolvedSchema should not be null");
            this.resolvedSchema = resolvedSchema;
            return this;
        }

        public CustomTableResultImpl.Builder resultKind(ResultKind resultKind) {
            Preconditions.checkNotNull(resultKind, "resultKind should not be null");
            this.resultKind = resultKind;
            return this;
        }

        public CustomTableResultImpl.Builder data(CloseableIterator<Row> rowIterator) {
            Preconditions.checkNotNull(rowIterator, "rowIterator should not be null");
            this.data = rowIterator;
            return this;
        }

        public CustomTableResultImpl.Builder data(List<Row> rowList) {
            Preconditions.checkNotNull(rowList, "listRows should not be null");
            this.data = CloseableIterator.adapterForIterator(rowList.iterator());
            return this;
        }

        public CustomTableResultImpl.Builder setPrintStyle(CustomTableResultImpl.PrintStyle printStyle) {
            Preconditions.checkNotNull(printStyle, "printStyle should not be null");
            this.printStyle = printStyle;
            return this;
        }

        public CustomTableResultImpl.Builder setSessionTimeZone(ZoneId sessionTimeZone) {
            Preconditions.checkNotNull(sessionTimeZone, "sessionTimeZone should not be null");
            this.sessionTimeZone = sessionTimeZone;
            return this;
        }

        public TableResult build() {
            return new CustomTableResultImpl(this.jobClient, this.resolvedSchema, this.resultKind, this.data, this.printStyle, this.sessionTimeZone);
        }
    }
}
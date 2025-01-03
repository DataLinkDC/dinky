package org.apache.lineage.flink.sql.metadata;

import io.openlineage.client.Clients;
import io.openlineage.client.OpenLineage;
import io.openlineage.client.OpenLineage.*;
import io.openlineage.client.OpenLineageClient;
import io.openlineage.client.utils.UUIDUtils;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.metadata.RelColumnOrigin;
import org.apache.calcite.rel.metadata.RelMetadataQuery;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.table.api.ValidationException;
import org.apache.flink.table.api.internal.TableEnvironmentInternal;
import org.apache.flink.table.catalog.*;
import org.apache.flink.table.operations.*;
import org.apache.flink.table.operations.ddl.CreateTableOperation;
import org.apache.flink.table.planner.operations.PlannerQueryOperation;
import org.apache.flink.table.planner.plan.schema.TableSourceTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.lineage.flink.sql.metadata.Constant.DELIMITER;

/**
 * Author: lwjhn
 * Date: 2024/9/23 16:27
 * Description:
 */
public class LineageHandler {
    protected static final Logger LOG = LoggerFactory.getLogger(LineageHandler.class);
    protected static final OpenLineage openLineage;
    protected static OpenLineageClient openLineageClient = null;

    public static OpenLineageClient getClient() {
        return openLineageClient;
    }

    public static OpenLineage getOpenLineage() {
        return openLineage;
    }

    public static void init() {
        try {
            openLineageClient = Clients.newClient();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    static {
        openLineage = new OpenLineage(URI.create("https://github.com/apache/flink"));
        init();
    }

    public static void analyze(TableEnvironmentInternal internal, Operation operation, String statement) {
        analyze(internal.getCatalogManager(), operation, statement);
    }

    public static void analyze(CatalogManager catalogManager, Operation operation, String statement) {
        if (openLineageClient == null) {
            return;
        }

        if (operation instanceof ModifyOperation) {
            ContextResolvedTable contextResolvedTable = null;
            if (operation instanceof SinkModifyOperation) {
                contextResolvedTable = ((SinkModifyOperation) operation).getContextResolvedTable();
            } else if (operation instanceof CreateTableASOperation) {
                CreateTableOperation ctOperation = ((CreateTableASOperation) operation).getCreateTableOperation();
                contextResolvedTable = ContextResolvedTable.permanent(ctOperation.getTableIdentifier(), catalogManager.getCatalog(ctOperation.getTableIdentifier().getCatalogName()).orElse(null), catalogManager.resolveCatalogBaseTable(ctOperation.getCatalogTable()));
            } else if (operation instanceof ExternalModifyOperation) {
                contextResolvedTable = ((ExternalModifyOperation) operation).getContextResolvedTable();
            }
            if (contextResolvedTable != null) {
                build(contextResolvedTable, ((PlannerQueryOperation) ((ModifyOperation) operation).getChild()).getCalciteTree(), statement);
            }
        }
    }

    protected static void build(ContextResolvedTable contextResolvedTable, RelNode optRelNode, String statement) {
        ObjectIdentifier sinkTable = contextResolvedTable.getIdentifier();
        String sinkSummary = sinkTable.asSummaryString();
        ResolvedSchema resolvedSchema = contextResolvedTable.getResolvedSchema();

        // target columns
        List<Column> targetColumnList = resolvedSchema.getColumns();
        // check the size of query and sink fields match
        validateSchema(sinkSummary, optRelNode, targetColumnList);

        List<SchemaDatasetFacetFields> sinkFields = new ArrayList<>();
        Map<String, RelOptTable> sourceDataset = new LinkedHashMap<>();
        ColumnLineageDatasetFacetFieldsBuilder columnLineageFields = new ColumnLineageDatasetFacetFieldsBuilder();

        RelMetadataQuery metadataQuery = optRelNode.getCluster().getMetadataQuery();
        for (int index = 0; index < targetColumnList.size(); index++) {
            Column column = targetColumnList.get(index);
            String targetColumn = column.getName();

            LOG.debug("**********************************************************");
            LOG.debug("==> Target table: {}, column: {}, type: {}, description: {}", sinkSummary, targetColumn, column.getDataType().toString(), column.getComment().orElse(null));
            sinkFields.add(openLineage.newSchemaDatasetFacetFieldsBuilder().name(targetColumn).type(column.getDataType().toString()).description(column.getComment().orElse(null)).build());
            List<ColumnLineageDatasetFacetFieldsAdditionalInputFields> additionalInputFields = new ArrayList<>();
            columnLineageFields.put(targetColumn, new ColumnLineageDatasetFacetFieldsAdditionalBuilder().inputFields(additionalInputFields).build());

            Set<RelColumnOrigin> relColumnOriginSet = metadataQuery.getColumnOrigins(optRelNode, index);
            if (CollectionUtils.isNotEmpty(relColumnOriginSet)) {
                for (RelColumnOrigin rco : relColumnOriginSet) {
                    String sourceTable = null;
                    String sourceColumn = null;
                    ColumnLineageDatasetFacetFieldsAdditionalInputFieldsBuilder fieldsBuilder = new ColumnLineageDatasetFacetFieldsAdditionalInputFieldsBuilder();
                    // table
                    RelOptTable table = rco.getOriginTable();
                    if (!(table instanceof LineageRelColumnOrigin.NullRelOptTable)) {
                        sourceTable = String.join(DELIMITER, table.getQualifiedName());
                        // filed
                        int ordinal = rco.getOriginColumnOrdinal();
                        List<String> fieldNames = ((TableSourceTable) table).contextResolvedTable().getResolvedSchema().getColumnNames();
                        sourceColumn = fieldNames.get(ordinal);
                        if (!sourceDataset.containsKey(sourceTable)) {
                            sourceDataset.put(sourceTable, table);
                        }
                    } else {
                        fieldsBuilder.namespace(sinkTable.getCatalogName());
                    }
//                    LOG.debug("----------------------------------------------------------");
//                    LOG.debug("Source table: {}", sourceTable);
//                    LOG.debug("Source column: {}", sourceColumn);
                    String transform = LineageRelColumnOrigin.getTransform(rco);
                    if (StringUtils.isNotEmpty(transform)) {
//                        LOG.debug("transform: {}", transform);
                        fieldsBuilder.transformations(Collections.singletonList(openLineage.newColumnLineageDatasetFacetFieldsAdditionalInputFieldsTransformationsBuilder().description(transform).put("operation", transform).build()));
                    }

                    if (StringUtils.isNotEmpty(sourceColumn)) {
                        fieldsBuilder.namespace(table.getQualifiedName().get(0)).name(sourceTable).field(sourceColumn);
                        additionalInputFields.add(fieldsBuilder.build());
                    }

                    LOG.debug("==> source table: {}, column: {}, transform: {}", sourceTable, sourceColumn, transform);
                }
            }
        }

        OutputDataset outputDatasets = openLineage.newOutputDatasetBuilder().namespace(sinkTable.getCatalogName()).name(sinkSummary)  //.name(sinkTable.getDatabaseName()+ DELIMITER +sinkTable.getObjectName())
                .facets(openLineage.newDatasetFacetsBuilder().columnLineage(openLineage.newColumnLineageDatasetFacetBuilder().fields(columnLineageFields.build()).build()).dataSource(openLineage.newDatasourceDatasetFacet(sinkTable.getCatalogName() + "." + sinkTable.getDatabaseName(), URI.create("flink://" + sinkTable.getCatalogName() + "/" + sinkTable.getDatabaseName()))).schema(openLineage.newSchemaDatasetFacetBuilder().fields(sinkFields).build()).build()).build();


        // 2. Build lineage based from RelMetadataQuery
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        UUID runId = UUIDUtils.generateNewUUID();
        JobFacets jobFacets = openLineage.newJobFacetsBuilder().sql(openLineage.newSQLJobFacet(statement)).documentation(openLineage.newDocumentationJobFacet("flink application .")).build();

        RunEventBuilder runEventBuilder = openLineage.newRunEventBuilder().eventType(RunEvent.EventType.COMPLETE).eventTime(now).run(openLineage.newRunBuilder().runId(runId).facets(openLineage.newRunFacetsBuilder().nominalTime(openLineage.newNominalTimeRunFacet(now, now)).build()).build()).job(openLineage.newJobBuilder().namespace(sinkTable.getCatalogName()).name(sinkTable.asSummaryString()).facets(jobFacets).build());

        RunEvent runEvent = runEventBuilder.inputs(sourceDataset.entrySet().stream().map(entry -> {
            String name = entry.getKey();
            RelOptTable table = entry.getValue();
            List<String> qualifiedName = table.getQualifiedName();
            String namespace = qualifiedName.size()>2 ? qualifiedName.get(0) : "default_catalog";
            String db = qualifiedName.size()>1 ? qualifiedName.get(qualifiedName.size()-1) : "default_database";
            return openLineage.newInputDatasetBuilder().namespace(table.getQualifiedName().get(0)).name(name).facets(openLineage.newDatasetFacetsBuilder().dataSource(openLineage.newDatasourceDatasetFacet(namespace + DELIMITER + db, URI.create("flink://" + namespace + "/" + db))).schema(openLineage.newSchemaDatasetFacetBuilder().fields(table.getRowType().getFieldList().stream().map(field -> openLineage.newSchemaDatasetFacetFieldsBuilder().name(field.getName()).type(field.getType().toString()).description(field.getName()).build()).collect(Collectors.toList())).build()).build()).build();
        }).collect(Collectors.toList())).outputs(Collections.singletonList(outputDatasets)).build();

//        String event = OpenLineageClientUtils.toJson(runEvent);
//        LOG.info("event: {}", event);
        LOG.info("==> openLineage emit...");
        openLineageClient.emit(runEvent);
    }


    private static void validateSchema(String sinkTable, RelNode relNode, List<Column> sinkFieldList) {
        List<String> queryFieldList = relNode.getRowType().getFieldNames();
        if (queryFieldList.size() != sinkFieldList.size()) {
            throw new ValidationException(String.format("Column types of query result and sink for %s do not match.\n" + "Query schema: %s\n" + "Sink schema:  %s", sinkTable, queryFieldList, sinkFieldList));
        }
    }
}

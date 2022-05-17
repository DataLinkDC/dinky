package com.dlink.utils;

import org.apache.flink.api.common.JobID;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.catalog.CatalogManager;
import org.apache.flink.table.catalog.ObjectIdentifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * FlinkUtil
 *
 * @author wenmo
 * @since 2021/10/22 10:02
 */
public class FlinkUtil {

    public static List<String> getFieldNamesFromCatalogManager(CatalogManager catalogManager, String catalog, String database, String table) {
        Optional<CatalogManager.TableLookupResult> tableOpt = catalogManager.getTable(
                ObjectIdentifier.of(catalog, database, table)
        );
        if (tableOpt.isPresent()) {
            return tableOpt.get().getResolvedSchema().getColumnNames();
        } else {
            return new ArrayList<String>();
        }
    }

    public static List<String> catchColumn(TableResult tableResult) {
        return tableResult.getResolvedSchema().getColumnNames();
    }

    public static String triggerSavepoint(ClusterClient clusterClient, String jobId, String savePoint) throws ExecutionException, InterruptedException {
        return clusterClient.triggerSavepoint(JobID.fromHexString(jobId), savePoint).get().toString();
    }

    public static String stopWithSavepoint(ClusterClient clusterClient, String jobId, String savePoint) throws ExecutionException, InterruptedException {
        return clusterClient.stopWithSavepoint(JobID.fromHexString(jobId), true, savePoint).get().toString();
    }

    public static String cancelWithSavepoint(ClusterClient clusterClient, String jobId, String savePoint) throws ExecutionException, InterruptedException {
        return clusterClient.cancelWithSavepoint(JobID.fromHexString(jobId), savePoint).get().toString();
    }
}

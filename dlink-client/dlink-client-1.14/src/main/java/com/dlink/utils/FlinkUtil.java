package com.dlink.utils;

import org.apache.flink.table.catalog.CatalogManager;
import org.apache.flink.table.catalog.ObjectIdentifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * FlinkUtil
 *
 * @author wenmo
 * @since 2021/10/22 10:02
 */
public class FlinkUtil {

    public static List<String> getFieldNamesFromCatalogManager(CatalogManager catalogManager, String catalog, String database, String table){
        Optional<CatalogManager.TableLookupResult> tableOpt = catalogManager.getTable(
                ObjectIdentifier.of(catalog, database, table)
        );
        if (tableOpt.isPresent()) {
            return tableOpt.get().getResolvedSchema().getColumnNames();
        }else{
            return new ArrayList<String>();
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.phoenix.compat.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.exceptions.DeserializationException;
import org.apache.hadoop.hbase.io.compress.Compression.Algorithm;
import org.apache.hadoop.hbase.io.encoding.DataBlockEncoding;
import org.apache.hadoop.hbase.io.hfile.HFileContext;
import org.apache.hadoop.hbase.io.hfile.HFileContextBuilder;
import org.apache.hadoop.hbase.regionserver.HStore;
import org.apache.hadoop.hbase.regionserver.StoreFileWriter;
import org.apache.hadoop.hbase.security.access.Permission;
import org.apache.hadoop.hbase.security.access.PermissionStorage;
import org.apache.hadoop.hbase.util.ChecksumType;
import org.apache.hbase.thirdparty.com.google.common.collect.ListMultimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class CompatUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        CompatUtil.class);

    private CompatUtil() {
        //Not to be instantiated
    }

    public static int getCellSerializedSize(Cell cell) {
        return cell.getSerializedSize();
    }

    public static ListMultimap<String, ? extends Permission> readPermissions(
            byte[] data, Configuration conf) throws DeserializationException {
        return PermissionStorage.readPermissions(data, conf);
    }

    public static HFileContext createHFileContext(Configuration conf, Algorithm compression,
            Integer blockSize, DataBlockEncoding encoding, CellComparator comparator) {

        return new HFileContextBuilder()
            .withCompression(compression)
            .withChecksumType(HStore.getChecksumType(conf))
            .withBytesPerCheckSum(HStore.getBytesPerChecksum(conf))
            .withBlockSize(blockSize)
            .withDataBlockEncoding(encoding)
            .build();
    }

    public static HFileContextBuilder withComparator(HFileContextBuilder contextBuilder,
            CellComparatorImpl cellComparator) {
        return contextBuilder.withCellComparator(cellComparator);
    }

    public static StoreFileWriter.Builder withComparator(StoreFileWriter.Builder builder,
            CellComparatorImpl cellComparator) {
        return builder;
    }

    public static Scan getScanForTableName(Connection conn, TableName tableName) {
        return MetaTableAccessor.getScanForTableName(conn, tableName);
    }

    /**
     * HBase 2.3+ has storeRefCount available in RegionMetrics
     *
     * @param admin Admin instance
     * @return true if any region has refCount leakage
     * @throws IOException if something went wrong while connecting to Admin
     */
    public synchronized static boolean isAnyStoreRefCountLeaked(Admin admin)
            throws IOException {
        int retries = 5;
        while (retries > 0) {
            boolean isStoreRefCountLeaked = isStoreRefCountLeaked(admin);
            if (!isStoreRefCountLeaked) {
                return false;
            }
            retries--;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LOGGER.error("Interrupted while sleeping", e);
                break;
            }
        }
        return true;
    }

    private static boolean isStoreRefCountLeaked(Admin admin)
            throws IOException {
        for (ServerName serverName : admin.getRegionServers()) {
            for (RegionMetrics regionMetrics : admin.getRegionMetrics(serverName)) {
                int regionTotalRefCount = regionMetrics.getStoreRefCount();
                if (regionTotalRefCount > 0) {
                    LOGGER.error("Region {} has refCount leak. Total refCount"
                            + " of all storeFiles combined for the region: {}",
                        regionMetrics.getNameAsString(), regionTotalRefCount);
                    return true;
                }
            }
        }
        return false;
    }

    public static ChecksumType getChecksumType(Configuration conf) {
        return HStore.getChecksumType(conf);
    }

    public static int getBytesPerChecksum(Configuration conf) {
        return HStore.getBytesPerChecksum(conf);
    }

}

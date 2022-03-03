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
package org.apache.phoenix.compat.hbase.coprocessor;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.KeepDeletedCells;
import org.apache.hadoop.hbase.MemoryCompactionPolicy;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptor;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.RegionObserver;
import org.apache.hadoop.hbase.regionserver.FlushLifeCycleTracker;
import org.apache.hadoop.hbase.regionserver.ScanOptions;
import org.apache.hadoop.hbase.regionserver.ScanType;
import org.apache.hadoop.hbase.regionserver.Store;
import org.apache.hadoop.hbase.regionserver.compactions.CompactionLifeCycleTracker;
import org.apache.hadoop.hbase.regionserver.compactions.CompactionRequest;
import org.apache.hadoop.hbase.util.EnvironmentEdgeManager;

import java.io.IOException;

public class CompatBaseScannerRegionObserver implements RegionObserver {

    public static final String PHOENIX_MAX_LOOKBACK_AGE_CONF_KEY =
        "phoenix.max.lookback.age.seconds";
    public static final int DEFAULT_PHOENIX_MAX_LOOKBACK_AGE = 0;

    public void preCompactScannerOpen(ObserverContext<RegionCoprocessorEnvironment> c, Store store,
                                             ScanType scanType, ScanOptions options, CompactionLifeCycleTracker tracker,
                                             CompactionRequest request) throws IOException {
        Configuration conf = c.getEnvironment().getConfiguration();
        if (isMaxLookbackTimeEnabled(conf)) {
            setScanOptionsForFlushesAndCompactions(conf, options, store, scanType);
        }
    }

    public void preFlushScannerOpen(ObserverContext<RegionCoprocessorEnvironment> c, Store store,
                                                ScanOptions options, FlushLifeCycleTracker tracker) throws IOException {
        Configuration conf = c.getEnvironment().getConfiguration();
        if (isMaxLookbackTimeEnabled(conf)) {
            setScanOptionsForFlushesAndCompactions(conf, options, store, ScanType.COMPACT_RETAIN_DELETES);
        }
    }

    public void preMemStoreCompactionCompactScannerOpen(
        ObserverContext<RegionCoprocessorEnvironment> c, Store store, ScanOptions options)
        throws IOException {
        Configuration conf = c.getEnvironment().getConfiguration();
        if (isMaxLookbackTimeEnabled(conf)) {
            MemoryCompactionPolicy inMemPolicy =
                store.getColumnFamilyDescriptor().getInMemoryCompaction();
            ScanType scanType;
            //the eager and adaptive in-memory compaction policies can purge versions; the others
            // can't. (Eager always does; adaptive sometimes does)
            if (inMemPolicy.equals(MemoryCompactionPolicy.EAGER) ||
                inMemPolicy.equals(MemoryCompactionPolicy.ADAPTIVE)) {
                scanType = ScanType.COMPACT_DROP_DELETES;
            } else {
                scanType = ScanType.COMPACT_RETAIN_DELETES;
            }
            setScanOptionsForFlushesAndCompactions(conf, options, store, scanType);
        }
    }

    public void preStoreScannerOpen(ObserverContext<RegionCoprocessorEnvironment> ctx, Store store,
                                           ScanOptions options) throws IOException {

        if (!storeFileScanDoesntNeedAlteration(options)) {
            //PHOENIX-4277 -- When doing a point-in-time (SCN) Scan, HBase by default will hide
            // mutations that happen before a delete marker. This overrides that behavior.
            options.setMinVersions(options.getMinVersions());
            KeepDeletedCells keepDeletedCells = KeepDeletedCells.TRUE;
            if (store.getColumnFamilyDescriptor().getTimeToLive() != HConstants.FOREVER) {
                keepDeletedCells = KeepDeletedCells.TTL;
            }
            options.setKeepDeletedCells(keepDeletedCells);
        }
    }

    private boolean storeFileScanDoesntNeedAlteration(ScanOptions options) {
        Scan scan = options.getScan();
        boolean isRaw = scan.isRaw();
        //true if keep deleted cells is either TRUE or TTL
        boolean keepDeletedCells = options.getKeepDeletedCells().equals(KeepDeletedCells.TRUE) ||
            options.getKeepDeletedCells().equals(KeepDeletedCells.TTL);
        boolean timeRangeIsLatest = scan.getTimeRange().getMax() == HConstants.LATEST_TIMESTAMP;
        boolean timestampIsTransactional =
            isTransactionalTimestamp(scan.getTimeRange().getMax());
        return isRaw
            || keepDeletedCells
            || timeRangeIsLatest
            || timestampIsTransactional;
    }

    private boolean isTransactionalTimestamp(long ts) {
        //have to use the HBase edge manager because the Phoenix one is in phoenix-core
        return ts > (long) (EnvironmentEdgeManager.currentTime() * 1.1);
    }

    /*
     * If KeepDeletedCells.FALSE, KeepDeletedCells.TTL ,
     * let delete markers age once lookback age is done.
     */
    public KeepDeletedCells getKeepDeletedCells(ScanOptions options, ScanType scanType) {
        //if we're doing a minor compaction or flush, always set keep deleted cells
        //to true. Otherwise, if keep deleted cells is false or TTL, use KeepDeletedCells TTL,
        //where the value of the ttl might be overriden to the max lookback age elsewhere
        return (options.getKeepDeletedCells() == KeepDeletedCells.TRUE
                || scanType.equals(ScanType.COMPACT_RETAIN_DELETES)) ?
                KeepDeletedCells.TRUE : KeepDeletedCells.TTL;
    }

    /*
     * if the user set a TTL we should leave MIN_VERSIONS at the default (0 in most of the cases).
     * Otherwise the data (1st version) will not be removed after the TTL. If no TTL, we want
     * Math.max(maxVersions, minVersions, 1)
     */
    public int getMinVersions(ScanOptions options, ColumnFamilyDescriptor cfDescriptor) {
        return cfDescriptor.getTimeToLive() != HConstants.FOREVER ? options.getMinVersions()
            : Math.max(Math.max(options.getMinVersions(),
            cfDescriptor.getMaxVersions()),1);
    }

    /**
     *
     * @param conf HBase Configuration
     * @param columnDescriptor ColumnFamilyDescriptor for the store being compacted
     * @param options ScanOptions of overrides to the compaction scan
     * @return Time to live in milliseconds, based on both HBase TTL and Phoenix max lookback age
     */
    public long getTimeToLiveForCompactions(Configuration conf,
                                                   ColumnFamilyDescriptor columnDescriptor,
                                                   ScanOptions options) {
        long ttlConfigured = columnDescriptor.getTimeToLive();
        long ttlInMillis = ttlConfigured * 1000;
        long maxLookbackTtl = getMaxLookbackInMillis(conf);
        if (isMaxLookbackTimeEnabled(maxLookbackTtl)) {
            if (ttlConfigured == HConstants.FOREVER
                && columnDescriptor.getKeepDeletedCells() != KeepDeletedCells.TRUE) {
                // If user configured default TTL(FOREVER) and keep deleted cells to false or
                // TTL then to remove unwanted delete markers we should change ttl to max lookback age
                ttlInMillis = maxLookbackTtl;
            } else {
                //if there is a TTL, use TTL instead of max lookback age.
                // Max lookback age should be more recent or equal to TTL
                ttlInMillis = Math.max(ttlInMillis, maxLookbackTtl);
            }
        }

        return ttlInMillis;
    }

    public void setScanOptionsForFlushesAndCompactions(Configuration conf,
                                                               ScanOptions options,
                                                               final Store store,
                                                               ScanType type) {
        ColumnFamilyDescriptor cfDescriptor = store.getColumnFamilyDescriptor();
        options.setTTL(getTimeToLiveForCompactions(conf, cfDescriptor,
            options));
        options.setKeepDeletedCells(getKeepDeletedCells(options, type));
        options.setMaxVersions(Integer.MAX_VALUE);
        options.setMinVersions(getMinVersions(options, cfDescriptor));
    }

    public static long getMaxLookbackInMillis(Configuration conf){
        //config param is in seconds, switch to millis
        return conf.getLong(PHOENIX_MAX_LOOKBACK_AGE_CONF_KEY,
            DEFAULT_PHOENIX_MAX_LOOKBACK_AGE) * 1000;
    }

    public static boolean isMaxLookbackTimeEnabled(Configuration conf){
        return isMaxLookbackTimeEnabled(conf.getLong(PHOENIX_MAX_LOOKBACK_AGE_CONF_KEY,
            DEFAULT_PHOENIX_MAX_LOOKBACK_AGE));
    }

    public static boolean isMaxLookbackTimeEnabled(long maxLookbackTime){
        return maxLookbackTime > 0L;
    }

}

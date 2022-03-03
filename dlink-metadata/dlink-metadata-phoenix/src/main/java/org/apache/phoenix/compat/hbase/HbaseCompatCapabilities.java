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

public class HbaseCompatCapabilities {

    public static boolean isMaxLookbackTimeSupported() {
        return true;
    }

    //In HBase 2.1 and 2.2, a lookback query won't return any results if covered by a future delete,
    //but in 2.3 and later we have the preSoreScannerOpen hook that overrides that behavior
    public static boolean isLookbackBeyondDeletesSupported() { return true; }

    //HBase 2.1 does not have HBASE-22710, which is necessary for raw scan skip scan and
    // AllVersionsIndexRebuild filters to
    // show all versions properly. HBase 2.2.5+ and HBase 2.3.0+ have this fix.
    public static boolean isRawFilterSupported() { return true; }

    //HBase 2.3+ has preWALAppend() on RegionObserver (HBASE-22623)
    public static boolean hasPreWALAppend() { return true; }

}

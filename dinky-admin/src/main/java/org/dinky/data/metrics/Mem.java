/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.dinky.data.metrics;

import org.dinky.data.annotation.GaugeM;

import cn.hutool.system.oshi.OshiUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;
import oshi.hardware.GlobalMemory;

@Getter
@Setter
@With
@AllArgsConstructor
@NoArgsConstructor
public class Mem extends BaseMetrics {
    /** 内存总量 */
    @GaugeM(name = "mem.total", description = "total memory")
    private double total;

    /** 已用内存 */
    @GaugeM(name = "mem.used", description = "used memory")
    private double used;

    /** 剩余内存 */
    @GaugeM(name = "mem.free", description = "remaining memory")
    private double free;

    public static Mem of() {
        GlobalMemory memory = OshiUtil.getMemory();
        long available = memory.getAvailable();
        long total1 = memory.getTotal();
        return new Mem().withTotal(total1).withFree(available).withUsed(total1 - available);
    }
}

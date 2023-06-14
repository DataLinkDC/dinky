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

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;

import cn.hutool.system.JvmInfo;
import cn.hutool.system.SystemUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

@Setter
@Getter
@With
@AllArgsConstructor
@NoArgsConstructor
public class Jvm extends BaseMetrics {
    /** 当前JVM占用的内存总数(b) */
    @GaugeM(
            name = "jvm.total",
            description = "The total amount of memory currently occupied by the JVM (b)")
    private long total;

    /** JVM最大可用内存总数(b) */
    @GaugeM(name = "jvm.max", description = "The total amount of JVM maximum available memory (b)")
    private long max;

    /** JVM空闲内存(b) */
    @GaugeM(name = "jvm.free", description = "JVM free memory (b)")
    private long free;
    /** 当前程序cpu使用率 */
    @GaugeM(name = "jvm.cpuUsed", baseUnit = "%", description = "Current program cpu usage")
    private Double cpuUsed;
    /** 非堆内存最大值 */
    @GaugeM(name = "jvm.nonHeapMax", description = "Maximum non-heap memory")
    private Long nonHeapMax;
    /** 非堆内存使用值 */
    @GaugeM(name = "jvm.nonHeapUsed", description = "Non-heap memory usage value")
    private Long nonHeapUsed;

    /** 堆内存最大值 */
    @GaugeM(name = "jvm.heapMax", description = "Maximum heap memory")
    private Long heapMax;
    /** 堆内存使用值 */
    @GaugeM(name = "jvm.heapUsed", description = "=heap memory usage value")
    private Long heapUsed;

    @GaugeM(name = "jvm.threadPeakCount", baseUnit = "units", description = "=thread peak count")
    private Integer threadPeakCount;

    @GaugeM(name = "jvm.threadCount", baseUnit = "units", description = "=thread count")
    private Integer threadCount;

    /** JDK版本 */
    private String version;

    /** JDK路径 */
    private String home;

    public static Jvm of() {
        MemoryUsage heapMemoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        MemoryUsage nonHeapMemoryUsage =
                ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

        JvmInfo jvmInfo = SystemUtil.getJvmInfo();
        return new Jvm()
                .withVersion(jvmInfo.getVersion())
                .withFree(SystemUtil.getFreeMemory())
                .withHome(SystemUtil.getJavaRuntimeInfo().getHomeDir())
                .withTotal(SystemUtil.getTotalMemory())
                .withMax(SystemUtil.getMaxMemory())
                .withCpuUsed(SystemUtil.getOperatingSystemMXBean().getSystemLoadAverage())
                .withHeapMax(heapMemoryUsage.getMax())
                .withHeapUsed(heapMemoryUsage.getUsed())
                .withNonHeapMax(
                        nonHeapMemoryUsage.getMax() < 0
                                ? nonHeapMemoryUsage.getUsed()
                                : nonHeapMemoryUsage.getMax())
                .withNonHeapUsed(nonHeapMemoryUsage.getUsed())
                .withThreadPeakCount(threadMXBean.getPeakThreadCount())
                .withThreadCount(threadMXBean.getThreadCount());
    }
}

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

export type MetricsDataType = {
  // content: {
  //     jvm: JVMType,
  //     cpu: CPUType,
  //     mem: MemoryType,
  // },
  content: any;
  metricsTotal: number;
  model: string;
  heartTime: Date;
};
export type JVMMetric = {
  jvm: JVMType;
  cpu: CPUType;
  mem: MemoryType;
  time: Date;
};

export type FlinkMetricsData = Record<string, Record<string, string>>;

export type JvmDataRecord = {
  cpuLastValue: number;
  heapMax: number;
  heapLastValue: number;
  nonHeapMax: number;
  nonHeapLastValue: number;
  threadPeakCount: number;
  threadCount: number;
};
type JVMType = {
  total: number;
  max: number;
  free: number;
  cpuUsed: number;
  nonHeapMax: number;
  nonHeapUsed: number;
  heapMax: number;
  heapUsed: number;
  threadPeakCount: number;
  threadCount: number;
  version: string;
  home: string;
};

type CPUType = {
  cpuNum: number;
  sys: number;
  wait: number;
  free: number;
};

type MemoryType = {
  total: number;
  used: number;
  free: number;
};

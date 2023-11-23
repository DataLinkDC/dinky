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

export type JobMetrics = {
  taskId: number;
  flinkJobId: string;
  jobName: string;
  subTaskId: string;
  metrics: string;
  url: string;
  title: string;
  layoutName: string;
  showType: string;
  showSize: string;
};

export type Task = {
  id: number;
  jid: string;
  name: string;
  type: string;
  clusterName: string;
};

export type SubTask = {
  id: string;
  name: string;
  status: string;
  parallelism: number;
};

export type ChartData = {
  time: Date;
  value: number | string;
};

export type MetricsLayout = {
  taskId: number;
  vertices: string;
  metrics: string;
  position: number;
  showType: string;
  showSize: string;
  title: string;
  layoutName: string;
};

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

export type StatusCountOverView = {
  all: number;
  initializing: number;
  running: number;
  finished: number;
  failed: number;
  canceled: number;
  restarting: number;
  created: number;
  failing: number;
  cancelling: number;
  suspended: number;
  reconciling: number;
  unknown: number;
  modelOverview: BatchStreamingOverView;
};

export type PieItem = {
  type: string;
  value: number;
};

export type BatchStreamingOverView = {
  batchJobCount: number;
  streamingJobCount: number;
};

export type ResourceOverView = {
  flinkClusterCount: number;
  flinkConfigCount: number;
  dbSourceCount: number;
  globalVarCount: number;
  alertInstanceCount: number;
  alertGroupCount: number;
  gitProjectCount: number;
};

type TaskDialectSummary = {
  jobType: string;
  jobTypeCount: number;
  rate: number;
};

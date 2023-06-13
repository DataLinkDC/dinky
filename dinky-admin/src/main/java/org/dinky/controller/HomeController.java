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

package org.dinky.controller;

import org.dinky.assertion.Asserts;
import org.dinky.data.model.HomeResource;
import org.dinky.data.model.JobModelOverview;
import org.dinky.data.model.JobStatusOverView;
import org.dinky.data.model.JobTypeOverView;
import org.dinky.data.result.Result;
import org.dinky.service.HomeService;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    /**
     * query resource overview
     *
     * @return {@link Result}<{@link HomeResource}>
     */
    @GetMapping("/getResourceOverview")
    public Result<HomeResource> getResourceOverview() {
        HomeResource resourceOverview = homeService.getResourceOverview();
        if (Asserts.isNull(resourceOverview)) {
            return Result.failed();
        }
        return Result.succeed(resourceOverview);
    }

    /**
     * query job status overview
     *
     * @return {@link Result}<{@link JobStatusOverView}>
     */
    @GetMapping("/getJobStatusOverView")
    public Result<JobStatusOverView> getJobStatusOverView() {
        JobStatusOverView jobStatusOverView = homeService.getJobStatusOverView();
        if (Asserts.isNull(jobStatusOverView)) {
            return Result.failed();
        }
        return Result.succeed(jobStatusOverView);
    }

    /**
     * query job type overview
     *
     * @return {@link Result}<{@link JobTypeOverView}>
     */
    @GetMapping("/getJobTypeOverview")
    public Result<List<JobTypeOverView>> getJobTypeOverView() {
        List<JobTypeOverView> jobTypeOverView = homeService.getJobTypeOverView();
        if (Asserts.isNull(jobTypeOverView)) {
            return Result.failed();
        }
        return Result.succeed(jobTypeOverView);
    }

    /**
     * query job model overview , eg: batch: 1, streaming: 2
     *
     * @return {@link Result}<{@link JobModelOverview}>
     */
    @GetMapping("/getJobModelOverview")
    public Result<JobModelOverview> getJobModelOverview() {
        JobModelOverview jobModelOverview = homeService.getJobModelOverview();
        if (Asserts.isNull(jobModelOverview)) {
            return Result.failed();
        }
        return Result.succeed(jobModelOverview);
    }
}

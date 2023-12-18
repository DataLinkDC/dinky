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

package org.dinky.service.impl;

import org.dinky.data.flink.config.FlinkConfigOption;
import org.dinky.data.model.Document;
import org.dinky.data.vo.CascaderVO;
import org.dinky.service.FlinkService;
import org.dinky.utils.FlinkConfigOptionsUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlinkServiceImpl implements FlinkService {

    private final DocumentServiceImpl documentService;

    @Override
    public List<CascaderVO> loadConfigOptions() {
        List<CascaderVO> dataList = new ArrayList<>();

        for (String name : FlinkConfigOptionsUtils.getConfigOptionsClass()) {
            List<FlinkConfigOption> flinkConfigOptions = FlinkConfigOptionsUtils.loadOptionsByClassName(name);
            String binlogGroup = FlinkConfigOptionsUtils.parsedBinlogGroup(name);
            List<CascaderVO> child = flinkConfigOptions.stream()
                    .map(conf -> new CascaderVO(conf.getKey()))
                    .collect(Collectors.toList());
            CascaderVO cascaderVO = new CascaderVO(binlogGroup, child);
            dataList.add(cascaderVO);
        }

        List<CascaderVO> voList = documentService.lambdaQuery().eq(Document::getCategory, "Variable").list().stream()
                .map(d -> new CascaderVO(d.getName().replace("set ", "")))
                .collect(Collectors.toList());

        CascaderVO cascaderVO = new CascaderVO();
        cascaderVO.setLabel("Custom Doc");
        cascaderVO.setChildren(voList);

        dataList.add(cascaderVO);
        return dataList;
    }
}

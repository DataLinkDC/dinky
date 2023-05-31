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

package org.dinky.utils;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * @author ZackYoung
 * @since 0.8.0
 */
@Disabled
public class GitRepositoryTests {
    // ssh-keygen -m PEM -t ecdsa
    GitRepository sshRepository =
            new GitRepository("git@gitee.com:DataLinkDC/dinky.git", null, null, "");
    GitRepository httpRepository =
            new GitRepository("https://github.com/DataLinkDC/dinky", null, null, null);

    @Test
    public void httpTest() {
        List<String> branchList1 = httpRepository.getBranchList();
        System.out.println(branchList1);
        File dinky = httpRepository.cloneAndPull("dinky", "0.7");
    }
}

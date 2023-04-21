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

package zdpx.coder.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.zdpx.coder.utils.NameHelper;

/** */
class NameHelperTest {

    @BeforeEach
    void init() {
        NameHelper.getCounter().set(0);
    }

    @ParameterizedTest
    @CsvSource({
        "BD, _BD1",
        "_BD, __BD1",
        "bd, _bd1",
        "apple, _apple1",
        "appleDescriptor, _appleDescriptor1",
        "orangedescriptor, _orangedescriptor1"
    })
    void generateName(String name, String result) {
        String nameResult = NameHelper.generateVariableName(name);
        assertEquals(nameResult, result);
    }

    @ParameterizedTest
    @CsvSource({
        "BD, _BD1Descriptor",
        "_BD, __BD1Descriptor",
        "bd, _bd1Descriptor",
        "apple, _apple1Descriptor",
        "appleDescriptor, _appleDescriptor1Descriptor",
        "orangedescriptor, _orangedescriptor1Descriptor"
    })
    void testGenerateName(String name, String result) {
        String nameResult = NameHelper.generateVariableName(name, "Descriptor");
        assertEquals(nameResult, result);
    }

    @ParameterizedTest
    @CsvSource({
        "BD, $BD1Descriptor",
        "_BD, $_BD1Descriptor",
        "bd, $bd1Descriptor",
        "apple, $apple1Descriptor",
        "appleDescriptor, $appleDescriptor1Descriptor",
        "orangedescriptor, $orangedescriptor1Descriptor"
    })
    void testGenerateName1(String name, String result) {
        String nameResult = NameHelper.generateVariableName("$", name, "Descriptor");
        assertEquals(nameResult, result);
    }
}

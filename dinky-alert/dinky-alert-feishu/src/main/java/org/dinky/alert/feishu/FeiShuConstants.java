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

package org.dinky.alert.feishu;

import org.dinky.alert.AlertBaseConstant;

/** FeiShuConstants */
public class FeiShuConstants extends AlertBaseConstant {
    /** FeiShu alert baseconstant */
    public static final String FEI_SHU_TEXT_TEMPLATE =
            "{\"msg_type\":\"{msg_type}\",\"content\":{\"{msg_type}\":\"{msg} {users} \" }}";

    public static final String FEI_SHU_POST_TEMPLATE =
            "{\"msg_type\":\"{msg_type}\",\"content\":{\"{msg_type}\":{\"zh_cn\":{\"title\":\"{keyword}\","
                    + "\"content\":[[{\"tag\":\"text\",\"un_escape\": true,\"text\":\"{msg}\"},{users}]]}}}}";
}

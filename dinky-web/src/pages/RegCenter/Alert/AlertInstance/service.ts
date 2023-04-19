/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import {l} from "@/utils/intl";
import {handleAddOrUpdate} from "@/services/BusinessCrud";
import {Alert} from "@/types/RegCenter/data.d";
import {postAll} from "@/services/api";
import {ErrorMessage, LoadingMessageAsync, SuccessMessage, WarningMessage} from "@/utils/messages";
import {RESPONSE_CODE} from "@/services/constants";

export async function createOrModifyAlertInstance(alertInstance: Alert.AlertInstance) {
  return handleAddOrUpdate("/api/alertInstance", alertInstance);
}

export async function sendTest(alertInstance: Alert.AlertInstance) {
  await LoadingMessageAsync(l("app.request.test.alert.msg"));
  try {
    const {code, msg} = await postAll("/api/alertInstance/sendTest", alertInstance);
    if (code === RESPONSE_CODE.SUCCESS) {
      SuccessMessage(msg);
      return true;
    } else {
      WarningMessage(msg);
      return false;
    }
  } catch (error) {
    ErrorMessage(l("app.request.failed"));
    return false;
  }
}

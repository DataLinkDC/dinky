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

export default {
  'app.request.error': 'Request error',
  'app.request.200': 'The server successfully returned the requested data. ',
  'app.request.201': 'New or modified data is successful. ',
  'app.request.202': 'A request has entered the background queue (asynchronous task). ',
  'app.request.204': 'Data deleted successfully. ',
  'app.request.400':
    'There was an error in the request sent, and the server did not create or modify data. ',
  'app.request.401': 'The user does not have permission (token, username, password error). ',
  'app.request.403': 'The user is authorized, but access is forbidden. ',
  'app.request.404': 'The request sent was for a record that did not exist. ',
  'app.request.405': 'The request method is not allowed. ',
  'app.request.406': 'The requested format is not available. ',
  'app.request.410':
    'The requested resource is permanently deleted and will no longer be available. ',
  'app.request.422': 'When creating an object, a validation error occurred. ',
  'app.request.500': 'An error occurred on the server, please check the server. ',
  'app.request.502': 'Gateway error. ',
  'app.request.503': 'The service is unavailable. ',
  'app.request.504': 'The gateway timed out. ',

  'app.request.failed':' The request failed, please try again ',
  'app.request.test.connection':' Testing connection ',
  'app.request.heartbeat.connection':' Detecting heartbeat ',
  'app.request.heartbeat.connection.success': 'The heartbeat of the data source is normal, and the detection time is: {time}',
  'app.request.heartbeat.connection.failed':' The heartbeat of the data source is abnormal, and the detection time is: {time} ',
  'app.request.upload.failed':' Upload failed. ',
  'app.request.update.setting.success': 'Modify the configuration successfully！',
  'app.request.test.alert.msg': 'Sending test alert message',
};
